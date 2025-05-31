package com.aventura.api.serviceImpl;

import com.aventura.api.dto.UbicacionDTO;
import com.aventura.api.entity.Ubicacion;
import com.aventura.api.entity.Usuario;
import com.aventura.api.mapper.UbicacionMapper;

import com.aventura.api.repository.UsuarioRepository;
import com.aventura.api.repository.UbicacionRepository;
import com.aventura.api.service.UbicacionService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UbicacionServiceImpl implements UbicacionService {

    @Autowired
    private UbicacionRepository ubicacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UbicacionMapper ubicacionMapper;

    @Override
    public UbicacionDTO save(UbicacionDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Ubicacion ubicacion = ubicacionMapper.toEntity(dto, usuario);
        return ubicacionMapper.toDTO(ubicacionRepository.save(ubicacion));
    }

    @Override
    public List<UbicacionDTO> findByUsuarioId(UUID usuarioId) {
        return ubicacionRepository.findByUsuario_Id(usuarioId).stream()
                .map(ubicacionMapper::toDTO)
                .collect(Collectors.toList());
    }
}
