package com.aventura.api.mapper;

import com.aventura.api.dto.LugarDTO;
import com.aventura.api.entity.Lugar;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LugarMapper {

    LugarDTO toDTO(Lugar lugar);
    Lugar toEntity(LugarDTO dto);
}
