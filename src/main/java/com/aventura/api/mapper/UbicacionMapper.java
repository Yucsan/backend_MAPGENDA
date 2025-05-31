package com.aventura.api.mapper;


import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.aventura.api.dto.UbicacionDTO;
import com.aventura.api.entity.Ubicacion;
import com.aventura.api.entity.Usuario;

@Component
public class UbicacionMapper {

    public Ubicacion toEntity(UbicacionDTO dto, Usuario usuario) {
        Ubicacion u = new Ubicacion();
        u.setId(dto.getId());
        u.setNombre(dto.getNombre());
        u.setLatitud(dto.getLatitud());
        u.setLongitud(dto.getLongitud());
        u.setTipo(dto.getTipo());
        
        if (dto.getFechaCreacion() != null) {
            u.setFechaCreacion(dto.getFechaCreacion());
        } else {
            u.setFechaCreacion(System.currentTimeMillis());
        }
        u.setUsuario(usuario);
        return u;
    }

    public UbicacionDTO toDTO(Ubicacion u) {
        return new UbicacionDTO(
            u.getId(),
            u.getNombre(),
            u.getLatitud(),
            u.getLongitud(),
            u.getTipo(),
            u.getFechaCreacion(),
            u.getUsuario().getId()
        );
    }
}