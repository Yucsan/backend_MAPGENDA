package com.aventura.api.mapper;

import com.aventura.api.dto.FavoritoDTO;
import com.aventura.api.entity.Favorito;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FavoritoMapper {
    FavoritoDTO toDTO(Favorito favorito);
    Favorito toEntity(FavoritoDTO dto);
}
