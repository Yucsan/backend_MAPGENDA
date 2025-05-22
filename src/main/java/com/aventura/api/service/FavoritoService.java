package com.aventura.api.service;

import com.aventura.api.dto.FavoritoDTO;

import java.util.List;
import java.util.UUID;

public interface FavoritoService {
    FavoritoDTO guardarFavorito(FavoritoDTO dto);
    List<FavoritoDTO> listarPorUsuario(UUID usuarioId);
    void eliminarFavorito(Long id);
}
