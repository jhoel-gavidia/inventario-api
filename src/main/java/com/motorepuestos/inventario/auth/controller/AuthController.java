package com.motorepuestos.inventario.auth.controller;

import com.motorepuestos.inventario.DTOs.Response.UsuarioResponse;
import com.motorepuestos.inventario.auth.dto.LoginRequest;
import com.motorepuestos.inventario.auth.dto.LoginResponse;
import com.motorepuestos.inventario.auth.service.AuthService;
import com.motorepuestos.inventario.auth.service.JwtService;
import com.motorepuestos.inventario.security.TokenBlacklistService;
import com.motorepuestos.inventario.service.AuditoriaService;
import com.motorepuestos.inventario.service.UsuarioService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;
    private final AuditoriaService auditoriaService;

    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        LoginResponse loginResponse = authService.login(request);

        ResponseCookie cookie = ResponseCookie.from(
                        "access_token",
                        loginResponse.getToken()
                )
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(Duration.ofHours(2))
                .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString()
        );

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        String token = extraerToken(request);

        if (token != null) {
            try {
                tokenBlacklistService.revoke(
                        jwtService.extractJti(token),
                        jwtService.extractExpiration(token).getTime()
                );

                auditoriaService.registrar(
                        "LOGOUT",
                        "AUTH",
                        null,
                        null,
                        "{\"jti\":\"" + jwtService.extractJti(token) + "\"}"
                );
            } catch (Exception ignored) {
            }
        }

        ResponseCookie cookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString()
        );

        return ResponseEntity.noContent().build();
    }

    private String extraerToken(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if ("access_token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> me(Authentication authentication) {

        UsuarioResponse usuario = usuarioService.obtenerPorUsername(authentication.getName());

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }


        return ResponseEntity.ok(usuario);
    }
}