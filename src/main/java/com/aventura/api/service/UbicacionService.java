package com.aventura.api.service;

import com.aventura.api.dto.UbicacionDTO;

import java.util.List;
import java.util.UUID;

public interface UbicacionService {
    UbicacionDTO save(UbicacionDTO dto);
    List<UbicacionDTO> findByUsuarioId(UUID usuarioId);
}