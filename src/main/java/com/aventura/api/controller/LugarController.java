package com.aventura.api.controller;

import com.aventura.api.dto.LugarDTO;
import com.aventura.api.entity.Usuario;
import com.aventura.api.service.LugarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/lugares")
@CrossOrigin(origins = "*")
public class LugarController {

    @Autowired
    private LugarService lugarService;

    @GetMapping
    public List<LugarDTO> getAllLugares() {
        return lugarService.findAll();
    }
    
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<LugarDTO>> obtenerLugaresPorUsuario(@PathVariable UUID usuarioId) {
    	 return ResponseEntity.ok(lugarService.findByUsuarioId(usuarioId));
    }
 

    @GetMapping("/{id}")
    public ResponseEntity<LugarDTO> getLugarById(@PathVariable String id) {
        return lugarService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<LugarDTO> updateLugar(@PathVariable String id, @RequestBody LugarDTO dto) {
        LugarDTO actualizado = lugarService.update(id, dto);
        return ResponseEntity.ok(actualizado);
    }
    


    @PostMapping
    public ResponseEntity<LugarDTO> createLugar(@RequestBody LugarDTO dto) {
    	
    	System.out.println("📥 Recibido lugar desde Android: " + dto.getNombre());
    	
        LugarDTO creado = lugarService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLugar(@PathVariable String id) {
        lugarService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/batch")
    public ResponseEntity<?> createLugaresBatch(@RequestBody List<LugarDTO> lugares) {
        List<String> errores = new ArrayList<>();

        for (LugarDTO lugar : lugares) {
            try {
                lugarService.save(lugar);
            } catch (Exception e) {
                errores.add("❌ Error con ID " + lugar.getId() + ": " + e.getMessage());
            }
        }

        if (errores.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CREATED).body("✅ Todos los lugares subidos correctamente.");
        } else {
            return ResponseEntity.status(HttpStatus.MULTI_STATUS).body(errores);
        }
    }
    
    @GetMapping("/usuario")
    public List<LugarDTO> getLugaresDelUsuarioActual() {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return lugarService.findByUsuarioId(usuario.getId());
    }

    
    


    
    
 
    
}
