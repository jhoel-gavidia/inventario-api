package com.motorepuestos.inventario.usuario.service.query;

import com.motorepuestos.inventario.common.exception.ResourceNotFoundException;
import com.motorepuestos.inventario.usuario.dto.UsuarioResponse;
import com.motorepuestos.inventario.usuario.entity.Rol;
import com.motorepuestos.inventario.usuario.entity.Usuario;
import com.motorepuestos.inventario.usuario.mapper.UsuarioMapper;
import com.motorepuestos.inventario.usuario.repository.UsuarioRepository;
import com.motorepuestos.inventario.usuario.repository.UsuarioSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioQueryServiceImpl implements UsuarioQueryService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Override
    public UsuarioResponse obtenerPorId(Long id) {
        return usuarioMapper.toResponse(obtenerUsuarioOrThrow(id));
    }

    @Override
    public List<UsuarioResponse> obtenerTodos(String search, Rol rol, Boolean estado) {
        Boolean estadoFiltro = estado != null ? estado : Boolean.TRUE;

        Specification<Usuario> specification = Specification
                .where(UsuarioSpecifications.conBusqueda(search))
                .and(UsuarioSpecifications.conRol(rol))
                .and(UsuarioSpecifications.conEstado(estadoFiltro));

        return usuarioRepository.findAll(specification, Sort.by(Sort.Direction.ASC, "username"))
                .stream()
                .map(usuarioMapper::toResponse)
                .toList();
    }

    @Override
    public UsuarioResponse obtenerPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .map(usuarioMapper::toResponse)
                .orElse(null);
    }

    private Usuario obtenerUsuarioOrThrow(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }
}
