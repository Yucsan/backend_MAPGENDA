package com.aventura.api.controller;

import com.aventura.api.dto.FavoritoDTO;
import com.aventura.api.service.FavoritoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/favoritos")
@CrossOrigin(origins = "*")
public class FavoritoController {

    @Autowired
    private FavoritoService favoritoService;

    @PostMapping
    public ResponseEntity<FavoritoDTO> guardar(@RequestBody FavoritoDTO dto) {
        return ResponseEntity.ok(favoritoService.guardarFavorito(dto));
    }

    @GetMapping
    public ResponseEntity<List<FavoritoDTO>> listar(@RequestParam UUID usuarioId) {
        return ResponseEntity.ok(favoritoService.listarPorUsuario(usuarioId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        favoritoService.eliminarFavorito(id);
        return ResponseEntity.noContent().build();
    }
}
