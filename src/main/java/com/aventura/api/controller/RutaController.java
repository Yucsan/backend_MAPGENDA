package com.aventura.api.controller;

import com.aventura.api.dto.RutaDTO;
import com.aventura.api.entity.Ruta;
import com.aventura.api.entity.RutaLugar;
import com.aventura.api.entity.Usuario;
import com.aventura.api.repository.RutaLugarRepository;
import com.aventura.api.repository.RutaRepository;
import com.aventura.api.repository.UsuarioRepository;
import com.aventura.api.service.RutaService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/rutas")
@CrossOrigin(origins = "*")
public class RutaController {
	
	
    private final RutaService rutaService;
    private final UsuarioRepository usuarioRepository;
    private RutaLugarRepository rutaLugarRepository;
    private RutaRepository rutaRepository;
        
    private static final Logger log = LoggerFactory.getLogger(RutaController.class);


    public RutaController(
    			RutaService rutaService, 
    			UsuarioRepository usuarioRepository,
    			RutaLugarRepository rutaLugarRepository,
    		    RutaRepository rutaRepository
    		) {
    	
        		this.rutaService = rutaService;
        		this.usuarioRepository = usuarioRepository;
        		this.rutaLugarRepository = rutaLugarRepository;
        	    this.rutaRepository = rutaRepository;
    }
    
    @GetMapping
    public List<RutaDTO> obtenerTodasLasRutas() {
        return rutaService.listarTodas();
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
    

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRuta(@PathVariable Long id) {
        rutaLugarRepository.deleteByRutaId(id);
        rutaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RutaDTO> getRuta(@PathVariable Long id) {
        Ruta ruta = rutaRepository.findById(id).orElseThrow();
        List<RutaLugar> lugares = rutaLugarRepository.findByRutaIdOrderByOrden(id);

        RutaDTO dto = new RutaDTO();
        dto.setId(ruta.getId());
        dto.setNombre(ruta.getNombre());
        dto.setCategoria(ruta.getCategoria());
        dto.setModoTransporte(ruta.getModoTransporte());
        dto.setPolylineCodificada(ruta.getPolylineCodificada());
        dto.setOrigenLat(ruta.getOrigenLat());
        dto.setOrigenLng(ruta.getOrigenLng());
        dto.setDestinoLat(ruta.getDestinoLat());
        dto.setDestinoLng(ruta.getDestinoLng());
        dto.setUbicacionId(ruta.getUbicacionId());
        dto.setLugaresIntermedios(ruta.getLugaresIntermedios());

        dto.setLugarIdsOrdenados(
                lugares.stream()
                       .map(rl -> rl.getLugar().getId())  // <-- aquí es la clave
                       .collect(Collectors.toList())
            );
        return ResponseEntity.ok(dto);
    }
    @GetMapping("/count")
    public ResponseEntity<Long> contarRutas() {
        return ResponseEntity.ok(rutaService.count());
    }

   
}



