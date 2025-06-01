package com.aventura.api.controller;


import com.aventura.api.dto.RutaDTO;
import com.aventura.api.entity.Usuario;
import com.aventura.api.repository.UsuarioRepository;
import com.aventura.api.service.RutaService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rutas")
@CrossOrigin(origins = "*")
public class RutaController {
	
	
    private final RutaService rutaService;
    private final UsuarioRepository usuarioRepository;

    public RutaController(RutaService rutaService, UsuarioRepository usuarioRepository) {
        this.rutaService = rutaService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping
    public ResponseEntity<?> crearRuta(@RequestBody RutaDTO dto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String usuarioId = authentication.getName();

            Usuario usuario = usuarioRepository.findById(UUID.fromString(usuarioId))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            RutaDTO rutaCreada = rutaService.crearRuta(dto, usuario);
            return ResponseEntity.ok(rutaCreada);
        } catch (Exception e) {
            e.printStackTrace(); // 👈 imprime el error exacto en logs
            return ResponseEntity.status(500).body("❌ Error al crear ruta: " + e.getMessage());
        }
    }


    @GetMapping("/usuario")
    public List<RutaDTO> obtenerRutasDelUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuarioId = authentication.getName();
        return rutaService.listarPorUsuario(UUID.fromString(usuarioId));
    }

}
