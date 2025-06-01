package com.aventura.api.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RutaDTO {

    private Long id;
    private String nombre;
    private String categoria;
    private Long ubicacionId;
    private String polylineCodificada;

    private Double origenLat;
    private Double origenLng;
    private Double destinoLat;
    private Double destinoLng;
    private String modoTransporte;

    private String lugaresIntermedios;

    private List<String> lugarIdsOrdenados;


}


