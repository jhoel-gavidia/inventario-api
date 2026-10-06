package com.motorepuestos.inventario.auth;

import com.motorepuestos.inventario.usuario.entity.Rol;
import com.motorepuestos.inventario.usuario.entity.Usuario;
import com.motorepuestos.inventario.auditoria.repository.AuditoriaRepository;
import com.motorepuestos.inventario.usuario.repository.UsuarioRepository;
import com.motorepuestos.inventario.support.AbstractIntegrationTest;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@AutoConfigureMockMvc
class AuthSecurityIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Usuario usuario;

    @BeforeEach
    void setUp() {

        usuario = new Usuario();
        usuario.setUsername("security.test");
        usuario.setPassword(passwordEncoder.encode("password-test"));
        usuario.setRol(Rol.ADMIN);
        usuario.setEstado(true);

        usuario = usuarioRepository.save(usuario);
    }

    @AfterEach
    void tearDown() {
        auditoriaRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    void usuarioDesactivado_suTokenDebeQuedarInvalido() throws Exception {

        // Arrange
        Cookie token = login("10.0.0.1");

        usuario.setEstado(false);
        usuarioRepository.save(usuario);

        // Act & Assert
        mockMvc.perform(get("/api/v1/auth/me")
                        .cookie(token)
                        .with(ip("10.0.0.1")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenDeUsuarioRenombrado_debeDevolver401YNo500() throws Exception {

        // Arrange
        Cookie token = login("10.0.0.2");

        usuario.setUsername("security.renombrado");
        usuarioRepository.save(usuario);

        // Act & Assert
        mockMvc.perform(get("/api/v1/auth/me")
                        .cookie(token)
                        .with(ip("10.0.0.2")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void tokenInvalido_debeDevolver401YNo500() throws Exception {

        mockMvc.perform(get("/api/v1/auth/me")
                        .cookie(new Cookie("access_token", "no-es-un-jwt"))
                        .with(ip("10.0.0.3")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void sinAutenticacion_debeDevolver401ConFormatoDeError() throws Exception {

        mockMvc.perform(get("/api/v1/auth/me").with(ip("10.0.0.4")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/api/v1/auth/me"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void metodoNoSoportado_debeDevolver405YNo500() throws Exception {

        // Arrange
        Cookie token = login("10.0.0.5");

        // Act & Assert
        mockMvc.perform(patch("/api/v1/productos/1")
                        .cookie(token)
                        .with(ip("10.0.0.5")))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405));
    }

    @Test
    void cambiarPassword_conCredencialesValidas_debeActualizarElPassword() throws Exception {

        // Arrange
        Cookie token = login("10.0.0.6");

        // Act
        mockMvc.perform(put("/api/v1/auth/password")
                        .cookie(token)
                        .with(ip("10.0.0.6"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "passwordActual": "password-test",
                                  "passwordNueva": "password-nueva-123"
                                }
                                """))
                .andExpect(status().isNoContent());

        // Assert
        Usuario actualizado = usuarioRepository.findById(usuario.getId()).orElseThrow();

        assertThat(passwordEncoder.matches("password-nueva-123", actualizado.getPassword()))
                .isTrue();
    }

    @Test
    void cambiarPassword_conPasswordActualIncorrecta_debeDevolver401() throws Exception {

        // Arrange
        Cookie token = login("10.0.0.7");

        // Act & Assert
        mockMvc.perform(put("/api/v1/auth/password")
                        .cookie(token)
                        .with(ip("10.0.0.7"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "passwordActual": "incorrecta",
                                  "passwordNueva": "password-nueva-123"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void cambiarPassword_sinAutenticacion_debeDevolver401() throws Exception {

        mockMvc.perform(put("/api/v1/auth/password")
                        .with(ip("10.0.0.8"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "passwordActual": "password-test",
                                  "passwordNueva": "password-nueva-123"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void permisosInsuficientes_debeDevolver403ConFormatoDeError() throws Exception {

        // Arrange
        usuario.setRol(Rol.USER);
        usuarioRepository.save(usuario);

        Cookie token = login("10.0.0.9");

        // Act & Assert
        mockMvc.perform(delete("/api/v1/productos/1")
                        .cookie(token)
                        .with(ip("10.0.0.9")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.path").value("/api/v1/productos/1"));
    }

    private Cookie login(String remoteAddr) throws Exception {

        return mockMvc.perform(post("/api/v1/auth/login")
                        .with(ip(remoteAddr))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "security.test",
                                  "password": "password-test"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getCookie("access_token");
    }

    private static RequestPostProcessor ip(
            String remoteAddr
    ) {
        return request -> {
            request.setRemoteAddr(remoteAddr);
            return request;
        };
    }
}