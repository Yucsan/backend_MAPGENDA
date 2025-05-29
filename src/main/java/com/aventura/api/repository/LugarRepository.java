package com.aventura.api.repository;

import com.aventura.api.entity.Lugar;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LugarRepository extends JpaRepository<Lugar, String> {
	List<Lugar> findByUsuario_Id(UUID usuarioId);

	
}
