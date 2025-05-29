package com.aventura.api.dto;

import java.util.UUID;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LugarDTO {
    private String id;
    private String nombre;
    private double latitud;
    private double longitud;
    private String direccion;
    private String tipo;
    private double calificacion;
    private String fotoUrl;
    private boolean abiertoAhora;
    private int duracionEstimadaMinutos;
    private UUID usuarioId;

}
