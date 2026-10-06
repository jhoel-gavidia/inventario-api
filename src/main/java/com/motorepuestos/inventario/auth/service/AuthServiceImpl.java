package com.motorepuestos.inventario.auth.service;

import com.motorepuestos.inventario.auth.dto.CambiarPasswordRequest;
import com.motorepuestos.inventario.auth.dto.LoginRequest;
import com.motorepuestos.inventario.auth.dto.LoginResponse;
import com.motorepuestos.inventario.usuario.entity.Usuario;
import com.motorepuestos.inventario.common.exception.BusinessException;
import com.motorepuestos.inventario.usuario.repository.UsuarioRepository;
import com.motorepuestos.inventario.common.security.SecurityUtils;
import com.motorepuestos.inventario.common.security.TokenBlacklistService;
import com.motorepuestos.inventario.auditoria.service.AuditoriaService;
import com.motorepuestos.inventario.common.util.JsonUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaService auditoriaService;
    private final JsonUtil jsonUtil;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtils securityUtils;

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                request.getPassword()
                        )
                );

        String token = jwtService.generateToken(
                (UserDetails) authentication.getPrincipal()
        );

        usuarioRepository.findByUsername(request.getUsername())
                .ifPresent(usuario -> registrarAuditoria(usuario, "LOGIN", token));

        return new LoginResponse(token);
    }

    @Override
    public void logout(String token) {

        if (token == null || token.isBlank()) {
            return;
        }

        Claims claims = parseClaims(token);

        if (claims == null) {
            log.info("Logout con token expirado o inválido: no hay nada que revocar");
            return;
        }

        tokenBlacklistService.revoke(
                claims.getId(),
                claims.getExpiration().getTime()
        );

        usuarioRepository.findByUsername(claims.getSubject())
                .ifPresentOrElse(
                        usuario -> registrarAuditoria(usuario, "LOGOUT", token),
                        () -> log.warn(
                                "Logout de un usuario que ya no existe: {}",
                                claims.getSubject()
                        )
                );
    }

    @Override
    @Transactional
    public void cambiarPassword(CambiarPasswordRequest request) {

        Usuario usuario = securityUtils.obtenerUsuarioAutenticado();

        if (!passwordEncoder.matches(request.getPasswordActual(), usuario.getPassword())) {
            throw new BadCredentialsException("La contraseña actual no es correcta");
        }

        if (passwordEncoder.matches(request.getPasswordNueva(), usuario.getPassword())) {
            throw new BusinessException("La nueva contraseña debe ser distinta de la actual");
        }

        usuario.setPassword(passwordEncoder.encode(request.getPasswordNueva()));
        usuarioRepository.save(usuario);

        auditoriaService.registrar(
                usuario,
                "CAMBIAR_PASSWORD",
                "AUTH",
                usuario.getId(),
                null,
                jsonUtil.convertir(Map.of("cambio", true))
        );
    }

    private Claims parseClaims(String token) {

        try {
            return jwtService.extractClaims(token);
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    private void registrarAuditoria(Usuario usuario, String accion, String token) {

        try {
            String jti = jwtService.extractJti(token);

            auditoriaService.registrar(
                    usuario,
                    accion,
                    "AUTH",
                    usuario.getId(),
                    null,
                    jsonUtil.convertir(Map.of("jti", jti))
            );
        } catch (JwtException | IllegalArgumentException e) {
            log.warn(
                    "No se pudo registrar auditoría de {} para usuario {}",
                    accion,
                    usuario.getId(),
                    e
            );
        }
    }
}
