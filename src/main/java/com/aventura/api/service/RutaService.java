package com.aventura.api.service;



import com.aventura.api.dto.RutaDTO;


import com.aventura.api.entity.Lugar;
import com.aventura.api.entity.Ruta;
import com.aventura.api.entity.RutaLugar;
import com.aventura.api.entity.Usuario;
import com.aventura.api.exception.LugarNotFoundException;
import com.aventura.api.mapper.RutaMapper;
import com.aventura.api.repository.LugarRepository;
import com.aventura.api.repository.RutaLugarRepository;
import com.aventura.api.repository.RutaRepository;
import com.aventura.api.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;


import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RutaService {

    private final RutaRepository rutaRepository;
    private final RutaLugarRepository rutaLugarRepository;
    private final UsuarioRepository usuarioRepository;
    private final LugarRepository lugarRepository;
    private final RutaMapper rutaMapper;

    public RutaService(
        RutaRepository rutaRepository,
        RutaLugarRepository rutaLugarRepository,
        UsuarioRepository usuarioRepository,
        LugarRepository lugarRepository,
        RutaMapper rutaMapper
    ) {
        this.rutaRepository = rutaRepository;
        this.rutaLugarRepository = rutaLugarRepository;
        this.usuarioRepository = usuarioRepository;
        this.lugarRepository = lugarRepository;
        this.rutaMapper = rutaMapper;
    }

    public RutaDTO crearRuta(RutaDTO dto, Usuario usuario) {
        log.info("🚧 Iniciando creación de ruta: '{}'", dto.getNombre());

        Ruta ruta = rutaMapper.toEntity(dto, usuario);
        Ruta rutaGuardada = rutaRepository.save(ruta);

        List<RutaLugar> referencias = new ArrayList<>();
        int orden = 0;

        // 🛡 Validación esencial
        if (dto.getLugarIdsOrdenados() == null || dto.getLugarIdsOrdenados().isEmpty()) {
            log.warn("⚠️ La lista lugarIdsOrdenados está vacía o es null para la ruta '{}'", dto.getNombre());
            throw new IllegalArgumentException("La lista de IDs de lugares no puede estar vacía");
        }

        try {
            for (String lugarId : dto.getLugarIdsOrdenados()) {
                log.info("🔍 Buscando lugar con ID: {}", lugarId);
                Lugar lugar = lugarRepository.findById(lugarId)
                    .orElseThrow(() -> new LugarNotFoundException(lugarId));
                referencias.add(new RutaLugar(rutaGuardada, lugar, orden++));
            }

            rutaLugarRepository.saveAll(referencias);

        } catch (Exception e) {
            log.error("❌ Error al asociar lugares a la ruta '{}'", dto.getNombre(), e);
            throw e; // Esto será capturado por GlobalExceptionHandler
        }

        log.info("✅ Ruta '{}' creada correctamente con {} lugares.", dto.getNombre(), referencias.size());

        return rutaMapper.toDTO(rutaGuardada, referencias.stream()
            .map(ref -> ref.getLugar().getId())
            .collect(Collectors.toList()));
    }




    public List<RutaDTO> listarPorUsuario(UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Ruta> rutas = rutaRepository.findByUsuario(usuario);

        return rutas.stream().map(ruta -> {
        	List<String> lugarIds = rutaLugarRepository.findByRuta(ruta).stream()
        		    .sorted(Comparator.comparing(RutaLugar::getOrden))
        		    .map(ref -> ref.getLugar().getId())  // ← String
        		    .collect(Collectors.toList());

        		return rutaMapper.toDTO(ruta, lugarIds); // List<String>

        }).collect(Collectors.toList());
    }
}
