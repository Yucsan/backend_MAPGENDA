package com.aventura.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioMesDTO {
    private String mes;
    private Long cantidad;
}
