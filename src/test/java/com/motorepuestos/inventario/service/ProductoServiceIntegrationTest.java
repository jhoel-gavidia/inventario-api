package com.motorepuestos.inventario.service;

import com.motorepuestos.inventario.DTOs.Request.ProductoRequest;
import com.motorepuestos.inventario.DTOs.Response.ProductoResponse;
import com.motorepuestos.inventario.entity.Categoria;
import com.motorepuestos.inventario.entity.Producto;
import com.motorepuestos.inventario.entity.Rol;
import com.motorepuestos.inventario.entity.Usuario;
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

class ProductoServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    private Categoria categoria;

    private Producto producto;

    @BeforeEach
    void setUp() {

        categoria = new Categoria();
        categoria.setNombre("Filtros");
        categoria = categoriaRepository.save(categoria);

        producto = new Producto();
        producto.setCodigo("FIL-001");
        producto.setNombre("Filtro de aceite");
        producto.setCategoria(categoria);
        producto.setPrecioCompra(new BigDecimal("10.00"));
        producto.setPrecioVenta(new BigDecimal("15.00"));
        producto.setStockActual(10);
        producto.setEstado(true);
        producto = productoRepository.save(producto);

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
    void crear_debeGuardarProducto() {

        // Arrange
        ProductoRequest request = crearProductoRequest(
                "FIL-002",
                "Filtro de aire",
                categoria.getId(),
                "8.00",
                "12.00",
                10,
                true
        );

        // Act
        ProductoResponse response = productoService.crear(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();
        assertThat(response.getCodigo()).isEqualTo("FIL-002");
        assertThat(response.getNombre()).isEqualTo("Filtro de aire");
        assertThat(response.getStockActual()).isEqualTo(10);

        Producto productoGuardado = productoRepository
                .findById(response.getId())
                .orElseThrow();

        assertThat(productoGuardado.getCodigo())
                .isEqualTo("FIL-002");

        assertThat(productoGuardado.getNombre())
                .isEqualTo("Filtro de aire");

        assertThat(productoGuardado.getCategoria().getId())
                .isEqualTo(categoria.getId());

        assertThat(productoGuardado.getPrecioCompra())
                .isEqualByComparingTo("8.00");

        assertThat(productoGuardado.getPrecioVenta())
                .isEqualByComparingTo("12.00");

        assertThat(productoGuardado.getStockActual())
                .isEqualTo(10);

        assertThat(productoGuardado.getEstado())
                .isTrue();
    }

    @Test
    void crear_conCodigoDuplicado_debeLanzarExcepcion() {

        // Arrange
        ProductoRequest request = crearProductoRequest(
                "FIL-001",
                "Otro filtro",
                categoria.getId(),
                "7.00",
                "11.00",
                0,
                true
        );

        // Act & Assert
        assertThatThrownBy(() -> productoService.crear(request))
                .isInstanceOf(ResourceConflictException.class);

        assertThat(productoRepository.findAll())
                .hasSize(1);
    }

    @Test
    void crear_conCategoriaInexistente_debeLanzarExcepcion() {

        // Arrange
        ProductoRequest request = crearProductoRequest(
                "FIL-002",
                "Filtro de aire",
                999999L,
                "8.00",
                "12.00",
                0,
                true
        );

        // Act & Assert
        assertThatThrownBy(() -> productoService.crear(request))
                .isInstanceOf(ResourceNotFoundException.class);

        assertThat(productoRepository.findAll())
                .hasSize(1);
    }

    @Test
    void obtenerPorId_debeRetornarProducto() {

        // Act
        ProductoResponse response =
                productoService.obtenerPorId(producto.getId());

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId())
                .isEqualTo(producto.getId());

        assertThat(response.getCodigo())
                .isEqualTo("FIL-001");

        assertThat(response.getNombre())
                .isEqualTo("Filtro de aceite");
    }

    @Test
    void obtenerPorId_productoInexistente_debeLanzarExcepcion() {

        assertThatThrownBy(
                () -> productoService.obtenerPorId(999999L)
        )
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void obtenerTodos_debeRetornarProductos() {

        // Arrange
        Producto segundoProducto = new Producto();
        segundoProducto.setCodigo("FIL-002");
        segundoProducto.setNombre("Filtro de aire");
        segundoProducto.setCategoria(categoria);
        segundoProducto.setPrecioCompra(new BigDecimal("8.00"));
        segundoProducto.setPrecioVenta(new BigDecimal("12.00"));
        segundoProducto.setStockActual(20);
        segundoProducto.setEstado(true);

        productoRepository.save(segundoProducto);

        // Act
        List<ProductoResponse> responses =
                productoService.obtenerTodos();

        // Assert
        assertThat(responses)
                .hasSize(2);

        assertThat(responses)
                .extracting(ProductoResponse::getCodigo)
                .containsExactlyInAnyOrder(
                        "FIL-001",
                        "FIL-002"
                );
    }

    @Test
    void actualizar_debeActualizarProducto() {

        // Arrange
        ProductoRequest request = crearProductoRequest(
                "FIL-010",
                "Filtro de aceite premium",
                categoria.getId(),
                "13.00",
                "20.00",
                0,
                true
        );

        // Act
        ProductoResponse response =
                productoService.actualizar(producto.getId(), request);

        // Assert
        assertThat(response.getId())
                .isEqualTo(producto.getId());

        assertThat(response.getCodigo())
                .isEqualTo("FIL-010");

        assertThat(response.getNombre())
                .isEqualTo("Filtro de aceite premium");

        assertThat(response.getPrecioCompra())
                .isEqualByComparingTo("13.00");

        assertThat(response.getPrecioVenta())
                .isEqualByComparingTo("20.00");

        Producto productoActualizado =
                productoRepository.findById(producto.getId())
                        .orElseThrow();

        assertThat(productoActualizado.getCodigo())
                .isEqualTo("FIL-010");

        assertThat(productoActualizado.getNombre())
                .isEqualTo("Filtro de aceite premium");

        assertThat(productoActualizado.getPrecioCompra())
                .isEqualByComparingTo("13.00");

        assertThat(productoActualizado.getPrecioVenta())
                .isEqualByComparingTo("20.00");
    }

    @Test
    void actualizar_conMismoCodigo_debeActualizarProducto() {

        // Arrange
        ProductoRequest request = crearProductoRequest(
                "FIL-001",
                "Filtro actualizado",
                categoria.getId(),
                "11.00",
                "17.00",
                0,
                true
        );

        // Act
        ProductoResponse response =
                productoService.actualizar(producto.getId(), request);

        // Assert
        assertThat(response.getCodigo())
                .isEqualTo("FIL-001");

        assertThat(response.getNombre())
                .isEqualTo("Filtro actualizado");

        assertThat(response.getPrecioCompra())
                .isEqualByComparingTo("11.00");

        assertThat(response.getPrecioVenta())
                .isEqualByComparingTo("17.00");
    }

    @Test
    void actualizar_conCodigoDuplicado_debeLanzarExcepcion() {

        // Arrange
        Producto segundoProducto = new Producto();
        segundoProducto.setCodigo("FIL-002");
        segundoProducto.setNombre("Filtro de aire");
        segundoProducto.setCategoria(categoria);
        segundoProducto.setPrecioCompra(new BigDecimal("8.00"));
        segundoProducto.setPrecioVenta(new BigDecimal("12.00"));
        segundoProducto.setStockActual(20);
        segundoProducto.setEstado(true);

        productoRepository.save(segundoProducto);

        ProductoRequest request = crearProductoRequest(
                "FIL-002",
                "Filtro actualizado",
                categoria.getId(),
                "11.00",
                "17.00",
                0,
                true
        );

        // Act & Assert
        assertThatThrownBy(
                () -> productoService.actualizar(producto.getId(), request)
        )
                .isInstanceOf(ResourceConflictException.class);

        Producto productoSinCambios =
                productoRepository.findById(producto.getId())
                        .orElseThrow();

        assertThat(productoSinCambios.getCodigo())
                .isEqualTo("FIL-001");
    }

    @Test
    void actualizar_conCategoriaInexistente_debeLanzarExcepcion() {

        // Arrange
        ProductoRequest request = crearProductoRequest(
                "FIL-010",
                "Filtro actualizado",
                999999L,
                "11.00",
                "17.00",
                0,
                true
        );

        // Act & Assert
        assertThatThrownBy(
                () -> productoService.actualizar(producto.getId(), request)
        )
                .isInstanceOf(ResourceNotFoundException.class);

        Producto productoSinCambios =
                productoRepository.findById(producto.getId())
                        .orElseThrow();

        assertThat(productoSinCambios.getCodigo())
                .isEqualTo("FIL-001");
    }

    @Test
    void actualizar_productoInexistente_debeLanzarExcepcion() {

        // Arrange
        ProductoRequest request = crearProductoRequest(
                "FIL-010",
                "Filtro actualizado",
                categoria.getId(),
                "11.00",
                "17.00",
                0,
                true
        );

        // Act & Assert
        assertThatThrownBy(
                () -> productoService.actualizar(999999L, request)
        )
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void eliminar_debeRealizarSoftDelete() {

        // Act
        productoService.eliminar(producto.getId());

        // Assert
        Producto productoActualizado =
                productoRepository.findById(producto.getId())
                        .orElseThrow();

        assertThat(productoActualizado.getEstado())
                .isFalse();

        // El registro sigue existiendo porque es soft delete
        assertThat(productoRepository.existsById(producto.getId()))
                .isTrue();
    }

    @Test
    void eliminar_productoInexistente_debeLanzarExcepcion() {

        assertThatThrownBy(
                () -> productoService.eliminar(999999L)
        )
                .isInstanceOf(ResourceNotFoundException.class);
    }

    private ProductoRequest crearProductoRequest(
            String codigo,
            String nombre,
            Long categoriaId,
            String precioCompra,
            String precioVenta,
            Integer stockInicial,
            Boolean estado
    ) {
        ProductoRequest request = new ProductoRequest();

        request.setCodigo(codigo);
        request.setNombre(nombre);
        request.setCategoriaId(categoriaId);
        request.setPrecioCompra(new BigDecimal(precioCompra));
        request.setPrecioVenta(new BigDecimal(precioVenta));
        request.setStockInicial(stockInicial);
        request.setEstado(estado);

        return request;
    }
}