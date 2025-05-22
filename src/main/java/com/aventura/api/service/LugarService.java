package com.aventura.api.service;

import com.aventura.api.dto.LugarDTO;

import java.util.List;
import java.util.Optional;

public interface LugarService {
    List<LugarDTO> findAll();
    Optional<LugarDTO> findById(String id);
    LugarDTO save(LugarDTO dto);
    void deleteById(String id);
    
    LugarDTO update(String id, LugarDTO dto);

}
