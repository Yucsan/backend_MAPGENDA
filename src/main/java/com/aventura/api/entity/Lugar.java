package com.aventura.api.entity;

import java.sql.Timestamp; // ✅ CORRECTO


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lugar")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Lugar {

    @Id
    private String id; // Google Place ID

    private String nombre;
    private double latitud;
    private double longitud;
    private String direccion;
    private String tipo;
    private double calificacion;

    @Column(name = "foto_url", columnDefinition = "TEXT")
    private String fotoUrl;

    @Column(name = "abierto_ahora")
    private boolean abiertoAhora;

    @Column(name = "duracion_estimada_minutos")
    private int duracionEstimadaMinutos;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    
    @Column(name = "fecha_creacion", updatable = false)
    private Timestamp fechaCreacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = new Timestamp(System.currentTimeMillis());
    }


}
