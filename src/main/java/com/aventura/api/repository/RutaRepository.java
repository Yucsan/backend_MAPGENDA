package com.aventura.api.repository;

import com.aventura.api.entity.Ruta;
import com.aventura.api.entity.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RutaRepository extends JpaRepository<Ruta, Long> {
	
	 List<Ruta> findByUsuario(Usuario usuario);
	 List<Ruta> findByUsuarioId(UUID usuarioId);
	 Optional<Ruta> findByUsuarioAndNombre(Usuario usuario, String nombre);





}
