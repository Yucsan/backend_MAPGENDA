package com.aventura.api.controller;

import com.aventura.api.dto.LugarDTO;
import com.aventura.api.entity.Lugar;
import com.aventura.api.entity.Usuario;
import com.aventura.api.security.JwtTokenProvider;
import com.aventura.api.service.LugarService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/lugares")
@CrossOrigin(origins = "*")
public class LugarController {

    @Autowired
    private LugarService lugarService;
    private final JwtTokenProvider tokenProvider;

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
    
    @PutMapping("/{id}/actualizar-foto")
    public ResponseEntity<?> actualizarFotoLugar(@PathVariable String id,
                                                 @RequestBody Map<String, String> body,
                                                 @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");

            if (!tokenProvider.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
            }

            String usuarioId = tokenProvider.getUserIdFromToken(token);
            if (usuarioId == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token sin ID de usuario válido");
            }

            Optional<Lugar> optLugar = lugarService.findEntityById(id);
            if (optLugar.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lugar no encontrado");
            }

            Lugar lugar = optLugar.get();

            if (!lugar.getUsuario().getId().toString().equals(usuarioId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No autorizado para modificar este lugar");
            }

            String nuevaFotoUrl = body.get("fotoUrl");
            String fotoAnterior = lugar.getFotoUrl();
            lugar.setFotoUrl(nuevaFotoUrl);

            lugarService.saveEntity(lugar); // este método guarda la entidad (no DTO)

            return ResponseEntity.ok(Map.of(
                "success", true,
                "nuevaFoto", nuevaFotoUrl,
                "fotoAnterior", fotoAnterior
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
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
    @GetMapping("/count")
    public ResponseEntity<Long> contarLugares() {
        return ResponseEntity.ok(lugarService.count());
    }
    
    @GetMapping("/tipo-distribucion")
    public ResponseEntity<Map<String, Long>> obtenerDistribucionPorTipo() {
        return ResponseEntity.ok(lugarService.contarPorTipo());
    }
    
    @GetMapping("/estadisticas-mensuales")
    public ResponseEntity<Map<String, Long>> contarLugaresPorMes() {
        return ResponseEntity.ok(lugarService.contarPorMes());
    }
    
    @PostMapping("/eliminar-imagen")
    public ResponseEntity<?> eliminarImagenLugar(@RequestHeader("Authorization") String authHeader,
                                                 @RequestBody Map<String, String> body) {
        System.out.println("📥 Endpoint /lugares/eliminar-imagen alcanzado correctamente.");

        try {
            String token = authHeader.replace("Bearer ", "");

            if (!tokenProvider.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
            }

            String usuarioId = tokenProvider.getUserIdFromToken(token);
            if (usuarioId == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token sin ID de usuario válido");
            }

            String publicId = body.get("public_id");

            Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", System.getenv("CLOUDINARY_CLOUD_NAME"),
                "api_key", System.getenv("CLOUDINARY_API_KEY"),
                "api_secret", System.getenv("CLOUDINARY_API_SECRET")
            ));

            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

            return ResponseEntity.ok(Map.of("success", true, "result", result));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

}
