package com.motorepuestos.inventario.auth;

import com.motorepuestos.inventario.entity.Auditoria;
import com.motorepuestos.inventario.entity.Rol;
import com.motorepuestos.inventario.entity.Usuario;
import com.motorepuestos.inventario.repository.AuditoriaRepository;
import com.motorepuestos.inventario.repository.UsuarioRepository;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class AuthLogoutIntegrationTest extends AbstractIntegrationTest {

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
        usuario.setUsername("logout.test");
        usuario.setPassword(passwordEncoder.encode("password-test"));
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
    void logout_debeRevocarElTokenYRechazarElAcceso() throws Exception {

        String loginBody =
                "{\"username\":\"logout.test\",\"password\":\"password-test\"}";

        Cookie accessToken = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("access_token"))
                .andReturn()
                .getResponse()
                .getCookie("access_token");

        mockMvc.perform(get("/api/v1/auth/me")
                        .cookie(accessToken))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/auth/logout")
                        .cookie(accessToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/auth/me")
                        .cookie(accessToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.path").value("/api/v1/auth/me"));
    }

    @Test
    void logout_sinCookie_noDebeFallar() throws Exception {

        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isNoContent());
    }

    @Test
    void logout_debeRegistrarAuditoria() throws Exception {

        // Arrange
        String loginBody =
                "{\"username\":\"logout.test\",\"password\":\"password-test\"}";

        Cookie accessToken = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getCookie("access_token");

        // Act
        mockMvc.perform(post("/api/v1/auth/logout")
                        .cookie(accessToken))
                .andExpect(status().isNoContent());

        // Assert
        List<Auditoria> auditorias = auditoriaRepository
                .findByEntidadAndEntidadIdOrderByFechaDesc("AUTH", usuario.getId());

        assertThat(auditorias)
                .extracting(Auditoria::getAccion)
                .contains("LOGOUT");
    }
}