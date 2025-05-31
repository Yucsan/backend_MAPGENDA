package com.aventura.api.mapper;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

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
	        u.setFechaCreacion(
	            Instant.ofEpochMilli(dto.getFechaCreacion())
	                   .atZone(ZoneId.systemDefault())
	                   .toLocalDateTime()
	        );
	    } else {
	        u.setFechaCreacion(LocalDateTime.now());
	    }

	    u.setUsuario(usuario);
	    return u;
	}


	public UbicacionDTO toDTO(Ubicacion u) {
	    Long fechaCreacionMillis = u.getFechaCreacion() != null
	        ? u.getFechaCreacion()
	            .atZone(ZoneId.systemDefault())
	            .toInstant()
	            .toEpochMilli()
	        : null;

	    return new UbicacionDTO(
	        u.getId(),
	        u.getNombre(),
	        u.getLatitud(),
	        u.getLongitud(),
	        u.getTipo(),
	        fechaCreacionMillis,
	        u.getUsuario().getId()
	    );
	}

}