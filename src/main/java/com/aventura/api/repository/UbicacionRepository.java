package com.aventura.api.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.aventura.api.entity.Ubicacion;

import java.util.List;
import java.util.UUID;

public interface UbicacionRepository extends JpaRepository<Ubicacion, UUID> {
	
    List<Ubicacion> findByUsuario_Id(UUID usuarioId);
    
    boolean existsByUsuario_IdAndLatitudAndLongitud(UUID usuarioId, double latitud, double longitud);

}