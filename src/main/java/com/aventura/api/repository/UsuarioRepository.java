package com.aventura.api.repository;

import com.aventura.api.entity.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
	
	Optional<Usuario> findByEmail(String email);
	
	@Query(value = """
		    SELECT 
		        TO_CHAR(u.fecha_registro, 'Mon') as mes, 
		        COUNT(*) as cantidad
		    FROM usuario u
		    WHERE u.fecha_registro >= CURRENT_DATE - INTERVAL '12 months'
		    GROUP BY TO_CHAR(u.fecha_registro, 'Mon'), EXTRACT(MONTH FROM u.fecha_registro)
		    ORDER BY EXTRACT(MONTH FROM u.fecha_registro)
		    """, nativeQuery = true)
		List<Object[]> contarUsuariosPorMes();



}
