package com.aventura.api.service;

import com.aventura.api.dto.LugarDTO;
import com.aventura.api.entity.Lugar;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface LugarService {
	
    List<LugarDTO> findAll();
    Optional<LugarDTO> findById(String id);
    LugarDTO save(LugarDTO dto);
    void deleteById(String id);

    LugarDTO update(String id, LugarDTO dto);
    List<LugarDTO> findByUsuarioId(UUID usuarioId);
    long count();
    
    Map<String, Long> contarPorTipo();

    Map<String, Long> contarPorMes();
    
    Optional<Lugar> findEntityById(String id);
    
    Lugar saveEntity(Lugar lugar);

    
    

  



}


