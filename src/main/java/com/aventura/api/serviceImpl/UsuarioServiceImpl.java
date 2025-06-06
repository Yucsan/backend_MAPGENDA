package com.aventura.api.serviceImpl;

import com.aventura.api.dto.UsuarioDTO;
import com.aventura.api.dto.UsuarioMesDTO;
import com.aventura.api.entity.Usuario;
import com.aventura.api.exception.ResourceNotFoundException;
import com.aventura.api.mapper.UsuarioMapper;
import com.aventura.api.repository.UsuarioRepository;
import com.aventura.api.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    
    @Autowired
    private PasswordEncoder passwordEncoder;


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

        // ✅ Encriptar si viene contraseña
        if (dto.getContrasena() != null && !dto.getContrasena().isEmpty()) {
            entity.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        }

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

        usuarioMapper.updateEntityFromDTO(dto, usuario);

        // ✅ Encriptar si se envía nueva contraseña
        if (dto.getContrasena() != null && !dto.getContrasena().isEmpty()) {
            usuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        }

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
    @Override
    public long count() {
        return usuarioRepository.count();
    }
    
    @Override
    public List<UsuarioMesDTO> obtenerEstadisticasMensuales() {
        List<Object[]> resultados = usuarioRepository.contarUsuariosPorMes();
        return resultados.stream()
            .map(obj -> new UsuarioMesDTO((String) obj[0], ((Number) obj[1]).longValue()))
            .toList();
    }



    
    
}
