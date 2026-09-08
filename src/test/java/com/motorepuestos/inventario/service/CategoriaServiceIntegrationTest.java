package com.motorepuestos.inventario.service;

import com.motorepuestos.inventario.DTOs.Request.CategoriaRequest;
import com.motorepuestos.inventario.DTOs.Response.CategoriaResponse;
import com.motorepuestos.inventario.entity.Categoria;
import com.motorepuestos.inventario.entity.Producto;
import com.motorepuestos.inventario.entity.Rol;
import com.motorepuestos.inventario.entity.Usuario;
import com.motorepuestos.inventario.exception.BusinessException;
import com.motorepuestos.inventario.exception.ResourceConflictException;
import com.motorepuestos.inventario.exception.ResourceNotFoundException;
import com.motorepuestos.inventario.repository.AuditoriaRepository;
import com.motorepuestos.inventario.repository.CategoriaRepository;
import com.motorepuestos.inventario.repository.ProductoRepository;
import com.motorepuestos.inventario.repository.UsuarioRepository;
import com.motorepuestos.inventario.support.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CategoriaServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    private Categoria categoria;

    @BeforeEach
    void setUp() {

        categoria = new Categoria();
        categoria.setNombre("Filtros");
        categoria = categoriaRepository.save(categoria);

        Usuario usuario = new Usuario();
        usuario.setUsername("usuario.test");
        usuario.setPassword("password-test");
        usuario.setRol(Rol.USER);
        usuario.setEstado(true);
        usuarioRepository.save(usuario);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "usuario.test",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                )
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        auditoriaRepository.deleteAll();
        productoRepository.deleteAll();
        categoriaRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    void crear_debeGuardarCategoria() {

        // Arrange
        CategoriaRequest request = crearCategoriaRequest("Lubricantes");

        // Act
        CategoriaResponse response =
                categoriaService.crear(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getNombre())
                .isEqualTo("Lubricantes");

        Categoria categoriaGuardada =
                categoriaRepository.findById(response.getId())
                        .orElseThrow();

        assertThat(categoriaGuardada.getNombre())
                .isEqualTo("Lubricantes");
    }

    @Test
    void crear_conNombreDuplicado_debeLanzarExcepcion() {

        // Arrange
        CategoriaRequest request =
                crearCategoriaRequest("Filtros");

        // Act & Assert
        assertThatThrownBy(
                () -> categoriaService.crear(request)
        )
                .isInstanceOf(ResourceConflictException.class);

        assertThat(categoriaRepository.findAll())
                .hasSize(1);
    }

    @Test
    void obtenerPorId_debeRetornarCategoria() {

        // Act
        CategoriaResponse response =
                categoriaService.obtenerPorId(categoria.getId());

        // Assert
        assertThat(response).isNotNull();

        assertThat(response.getId())
                .isEqualTo(categoria.getId());

        assertThat(response.getNombre())
                .isEqualTo("Filtros");
    }

    @Test
    void obtenerPorId_categoriaInexistente_debeLanzarExcepcion() {

        assertThatThrownBy(
                () -> categoriaService.obtenerPorId(999999L)
        )
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void obtenerTodos_debeRetornarCategorias() {

        // Arrange
        Categoria segundaCategoria = new Categoria();
        segundaCategoria.setNombre("Lubricantes");
        categoriaRepository.save(segundaCategoria);

        // Act
        List<CategoriaResponse> responses =
                categoriaService.obtenerTodos();

        // Assert
        assertThat(responses)
                .hasSize(2);

        assertThat(responses)
                .extracting(CategoriaResponse::getNombre)
                .containsExactlyInAnyOrder(
                        "Filtros",
                        "Lubricantes"
                );
    }

    @Test
    void actualizar_debeActualizarCategoria() {

        // Arrange
        CategoriaRequest request =
                crearCategoriaRequest("Lubricantes");

        // Act
        CategoriaResponse response =
                categoriaService.actualizar(
                        categoria.getId(),
                        request
                );

        // Assert
        assertThat(response.getId())
                .isEqualTo(categoria.getId());

        assertThat(response.getNombre())
                .isEqualTo("Lubricantes");

        Categoria categoriaActualizada =
                categoriaRepository.findById(categoria.getId())
                        .orElseThrow();

        assertThat(categoriaActualizada.getNombre())
                .isEqualTo("Lubricantes");
    }

    @Test
    void actualizar_conMismoNombre_debeActualizarCategoria() {

        // Arrange
        CategoriaRequest request =
                crearCategoriaRequest("Filtros");

        // Act
        CategoriaResponse response =
                categoriaService.actualizar(
                        categoria.getId(),
                        request
                );

        // Assert
        assertThat(response.getId())
                .isEqualTo(categoria.getId());

        assertThat(response.getNombre())
                .isEqualTo("Filtros");
    }

    @Test
    void actualizar_conNombreDuplicado_debeLanzarExcepcion() {

        // Arrange
        Categoria segundaCategoria = new Categoria();
        segundaCategoria.setNombre("Lubricantes");
        categoriaRepository.save(segundaCategoria);

        CategoriaRequest request =
                crearCategoriaRequest("Lubricantes");

        // Act & Assert
        assertThatThrownBy(
                () -> categoriaService.actualizar(
                        categoria.getId(),
                        request
                )
        )
                .isInstanceOf(ResourceConflictException.class);

        Categoria categoriaSinCambios =
                categoriaRepository.findById(categoria.getId())
                        .orElseThrow();

        assertThat(categoriaSinCambios.getNombre())
                .isEqualTo("Filtros");
    }

    @Test
    void actualizar_categoriaInexistente_debeLanzarExcepcion() {

        // Arrange
        CategoriaRequest request =
                crearCategoriaRequest("Lubricantes");

        // Act & Assert
        assertThatThrownBy(
                () -> categoriaService.actualizar(
                        999999L,
                        request
                )
        )
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void eliminar_debeEliminarCategoria() {

        // Arrange
        Long categoriaId = categoria.getId();

        // Act
        categoriaService.eliminar(categoriaId);

        // Assert
        assertThat(categoriaRepository.existsById(categoriaId))
                .isFalse();
    }

    @Test
    void eliminar_conProductosAsociados_debeLanzarExcepcion() {

        // Arrange
        Producto producto = new Producto();
        producto.setCodigo("FIL-001");
        producto.setNombre("Filtro de aceite");
        producto.setCategoria(categoria);
        producto.setPrecioCompra(new BigDecimal("10.00"));
        producto.setPrecioVenta(new BigDecimal("15.00"));
        producto.setStockActual(10);
        producto.setEstado(true);

        productoRepository.save(producto);

        // Act & Assert
        assertThatThrownBy(
                () -> categoriaService.eliminar(categoria.getId())
        )
                .isInstanceOf(BusinessException.class);

        assertThat(categoriaRepository.existsById(categoria.getId()))
                .isTrue();
    }

    private CategoriaRequest crearCategoriaRequest(String nombre) {

        CategoriaRequest request = new CategoriaRequest();
        request.setNombre(nombre);

        return request;
    }
}