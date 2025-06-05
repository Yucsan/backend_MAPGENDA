package com.aventura.api.entity;


import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ruta_lugar")
public class RutaLugar {
	
	 	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	 	
	    @ManyToOne
	    private Ruta ruta;
	    
	    @ManyToOne
	    private Lugar lugar;
	    
	    private Integer orden; 
	    
	    
	    public RutaLugar(Ruta ruta, Lugar lugar, int orden) {
	        this.ruta = ruta;
	        this.lugar = lugar;
	        this.orden = orden;
	    }
}
