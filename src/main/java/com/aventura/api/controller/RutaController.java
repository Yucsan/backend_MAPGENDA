package com.aventura.api.controller;


import com.aventura.api.dto.RutaDTO;
import com.aventura.api.service.RutaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rutas")
@CrossOrigin(origins = "*")
public class RutaController {

    private final RutaService rutaService;

    public RutaController(RutaService rutaService) {
        this.rutaService = rutaService;
    }

    @PostMapping
    public RutaDTO crearRuta(@RequestBody RutaDTO dto) {
        return rutaService.crearRuta(dto);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<RutaDTO> obtenerRutasPorUsuario(@PathVariable UUID usuarioId) {
        return rutaService.listarPorUsuario(usuarioId);
    }
}
