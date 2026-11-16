package com.motorepuestos.inventario.producto.application.usecase;

import com.motorepuestos.inventario.auditoria.service.AuditoriaHelper;
import com.motorepuestos.inventario.categoria.entity.Categoria;
import com.motorepuestos.inventario.categoria.service.CategoriaConsultaService;
import com.motorepuestos.inventario.producto.application.command.CrearProductoCommand;
import com.motorepuestos.inventario.producto.domain.policy.ProductoCodigoPolicy;
import com.motorepuestos.inventario.producto.domain.valueobject.CodigoProducto;
import com.motorepuestos.inventario.producto.domain.valueobject.PrecioProducto;
import com.motorepuestos.inventario.producto.domain.valueobject.StockProducto;
import com.motorepuestos.inventario.producto.dto.ProductoRequest;
import com.motorepuestos.inventario.producto.dto.ProductoResponse;
import com.motorepuestos.inventario.producto.entity.Producto;
import com.motorepuestos.inventario.producto.mapper.ProductoMapper;
import com.motorepuestos.inventario.producto.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CrearProductoUseCaseImpl implements CrearProductoUseCase {

    private final ProductoMapper productoMapper;
    private final ProductoRepository productoRepository;
    private final CategoriaConsultaService categoriaConsultaService;
    private final AuditoriaHelper auditoriaHelper;
    private final ProductoCodigoPolicy productoCodigoPolicy;

    @Override
    @Transactional
    public ProductoResponse ejecutar(CrearProductoCommand command) {
        ProductoRequest request = command.request();

        CodigoProducto codigo = productoCodigoPolicy.validarYNormalizar(request.getCodigo());
        PrecioProducto precioCompra = PrecioProducto.de(request.getPrecioCompra());
        PrecioProducto precioVenta = PrecioProducto.de(request.getPrecioVenta());
        StockProducto stockInicial = StockProducto.de(request.getStockInicial());

        productoCodigoPolicy.validarDisponibleParaCreacion(codigo);
        Categoria categoria = categoriaConsultaService.obtenerActiva(request.getCategoriaId());

        Producto producto = productoMapper.toEntity(request);
        producto.setCodigo(codigo.valor());
        producto.setPrecioCompra(precioCompra.valor());
        producto.setPrecioVenta(precioVenta.valor());
        producto.setStockActual(stockInicial.valor());
        producto.setCategoria(categoria);

        productoRepository.save(producto);

        ProductoResponse response = productoMapper.toResponse(producto);
        auditoriaHelper.registrarCreacion("CREAR", "PRODUCTO", producto.getId(), response);
        return response;
    }
}
