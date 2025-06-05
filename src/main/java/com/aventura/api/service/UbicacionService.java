package com.aventura.api.service;

import com.aventura.api.dto.UbicacionDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UbicacionService {
    UbicacionDTO save(UbicacionDTO dto);
    List<UbicacionDTO> findByUsuarioId(UUID usuarioId);
   
    Optional<UbicacionDTO> findById(UUID id);
    UbicacionDTO update(UUID id, UbicacionDTO dto);
    void deleteById(UUID id);
    Page<UbicacionDTO> findAll(Pageable pageable);
    long count();

}