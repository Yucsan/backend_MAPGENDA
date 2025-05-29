package com.aventura.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private UUID id;
    private String nombre;
    private String apellido;
    private String email;
    private String contrasena;
    private String rol;
    private String telefono;
    private String pais;
    private String ciudad;
    private String fotoPerfilUrl;
    private String direccion;
    private String descripcion;
    private Boolean verificado;
}

