package com.aventura.api.mapper;

import com.aventura.api.dto.RutaDTO;
import com.aventura.api.entity.Ruta;
import com.aventura.api.entity.Usuario;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RutaMapper {

    public Ruta toEntity(RutaDTO dto, Usuario usuario) {
        Ruta ruta = new Ruta();
        ruta.setId(dto.getId());
        ruta.setUsuario(usuario);
        ruta.setNombre(dto.getNombre());
        ruta.setCategoria(dto.getCategoria());
        ruta.setUbicacionId(dto.getUbicacionId());
        ruta.setPolylineCodificada(dto.getPolylineCodificada());

        ruta.setOrigenLat(dto.getOrigenLat());
        ruta.setOrigenLng(dto.getOrigenLng());
        ruta.setDestinoLat(dto.getDestinoLat());
        ruta.setDestinoLng(dto.getDestinoLng());
        ruta.setModoTransporte(dto.getModoTransporte());

        return ruta;
    }

    public RutaDTO toDTO(Ruta ruta, List<String> lugarIdsOrdenados) {
        return new RutaDTO(
            ruta.getId(),
            ruta.getNombre(),
            ruta.getCategoria(),
            ruta.getUbicacionId(),
            ruta.getPolylineCodificada(),
            ruta.getOrigenLat(),
            ruta.getOrigenLng(),
            ruta.getDestinoLat(),
            ruta.getDestinoLng(),
            ruta.getModoTransporte(),
            ruta.getLugaresIntermedios(),
            lugarIdsOrdenados
        );
    }

}
