package com.aventura.api.controller;

import com.aventura.api.dto.UbicacionDTO;
import com.aventura.api.entity.Usuario;
import com.aventura.api.service.UbicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ubicaciones")
public class UbicacionController {

    @Autowired
    private UbicacionService ubicacionService;

    @PostMapping
    public ResponseEntity<UbicacionDTO> guardarUbicacion(@RequestBody UbicacionDTO dto) {
        // Validación simple
        if (!dto.getTipo().equalsIgnoreCase("país") && !dto.getTipo().equalsIgnoreCase("provincia")) {
            return ResponseEntity.badRequest().body(null);
        }
        UbicacionDTO guardada = ubicacionService.save(dto);
        return ResponseEntity.ok(guardada);
    }

    @GetMapping("/usuario")
    public ResponseEntity<List<UbicacionDTO>> obtenerUbicacionesDelUsuarioActual() {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<UbicacionDTO> ubicaciones = ubicacionService.findByUsuarioId(usuario.getId());
        return ResponseEntity.ok(ubicaciones);
    }

}