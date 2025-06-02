package com.aventura.api.controller;


import com.aventura.api.dto.RutaDTO;

import com.aventura.api.entity.Usuario;
import com.aventura.api.repository.UsuarioRepository;
import com.aventura.api.service.RutaService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;



@RestController
@RequestMapping("/rutas")
@CrossOrigin(origins = "*")
public class RutaController {
	
	
    private final RutaService rutaService;
    private final UsuarioRepository usuarioRepository;
    private static final Logger log = LoggerFactory.getLogger(RutaController.class);



    public RutaController(RutaService rutaService, UsuarioRepository usuarioRepository) {
        this.rutaService = rutaService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping
    public ResponseEntity<?> crearRuta(@RequestBody RutaDTO dto) {
    	log.info("📩 Endpoint POST /api/rutas invocado");

        try {

        	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Usuario usuario = (Usuario) authentication.getPrincipal();
            log.info("🧪 Usuario autenticado: {}", usuario.getId());

            RutaDTO rutaCreada = rutaService.crearRuta(dto, usuario);
            return ResponseEntity.ok(rutaCreada);
        } catch (Exception e) {
        	 log.error("❌ Error al crear ruta", e); // ⬅ Aquí va el log útil👈 imprime el error exacto en logs
            return ResponseEntity.status(500).body("❌ Error al crear ruta: " + e.getMessage());
        }
    }


    @GetMapping("/usuario")
    public List<RutaDTO> obtenerRutasDelUsuarioActual() {
    	Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return rutaService.listarPorUsuario(usuario.getId());
    }
    
    
    
    

}
