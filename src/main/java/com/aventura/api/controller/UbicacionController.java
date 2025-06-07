package com.aventura.api.controller;

import com.aventura.api.dto.UbicacionDTO;
import com.aventura.api.entity.Usuario;
import com.aventura.api.service.UbicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import com.aventura.api.entity.Ubicacion;
import com.aventura.api.mapper.UbicacionMapper;
import com.aventura.api.repository.UbicacionRepository;

import java.util.List;


@RestController
@RequestMapping("/ubicaciones")
public class UbicacionController {

    @Autowired
    private UbicacionService ubicacionService;
    
    @Autowired
    private UbicacionRepository ubicacionRepository;
    
    @Autowired
    private UbicacionMapper ubicacionMapper;

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
    
    @GetMapping("/{id}")
    public ResponseEntity<UbicacionDTO> obtenerPorId(@PathVariable UUID id) {
        return ubicacionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UbicacionDTO> actualizar(@PathVariable UUID id, @RequestBody UbicacionDTO dto) {
        UbicacionDTO actualizado = ubicacionService.update(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        ubicacionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping
    public ResponseEntity<Page<UbicacionDTO>> obtenerUbicacionesPaginadas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaCreacion").descending());
        Page<Ubicacion> entidades = ubicacionRepository.findAll(pageable);

        Page<UbicacionDTO> dtoPage = entidades.map(ubicacionMapper::toDTO);
        return ResponseEntity.ok(dtoPage);
    }
    @GetMapping("/count")
    public ResponseEntity<Long> contarUbicaciones() {
        return ResponseEntity.ok(ubicacionService.count());
    }
   
}

    
    
    
    
    
    
    
    

