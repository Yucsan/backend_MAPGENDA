package com.aventura.api.repository;

import com.aventura.api.entity.Lugar;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LugarRepository extends JpaRepository<Lugar, String> {
	List<Lugar> findByUsuario_Id(UUID usuarioId);
	
	@Query("SELECT l.tipo, COUNT(l) FROM Lugar l GROUP BY l.tipo")
	List<Object[]> contarPorTipo();

	
	@Query("SELECT TO_CHAR(l.fechaCreacion, 'Mon') AS mes, COUNT(l) " +
		       "FROM Lugar l " +
		       "GROUP BY mes " +
		       "ORDER BY MIN(l.fechaCreacion)")
		List<Object[]> contarPorMes();

	
}
