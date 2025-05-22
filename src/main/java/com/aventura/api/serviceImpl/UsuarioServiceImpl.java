package com.aventura.api.serviceImpl;

import com.aventura.api.dto.UsuarioDTO;
import com.aventura.api.entity.Usuario;
import com.aventura.api.exception.ResourceNotFoundException;
import com.aventura.api.mapper.UsuarioMapper;
import com.aventura.api.repository.UsuarioRepository;
import com.aventura.api.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Override
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<Usuario> findById(UUID id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public UsuarioDTO crearUsuario(UsuarioDTO dto) {
        Usuario entity = usuarioMapper.toEntity(dto);
        entity.setFechaRegistro(new Timestamp(System.currentTimeMillis()));
        Usuario guardado = usuarioRepository.save(entity);
        return usuarioMapper.toDTO(guardado);
    }

    @Override
    public void deleteById(UUID id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario con ID " + id + " no encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    public UsuarioDTO updateUsuario(UUID id, UsuarioDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario con ID " + id + " no encontrado"));

        // Solo actualizamos campos permitidos
        usuarioMapper.updateEntityFromDTO(dto, usuario);

        Usuario actualizado = usuarioRepository.save(usuario);
        return usuarioMapper.toDTO(actualizado);
    }

    @Override
    public UsuarioDTO getById(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario con ID " + id + " no encontrado"));
        return usuarioMapper.toDTO(usuario);
    }
    
    @Override
    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    
    
}
