package com.aventura.api.dto;

import lombok.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UbicacionDTO {

    private UUID id;
    private String nombre;
    private double latitud;
    private double longitud;
    private String tipo; // Validado más adelante
    private Long fechaCreacion;
    private UUID usuarioId;
}