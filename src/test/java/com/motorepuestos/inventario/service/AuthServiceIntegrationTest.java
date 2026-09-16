package com.motorepuestos.inventario.service;

import com.motorepuestos.inventario.auth.dto.LoginRequest;
import com.motorepuestos.inventario.auth.dto.LoginResponse;
import com.motorepuestos.inventario.auth.service.AuthService;
import com.motorepuestos.inventario.auth.service.JwtService;
import com.motorepuestos.inventario.entity.Rol;
import com.motorepuestos.inventario.entity.Usuario;
import com.motorepuestos.inventario.repository.AuditoriaRepository;
import com.motorepuestos.inventario.repository.UsuarioRepository;
import com.motorepuestos.inventario.support.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {

        usuario = new Usuario();
        usuario.setUsername("usuario.test");
        usuario.setPassword(
                passwordEncoder.encode("password-test")
        );
        usuario.setRol(Rol.USER);
        usuario.setEstado(true);

        usuario = usuarioRepository.save(usuario);
    }

    @AfterEach
    void tearDown() {
        auditoriaRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    void login_conCredencialesValidas_debeRetornarToken() {

        LoginRequest request = new LoginRequest();
        request.setUsername("usuario.test");
        request.setPassword("password-test");

        LoginResponse response =
                authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken())
                .isNotBlank();

        assertThat(jwtService.extractUsername(response.getToken()))
                .isEqualTo("usuario.test");
    }

    @Test
    void login_conPasswordIncorrecta_debeLanzarExcepcion() {

        LoginRequest request = new LoginRequest();
        request.setUsername("usuario.test");
        request.setPassword("password-incorrecta");

        assertThatThrownBy(
                () -> authService.login(request)
        )
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void login_conUsuarioInexistente_debeLanzarExcepcion() {

        LoginRequest request = new LoginRequest();
        request.setUsername("usuario.inexistente");
        request.setPassword("password-test");

        assertThatThrownBy(
                () -> authService.login(request)
        )
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void login_conUsuarioInactivo_debeLanzarExcepcion() {

        usuario.setEstado(false);
        usuarioRepository.save(usuario);

        LoginRequest request = new LoginRequest();
        request.setUsername("usuario.test");
        request.setPassword("password-test");

        assertThatThrownBy(
                () -> authService.login(request)
        )
                .isInstanceOf(DisabledException.class);
    }
}