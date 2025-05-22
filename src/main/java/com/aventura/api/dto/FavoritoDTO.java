package com.aventura.api.dto;

import lombok.*;
import java.util.UUID;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoritoDTO {
    private Long id;
    private UUID usuarioId;
    private String lugarId;
    private Timestamp fechaGuardado;
}
