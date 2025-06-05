package com.aventura.api.repository;

import com.aventura.api.entity.RutaLugar;
import com.aventura.api.entity.Ruta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RutaLugarRepository extends JpaRepository<RutaLugar, Long> {
    List<RutaLugar> findByRuta(Ruta ruta);
    List<RutaLugar> findByRutaIdOrderByOrden(Long rutaId);
    void deleteByRutaId(Long rutaId);
}
