package com.aventura.api.repository;

import com.aventura.api.entity.RutaLugar;

import org.springframework.transaction.annotation.Transactional;

import com.aventura.api.entity.Ruta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;




@Repository
public interface RutaLugarRepository extends JpaRepository<RutaLugar, Long> {

    List<RutaLugar> findByRuta(Ruta ruta);

    List<RutaLugar> findByRutaIdOrderByOrden(Long rutaId);

    @Transactional
    @Modifying
    @Query("DELETE FROM RutaLugar rl WHERE rl.ruta.id = :rutaId")
    void eliminarPorRutaId(@Param("rutaId") Long rutaId); // ✅ nombre distinto y claro
    
	 void deleteByRuta(Ruta ruta);
}

