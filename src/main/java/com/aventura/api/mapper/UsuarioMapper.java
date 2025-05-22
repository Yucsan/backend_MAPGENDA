package com.aventura.api.mapper;

import com.aventura.api.dto.UsuarioDTO;
import com.aventura.api.entity.Usuario;
import org.mapstruct.*;
import org.mapstruct.ReportingPolicy;

@Mapper(
  componentModel = "spring",
  // Esto hace que MapStruct no falle por campos no mapeados
  unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UsuarioMapper {

    UsuarioDTO toDTO(Usuario usuario);

    Usuario toEntity(UsuarioDTO usuarioDTO);

    /** 
     * Actualiza un Usuario existente con datos del DTO, 
     * ignorando id, fechaRegistro y deletedAt.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntityFromDTO(UsuarioDTO dto, @MappingTarget Usuario entity);
}
