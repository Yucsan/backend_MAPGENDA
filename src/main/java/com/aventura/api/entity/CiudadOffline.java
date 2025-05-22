package com.aventura.api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ciudad_offline")
public class CiudadOffline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Usuario usuario;

    private String nombreCiudad;
    private Timestamp fechaDescarga;

    @Column(columnDefinition = "TEXT")
    private String lugaresJson;
}