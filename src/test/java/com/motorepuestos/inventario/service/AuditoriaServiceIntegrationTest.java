package com.motorepuestos.inventario.service;

import com.motorepuestos.inventario.DTOs.Request.CategoriaRequest;
import com.motorepuestos.inventario.DTOs.Response.AuditoriaResponse;
import com.motorepuestos.inventario.DTOs.Response.CategoriaResponse;
import com.motorepuestos.inventario.entity.Auditoria;
import com.motorepuestos.inventario.entity.Rol;
import com.motorepuestos.inventario.entity.Usuario;
import com.motorepuestos.inventario.exception.ResourceNotFoundException;
import com.motorepuestos.inventario.repository.AuditoriaRepository;
import com.motorepuestos.inventario.repository.CategoriaRepository;
import com.motorepuestos.inventario.repository.UsuarioRepository;
import com.motorepuestos.inventario.support.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuditoriaServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private AuditoriaService auditoriaService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setUsername("usuario.auditoria");
        usuario.setPassword("password-test");
        usuario.setRol(Rol.ADMIN);
        usuario.setEstado(true);
        usuario = usuarioRepository.save(usuario);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "usuario.auditoria",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                )
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        auditoriaRepository.deleteAll();
        categoriaRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    void listar_debeRetornarAuditoriasOrdenadasPorFechaDescendente() {

        // Arrange: auditoría antigua creada directamente
        Auditoria antigua = new Auditoria();
        antigua.setUsuario(usuario);
        antigua.setAccion("CREAR");
        antigua.setEntidad("CATEGORIA");
        antigua.setEntidadId(1L);
        antigua.setFecha(LocalDateTime.now().minusDays(1));
        auditoriaRepository.save(antigua);

        CategoriaResponse creada = categoriaService.crear(
                request("Lubricantes")
        );

        // Act
        List<AuditoriaResponse> auditorias = auditoriaService.listar();

        // Assert
        assertThat(auditorias).hasSize(2);
        assertThat(auditorias.get(0).entidadId()).isEqualTo(creada.getId());
        assertThat(auditorias.get(0).accion()).isEqualTo("CREAR");
        assertThat(auditorias.get(0).entidad()).isEqualTo("CATEGORIA");
        assertThat(auditorias.get(0).username()).isEqualTo("usuario.auditoria");
    }

    @Test
    void listarPorEntidad_debeRetornarSoloLosRegistrosDeLaEntidad() {

        // Arrange
        CategoriaResponse creada = categoriaService.crear(
                request("Lubricantes")
        );

        // Act
        List<AuditoriaResponse> auditorias =
                auditoriaService.listarPorEntidad("CATEGORIA", creada.getId());

        // Assert
        assertThat(auditorias).hasSize(1);

        AuditoriaResponse auditoria = auditorias.get(0);
        assertThat(auditoria.accion()).isEqualTo("CREAR");
        assertThat(auditoria.entidad()).isEqualTo("CATEGORIA");
        assertThat(auditoria.entidadId()).isEqualTo(creada.getId());
        assertThat(auditoria.datosAnt()).isNull();
        assertThat(auditoria.datosNew()).isNotNull();
        assertThat(auditoria.fecha()).isNotNull();
        assertThat(auditoria.usuarioId()).isEqualTo(usuario.getId());
        assertThat(auditoria.username()).isEqualTo("usuario.auditoria");
    }

    @Test
    void listarPorEntidad_sinRegistros_debeRetornarListaVacia() {

        // Act
        List<AuditoriaResponse> auditorias =
                auditoriaService.listarPorEntidad("PRODUCTO", 999999L);

        // Assert
        assertThat(auditorias).isEmpty();
    }

    @Test
    void obtenerPorId_debeRetornarAuditoria() {

        // Arrange
        CategoriaResponse creada = categoriaService.crear(
                request("Lubricantes")
        );

        AuditoriaResponse registrada =
                auditoriaService.listarPorEntidad("CATEGORIA", creada.getId())
                        .get(0);

        // Act
        AuditoriaResponse auditoria =
                auditoriaService.obtenerPorId(registrada.id());

        // Assert
        assertThat(auditoria.id()).isEqualTo(registrada.id());
        assertThat(auditoria.accion()).isEqualTo("CREAR");
        assertThat(auditoria.usuarioId()).isEqualTo(usuario.getId());
        assertThat(auditoria.username()).isEqualTo("usuario.auditoria");
    }

    @Test
    void obtenerPorId_auditoriaInexistente_debeLanzarExcepcion() {

        assertThatThrownBy(() -> auditoriaService.obtenerPorId(999999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    private CategoriaRequest request(String nombre) {
        CategoriaRequest request = new CategoriaRequest();
        request.setNombre(nombre);
        return request;
    }
}