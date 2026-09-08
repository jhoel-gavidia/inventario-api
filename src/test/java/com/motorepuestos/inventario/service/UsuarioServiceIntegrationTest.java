package com.motorepuestos.inventario.service;

import com.motorepuestos.inventario.DTOs.Request.UsuarioRequest;
import com.motorepuestos.inventario.DTOs.Request.UsuarioUpdateRequest;
import com.motorepuestos.inventario.DTOs.Response.UsuarioResponse;
import com.motorepuestos.inventario.entity.Auditoria;
import com.motorepuestos.inventario.entity.Rol;
import com.motorepuestos.inventario.entity.Usuario;
import com.motorepuestos.inventario.exception.ResourceConflictException;
import com.motorepuestos.inventario.exception.ResourceNotFoundException;
import com.motorepuestos.inventario.repository.AuditoriaRepository;
import com.motorepuestos.inventario.repository.UsuarioRepository;
import com.motorepuestos.inventario.support.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsuarioServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private UsuarioService usuarioService;

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
        usuario.setUsername("usuario.test");
        usuario.setPassword(
                passwordEncoder.encode("password-test")
        );
        usuario.setRol(Rol.USER);
        usuario.setEstado(true);

        usuario = usuarioRepository.save(usuario);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        usuario.getUsername(),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
    }

    @AfterEach
    void tearDown() {

        SecurityContextHolder.clearContext();

        auditoriaRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    void crear_debeGuardarUsuario() {

        UsuarioRequest request = new UsuarioRequest();
        request.setUsername("nuevo.usuario");
        request.setPassword("password-nuevo");
        request.setRol(Rol.USER);
        request.setEstado(true);

        UsuarioResponse response =
                usuarioService.crear(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getUsername())
                .isEqualTo("nuevo.usuario");
        assertThat(response.getRol())
                .isEqualTo(Rol.USER);
        assertThat(response.getEstado())
                .isTrue();

        Usuario usuarioGuardado =
                usuarioRepository.findById(response.getId())
                        .orElseThrow();

        assertThat(usuarioGuardado.getUsername())
                .isEqualTo("nuevo.usuario");
    }

    @Test
    void crear_debeEncriptarPassword() {

        UsuarioRequest request = new UsuarioRequest();
        request.setUsername("usuario.password");
        request.setPassword("password-secreta");
        request.setRol(Rol.USER);
        request.setEstado(true);

        UsuarioResponse response =
                usuarioService.crear(request);

        Usuario usuarioGuardado =
                usuarioRepository.findById(response.getId())
                        .orElseThrow();

        assertThat(usuarioGuardado.getPassword())
                .isNotEqualTo("password-secreta");

        assertThat(
                passwordEncoder.matches(
                        "password-secreta",
                        usuarioGuardado.getPassword()
                )
        ).isTrue();
    }

    @Test
    void crear_conUsernameDuplicado_debeLanzarExcepcion() {

        UsuarioRequest request = new UsuarioRequest();
        request.setUsername("usuario.test");
        request.setPassword("password-nuevo");
        request.setRol(Rol.USER);
        request.setEstado(true);

        assertThatThrownBy(
                () -> usuarioService.crear(request)
        )
                .isInstanceOf(ResourceConflictException.class)
                .hasMessage("El username ya existe");
    }

    @Test
    void obtenerPorId_debeRetornarUsuario() {

        UsuarioResponse response =
                usuarioService.obtenerPorId(usuario.getId());

        assertThat(response).isNotNull();
        assertThat(response.getId())
                .isEqualTo(usuario.getId());
        assertThat(response.getUsername())
                .isEqualTo("usuario.test");
        assertThat(response.getRol())
                .isEqualTo(Rol.USER);
        assertThat(response.getEstado())
                .isTrue();
    }

    @Test
    void obtenerPorId_usuarioInexistente_debeLanzarExcepcion() {

        Long idInexistente = 999999L;

        assertThatThrownBy(
                () -> usuarioService.obtenerPorId(idInexistente)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Usuario no encontrado");
    }

    @Test
    void obtenerTodos_debeRetornarUsuarios() {

        Usuario segundoUsuario = new Usuario();
        segundoUsuario.setUsername("usuario.dos");
        segundoUsuario.setPassword(
                passwordEncoder.encode("password-dos")
        );
        segundoUsuario.setRol(Rol.ADMIN);
        segundoUsuario.setEstado(true);

        usuarioRepository.save(segundoUsuario);

        List<UsuarioResponse> usuarios =
                usuarioService.obtenerTodos();

        assertThat(usuarios)
                .hasSize(2)
                .extracting(UsuarioResponse::getUsername)
                .containsExactlyInAnyOrder(
                        "usuario.test",
                        "usuario.dos"
                );
    }

    @Test
    void actualizar_debeActualizarUsuario() {

        UsuarioUpdateRequest request =
                new UsuarioUpdateRequest();

        request.setUsername("usuario.actualizado");
        request.setRol(Rol.ADMIN);
        request.setEstado(false);

        UsuarioResponse response =
                usuarioService.actualizar(
                        usuario.getId(),
                        request
                );

        assertThat(response).isNotNull();
        assertThat(response.getUsername())
                .isEqualTo("usuario.actualizado");
        assertThat(response.getRol())
                .isEqualTo(Rol.ADMIN);
        assertThat(response.getEstado())
                .isFalse();

        Usuario usuarioActualizado =
                usuarioRepository.findById(usuario.getId())
                        .orElseThrow();

        assertThat(usuarioActualizado.getUsername())
                .isEqualTo("usuario.actualizado");
        assertThat(usuarioActualizado.getRol())
                .isEqualTo(Rol.ADMIN);
        assertThat(usuarioActualizado.getEstado())
                .isFalse();
    }

    @Test
    void actualizar_conMismoUsername_debeActualizarUsuario() {

        UsuarioUpdateRequest request =
                new UsuarioUpdateRequest();

        request.setUsername("usuario.test");
        request.setRol(Rol.ADMIN);
        request.setEstado(true);

        UsuarioResponse response =
                usuarioService.actualizar(
                        usuario.getId(),
                        request
                );

        assertThat(response.getUsername())
                .isEqualTo("usuario.test");
        assertThat(response.getRol())
                .isEqualTo(Rol.ADMIN);
        assertThat(response.getEstado())
                .isTrue();
    }

    @Test
    void actualizar_conUsernameDuplicado_debeLanzarExcepcion() {

        Usuario segundoUsuario = new Usuario();
        segundoUsuario.setUsername("usuario.dos");
        segundoUsuario.setPassword(
                passwordEncoder.encode("password-dos")
        );
        segundoUsuario.setRol(Rol.USER);
        segundoUsuario.setEstado(true);

        usuarioRepository.save(segundoUsuario);

        UsuarioUpdateRequest request =
                new UsuarioUpdateRequest();

        request.setUsername("usuario.dos");
        request.setRol(Rol.ADMIN);
        request.setEstado(true);

        assertThatThrownBy(
                () -> usuarioService.actualizar(
                        usuario.getId(),
                        request
                )
        )
                .isInstanceOf(ResourceConflictException.class)
                .hasMessage("El username ya existe");
    }

    @Test
    void actualizar_usuarioInexistente_debeLanzarExcepcion() {

        UsuarioUpdateRequest request =
                new UsuarioUpdateRequest();

        request.setUsername("usuario.actualizado");
        request.setRol(Rol.ADMIN);
        request.setEstado(true);

        assertThatThrownBy(
                () -> usuarioService.actualizar(
                        999999L,
                        request
                )
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Usuario no encontrado");
    }

    @Test
    void eliminar_debeRealizarSoftDelete() {

        usuarioService.eliminar(usuario.getId());

        Usuario usuarioEliminado =
                usuarioRepository.findById(usuario.getId())
                        .orElseThrow();

        assertThat(usuarioEliminado.getEstado())
                .isFalse();
    }

    @Test
    void eliminar_usuarioInexistente_debeLanzarExcepcion() {

        assertThatThrownBy(
                () -> usuarioService.eliminar(999999L)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Usuario no encontrado");
    }

    @Test
    void crear_debeRegistrarAuditoria() {

        UsuarioRequest request = new UsuarioRequest();
        request.setUsername("usuario.auditoria");
        request.setPassword("password-auditoria");
        request.setRol(Rol.USER);
        request.setEstado(true);

        UsuarioResponse response =
                usuarioService.crear(request);

        List<Auditoria> auditorias =
                auditoriaRepository.findAll();

        assertThat(auditorias).hasSize(1);

        Auditoria auditoria = auditorias.get(0);

        assertThat(auditoria.getAccion())
                .isEqualTo("CREAR");
        assertThat(auditoria.getEntidad())
                .isEqualTo("USUARIO");
        assertThat(auditoria.getEntidadId())
                .isEqualTo(response.getId());
        assertThat(auditoria.getDatosAnt())
                .isNull();
        assertThat(auditoria.getDatosNew())
                .isNotBlank();
        assertThat(auditoria.getFecha())
                .isNotNull();
        assertThat(auditoria.getUsuario().getId())
                .isEqualTo(usuario.getId());
    }

    @Test
    void actualizar_debeRegistrarAuditoria() {

        UsuarioUpdateRequest request =
                new UsuarioUpdateRequest();

        request.setUsername("usuario.actualizado");
        request.setRol(Rol.ADMIN);
        request.setEstado(false);

        usuarioService.actualizar(
                usuario.getId(),
                request
        );

        List<Auditoria> auditorias =
                auditoriaRepository.findAll();

        assertThat(auditorias).hasSize(1);

        Auditoria auditoria = auditorias.get(0);

        assertThat(auditoria.getAccion())
                .isEqualTo("ACTUALIZAR");
        assertThat(auditoria.getEntidad())
                .isEqualTo("USUARIO");
        assertThat(auditoria.getEntidadId())
                .isEqualTo(usuario.getId());
        assertThat(auditoria.getDatosAnt())
                .isNotBlank();
        assertThat(auditoria.getDatosNew())
                .isNotBlank();
        assertThat(auditoria.getFecha())
                .isNotNull();
    }

    @Test
    void eliminar_debeRegistrarAuditoria() {

        usuarioService.eliminar(usuario.getId());

        List<Auditoria> auditorias =
                auditoriaRepository.findAll();

        assertThat(auditorias).hasSize(1);

        Auditoria auditoria = auditorias.get(0);

        assertThat(auditoria.getAccion())
                .isEqualTo("ELIMINAR");
        assertThat(auditoria.getEntidad())
                .isEqualTo("USUARIO");
        assertThat(auditoria.getEntidadId())
                .isEqualTo(usuario.getId());
        assertThat(auditoria.getDatosAnt())
                .isNotBlank();
        assertThat(auditoria.getDatosNew())
                .isNotBlank();
        assertThat(auditoria.getFecha())
                .isNotNull();
    }
}