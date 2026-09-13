package com.motorepuestos.inventario.auth.controller;

import com.motorepuestos.inventario.DTOs.Request.UsuarioRequest;
import com.motorepuestos.inventario.DTOs.Response.UsuarioResponse;
import com.motorepuestos.inventario.auth.dto.LoginRequest;
import com.motorepuestos.inventario.auth.dto.LoginResponse;
import com.motorepuestos.inventario.auth.service.AuthService;
import com.motorepuestos.inventario.service.UsuarioService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${ADMIN_BOOTSTRAP_TOKEN}")
    private String adminBootstrapToken;

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
                .secure(false)
                .sameSite("Lax")
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
    public ResponseEntity<Void> logout(HttpServletResponse response) {

        ResponseCookie cookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString()
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<Void> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/bootstrap")
    public ResponseEntity<UsuarioResponse> bootstrap(
            @RequestHeader("X-Bootstrap-Token") String token,
            @RequestBody UsuarioRequest request
    ) {

        if (!adminBootstrapToken.equals(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UsuarioResponse response =
                usuarioService.crearAdminInicial(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}