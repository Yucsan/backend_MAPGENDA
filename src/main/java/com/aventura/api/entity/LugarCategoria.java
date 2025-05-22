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
@Table(name = "lugar_categoria")
@IdClass(LugarCategoriaId.class)
public class LugarCategoria {

    @Id
    @ManyToOne
    private Lugar lugar;

    @Id
    @ManyToOne
    private Categoria categoria;
}