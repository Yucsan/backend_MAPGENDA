package com.aventura.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue
    private UUID id;

    private String nombre;
    private String apellido; // 👈 nuevo

    private String email;
    @Column(nullable = true)
    private String contrasena;

    @Column(name = "fecha_registro")
    private Timestamp fechaRegistro;

    @Column(columnDefinition = "TEXT")
    private String preferencias;

    private String rol;

    private String telefono;   // 👈 nuevo
    private String pais;       // 👈 nuevo
    private String ciudad;     // 👈 nuevo
    private String direccion;  // 👈 nuevo

    @Column(columnDefinition = "TEXT")
    private String descripcion; // 👈 nuevo

    private Boolean verificado = false; // 👈 nuevo

    @Column(name = "deleted_at")
    private Timestamp deletedAt; // 👈 soft delete
}
