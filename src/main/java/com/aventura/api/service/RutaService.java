package com.aventura.api.service;

import com.aventura.api.dto.RutaDTO;
import com.aventura.api.entity.Ruta;
import com.aventura.api.entity.Usuario;

import java.util.List;
import java.util.UUID;

public interface RutaService {
    RutaDTO crearRuta(RutaDTO dto, Usuario usuario);
    List<RutaDTO> listarPorUsuario(UUID usuarioId);
    Ruta guardarRuta(RutaDTO dto);
    List<RutaDTO> listarTodas();
    long count();



}
