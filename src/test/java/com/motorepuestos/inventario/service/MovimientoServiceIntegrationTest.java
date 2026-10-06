package com.motorepuestos.inventario.service;

import com.motorepuestos.inventario.DTOs.Request.DetalleMovimientoRequest;
import com.motorepuestos.inventario.DTOs.Request.MovimientoRequest;
import com.motorepuestos.inventario.DTOs.Response.MovimientoResponse;
import com.motorepuestos.inventario.entity.*;
import com.motorepuestos.inventario.exception.BusinessException;
import com.motorepuestos.inventario.repository.AuditoriaRepository;
import com.motorepuestos.inventario.repository.CategoriaRepository;
import com.motorepuestos.inventario.repository.MovimientoRepository;
import com.motorepuestos.inventario.repository.ProductoRepository;
import com.motorepuestos.inventario.repository.UsuarioRepository;
import com.motorepuestos.inventario.support.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class MovimientoServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MovimientoService movimientoService;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Producto producto;

    @BeforeEach
    void setUp() {
        Categoria categoria = new Categoria();
        categoria.setNombre("Filtros");
        categoria.setEstado(true);
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
        movimientoRepository.deleteAll();
        auditoriaRepository.deleteAll();
        productoRepository.deleteAll();
        categoriaRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    void registrarEntrada_debeIncrementarStock() {
        // Arrange
        DetalleMovimientoRequest detalle = new DetalleMovimientoRequest();
        detalle.setProductoId(producto.getId());
        detalle.setCantidad(5);

        MovimientoRequest request = new MovimientoRequest();
        request.setTipo(Tipo.ENTRADA);
        request.setDetalles(List.of(detalle));

        // Act
        movimientoService.registrar(request);

        // Assert
        Producto productoActualizado =
                productoRepository.findById(producto.getId())
                        .orElseThrow();

        assertThat(productoActualizado.getStockActual())
                .isEqualTo(15);
    }

    @Test
    void registrarSalida_debeDismiuirStock() {
        //Arannge
        DetalleMovimientoRequest detalle = new DetalleMovimientoRequest();
        detalle.setProductoId(producto.getId());
        detalle.setCantidad(5);

        MovimientoRequest request = new MovimientoRequest();
        request.setTipo(Tipo.SALIDA);
        request.setDetalles(List.of(detalle));

        //Act
        movimientoService.registrar(request);

        //Assert
        Producto productoActualizado =
                productoRepository.findById(producto.getId())
                        .orElseThrow();

        assertThat(productoActualizado.getStockActual()).isEqualTo(5);
    }

    @Test
    void registrarSalida_sinStockSuficiente_debeRollback() {
        DetalleMovimientoRequest detalle = new DetalleMovimientoRequest();
        detalle.setProductoId(producto.getId());
        detalle.setCantidad(15);

        MovimientoRequest request = new MovimientoRequest();
        request.setTipo(Tipo.SALIDA);
        request.setDetalles(List.of(detalle));

        assertThatThrownBy(() -> movimientoService.registrar(request))
                .isInstanceOf(BusinessException.class);

        Producto productoActualizado =
                productoRepository.findById(producto.getId())
                        .orElseThrow();

        assertThat(productoActualizado.getStockActual()).isEqualTo(10);

        assertThat(movimientoRepository.findAll()).isEmpty();
    }

    @Test
    void registrarMovimiento_conVariosDetalles_debeActualizarStock() {
        Producto producto2 = new Producto();
        producto2.setCodigo("FIL-002");
        producto2.setNombre("Filtro de aire");
        producto2.setCategoria(producto.getCategoria());
        producto2.setPrecioCompra(new BigDecimal("8.00"));
        producto2.setPrecioVenta(new BigDecimal("12.00"));
        producto2.setStockActual(20);
        producto2.setEstado(true);
        producto2 = productoRepository.save(producto2);

        DetalleMovimientoRequest detalle1 = new DetalleMovimientoRequest();
        detalle1.setProductoId(producto.getId());
        detalle1.setCantidad(5);

        DetalleMovimientoRequest detalle2 = new DetalleMovimientoRequest();
        detalle2.setProductoId(producto2.getId());
        detalle2.setCantidad(3);

        MovimientoRequest request = new MovimientoRequest();
        request.setTipo(Tipo.ENTRADA);
        request.setDetalles(List.of(detalle1, detalle2));

        movimientoService.registrar(request);

        Producto productoActualizado =
                productoRepository.findById(producto.getId()).orElseThrow();
        Producto producto2Actualizado =
                productoRepository.findById(producto2.getId()).orElseThrow();

        assertThat(productoActualizado.getStockActual()).isEqualTo(15);
        assertThat(producto2Actualizado.getStockActual()).isEqualTo(23);
    }

    @Test
    void registrarMovimiento_debeRegistrarAuditoria() {

        // Arrange
        DetalleMovimientoRequest detalle = new DetalleMovimientoRequest();
        detalle.setProductoId(producto.getId());
        detalle.setCantidad(5);

        MovimientoRequest request = new MovimientoRequest();
        request.setTipo(Tipo.ENTRADA);
        request.setDetalles(List.of(detalle));

        // Act
        MovimientoResponse response = movimientoService.registrar(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isNotNull();

        List<Auditoria> auditorias = auditoriaRepository
                .findByEntidadAndEntidadIdOrderByFechaDesc("MOVIMIENTO", response.getId());

        assertThat(auditorias).hasSize(1);

        Auditoria auditoria = auditorias.get(0);

        assertThat(auditoria.getAccion()).isEqualTo("REGISTRAR");
        assertThat(auditoria.getEntidad()).isEqualTo("MOVIMIENTO");
        assertThat(auditoria.getEntidadId()).isEqualTo(response.getId());
        assertThat(auditoria.getDatosAnt()).isNull();
        assertThat(auditoria.getDatosNew()).isNotNull();
        assertThat(auditoria.getFecha()).isNotNull();
        assertThat(auditoria.getUsuario().getUsername()).isEqualTo("usuario.test");
    }

    @Test
    void registrar_conProductoInactivo_debeLanzarExcepcion() {

        // Arrange
        producto.setEstado(false);
        productoRepository.save(producto);

        DetalleMovimientoRequest detalle = new DetalleMovimientoRequest();
        detalle.setProductoId(producto.getId());
        detalle.setCantidad(1);

        MovimientoRequest request = new MovimientoRequest();
        request.setTipo(Tipo.ENTRADA);
        request.setDetalles(List.of(detalle));

        // Act & Assert
        assertThatThrownBy(() -> movimientoService.registrar(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("inactivo");

        Producto sinCambios = productoRepository.findById(producto.getId()).orElseThrow();

        assertThat(sinCambios.getStockActual()).isEqualTo(10);
        assertThat(movimientoRepository.findAll()).isEmpty();
    }

    @Test
    void registrar_cantidadCero_debeLanzarExcepcion() {

        // Arrange
        DetalleMovimientoRequest detalle = new DetalleMovimientoRequest();
        detalle.setProductoId(producto.getId());
        detalle.setCantidad(0);

        MovimientoRequest request = new MovimientoRequest();
        request.setTipo(Tipo.ENTRADA);
        request.setDetalles(List.of(detalle));

        // Act & Assert
        assertThatThrownBy(() -> movimientoService.registrar(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("mayor que cero");

        assertThat(productoRepository.findById(producto.getId()).orElseThrow().getStockActual())
                .isEqualTo(10);
        assertThat(movimientoRepository.findAll()).isEmpty();
    }

    @Test
    void registrar_cantidadNula_debeLanzarExcepcion() {

        // Arrange
        DetalleMovimientoRequest detalle = new DetalleMovimientoRequest();
        detalle.setProductoId(producto.getId());
        detalle.setCantidad(null);

        MovimientoRequest request = new MovimientoRequest();
        request.setTipo(Tipo.ENTRADA);
        request.setDetalles(List.of(detalle));

        // Act & Assert
        assertThatThrownBy(() -> movimientoService.registrar(request))
                .isInstanceOf(BusinessException.class);

        assertThat(movimientoRepository.findAll()).isEmpty();
    }

    @Test
    void registrar_conDetallesEnOrdenInverso_debeAplicarElMismoStock() {

        // Arrange
        Producto producto2 = new Producto();
        producto2.setCodigo("FIL-002");
        producto2.setNombre("Filtro de aire");
        producto2.setCategoria(producto.getCategoria());
        producto2.setPrecioCompra(new BigDecimal("8.00"));
        producto2.setPrecioVenta(new BigDecimal("12.00"));
        producto2.setStockActual(20);
        producto2.setEstado(true);
        producto2 = productoRepository.save(producto2);

        DetalleMovimientoRequest detalleProducto1 = new DetalleMovimientoRequest();
        detalleProducto1.setProductoId(producto.getId());
        detalleProducto1.setCantidad(5);

        DetalleMovimientoRequest detalleProducto2 = new DetalleMovimientoRequest();
        detalleProducto2.setProductoId(producto2.getId());
        detalleProducto2.setCantidad(3);

        // Act: el mismo conjunto de productos en orden inverso
        MovimientoRequest request = new MovimientoRequest();
        request.setTipo(Tipo.ENTRADA);
        request.setDetalles(List.of(detalleProducto2, detalleProducto1));

        movimientoService.registrar(request);

        // Assert
        assertThat(productoRepository.findById(producto.getId()).orElseThrow().getStockActual())
                .isEqualTo(15);
        assertThat(productoRepository.findById(producto2.getId()).orElseThrow().getStockActual())
                .isEqualTo(23);
    }

    @Test
    void registrar_entradaQueDesbordariaElStock_debeLanzarExcepcion() {

        // Arrange
        producto.setStockActual(Integer.MAX_VALUE - 1);
        productoRepository.save(producto);

        DetalleMovimientoRequest detalle = new DetalleMovimientoRequest();
        detalle.setProductoId(producto.getId());
        detalle.setCantidad(100);

        MovimientoRequest request = new MovimientoRequest();
        request.setTipo(Tipo.ENTRADA);
        request.setDetalles(List.of(detalle));

        // Act & Assert
        assertThatThrownBy(() -> movimientoService.registrar(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("límite");

        assertThat(movimientoRepository.findAll()).isEmpty();
    }

    @Test
    void registrar_tipoDesconocido_noDebeDescontarStock() {

        // Arrange
        DetalleMovimientoRequest detalle = new DetalleMovimientoRequest();
        detalle.setProductoId(producto.getId());
        detalle.setCantidad(5);

        MovimientoRequest request = new MovimientoRequest();
        request.setTipo(null);
        request.setDetalles(List.of(detalle));

        // Act & Assert
        assertThatThrownBy(() -> movimientoService.registrar(request))
                .isInstanceOf(BusinessException.class);

        assertThat(productoRepository.findById(producto.getId()).orElseThrow().getStockActual())
                .isEqualTo(10);
        assertThat(movimientoRepository.findAll()).isEmpty();
    }

    @Test
    void registrarSalida_exactamenteElStock_debeDejarEnCero() {

        // Arrange
        DetalleMovimientoRequest detalle = new DetalleMovimientoRequest();
        detalle.setProductoId(producto.getId());
        detalle.setCantidad(10);

        MovimientoRequest request = new MovimientoRequest();
        request.setTipo(Tipo.SALIDA);
        request.setDetalles(List.of(detalle));

        // Act
        movimientoService.registrar(request);

        // Assert
        assertThat(productoRepository.findById(producto.getId()).orElseThrow().getStockActual())
                .isZero();
    }

    @Test
    void obtenerTodos_debeRetornarTodosLosMovimientos() {
        // Arrange
        DetalleMovimientoRequest detalle1 = new DetalleMovimientoRequest();
        detalle1.setProductoId(producto.getId());
        detalle1.setCantidad(2);

        MovimientoRequest entrada = new MovimientoRequest();
        entrada.setTipo(Tipo.ENTRADA);
        entrada.setDetalles(List.of(detalle1));
        movimientoService.registrar(entrada);

        DetalleMovimientoRequest detalle2 = new DetalleMovimientoRequest();
        detalle2.setProductoId(producto.getId());
        detalle2.setCantidad(3);

        MovimientoRequest salida = new MovimientoRequest();
        salida.setTipo(Tipo.SALIDA);
        salida.setDetalles(List.of(detalle2));
        movimientoService.registrar(salida);

        // Act
        List<MovimientoResponse> movimientos = movimientoService.obtenerTodos();

        // Assert
        assertThat(movimientos).hasSize(2);
        assertThat(movimientos.get(0).getTipo()).isEqualTo(Tipo.ENTRADA);
        assertThat(movimientos.get(1).getTipo()).isEqualTo(Tipo.SALIDA);
    }
}

