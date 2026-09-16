package com.motorepuestos.inventario.auth.service;

import com.motorepuestos.inventario.auth.dto.LoginRequest;
import com.motorepuestos.inventario.auth.dto.LoginResponse;
import com.motorepuestos.inventario.auth.service.AuthService;
import com.motorepuestos.inventario.auth.service.JwtService;
import com.motorepuestos.inventario.entity.Usuario;
import com.motorepuestos.inventario.repository.UsuarioRepository;
import com.motorepuestos.inventario.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaService auditoriaService;
    private final ObjectMapper objectMapper;

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Override
    public LoginResponse login(LoginRequest request) {

            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getUsername(),
                                    request.getPassword()
                            )
                    );

            String token = jwtService.generateToken(
                    (org.springframework.security.core.userdetails.UserDetails)
                            authentication.getPrincipal()
            );

            usuarioRepository.findByUsername(request.getUsername())
                    .ifPresent(usuario -> registrarAuditoriaLogin(usuario, token));

            return new LoginResponse(token);
    }

    private void registrarAuditoriaLogin(Usuario usuario, String token) {
        try {
            String jti = jwtService.extractJti(token);
            String datosNewJson = objectMapper.writeValueAsString(java.util.Map.of("jti", jti));
            auditoriaService.registrar(usuario, "LOGIN", "AUTH", usuario.getId(),
                    null, datosNewJson);

        } catch (Exception e) {
            log.warn("No se pudo registrar auditoría de login para usuario {}", usuario.getId(), e);
        }
    }
}