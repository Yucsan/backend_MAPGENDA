package com.aventura.api.serviceImpl;

import com.aventura.api.dto.RutaDTO;
import com.aventura.api.entity.*;
import com.aventura.api.exception.LugarNotFoundException;
import com.aventura.api.mapper.RutaMapper;
import com.aventura.api.repository.*;

import com.aventura.api.service.RutaService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RutaServiceImpl implements RutaService {

    private final RutaRepository rutaRepository;
    private final RutaLugarRepository rutaLugarRepository;
    private final UsuarioRepository usuarioRepository;
    private final LugarRepository lugarRepository;
    private final RutaMapper rutaMapper;

    public RutaServiceImpl(
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

    @Override
    public RutaDTO crearRuta(RutaDTO dto, Usuario usuario) {
        log.info("🚧 Iniciando creación de ruta: '{}'", dto.getNombre());

        Ruta ruta = rutaMapper.toEntity(dto, usuario);
        Ruta rutaGuardada = rutaRepository.save(ruta);

        List<RutaLugar> referencias = new ArrayList<>();
        int orden = 0;

        if (dto.getLugarIdsOrdenados() == null || dto.getLugarIdsOrdenados().isEmpty()) {
            log.warn("⚠️ La lista lugarIdsOrdenados está vacía o es null para la ruta '{}'", dto.getNombre());
            throw new IllegalArgumentException("La lista de IDs de lugares no puede estar vacía");
        }

        for (String lugarId : dto.getLugarIdsOrdenados()) {
            log.info("🔍 Buscando lugar con ID: {}", lugarId);
            Lugar lugar = lugarRepository.findById(lugarId)
                    .orElseThrow(() -> new LugarNotFoundException(lugarId));
            referencias.add(new RutaLugar(rutaGuardada, lugar, orden++));
        }

        rutaLugarRepository.saveAll(referencias);

        log.info("✅ Ruta '{}' creada correctamente con {} lugares.", dto.getNombre(), referencias.size());

        return rutaMapper.toDTO(rutaGuardada, referencias.stream()
                .map(ref -> ref.getLugar().getId())
                .collect(Collectors.toList()));
    }

    @Override
    public List<RutaDTO> listarPorUsuario(UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Ruta> rutas = rutaRepository.findByUsuario(usuario);

        return rutas.stream().map(ruta -> {
            List<String> lugarIds = rutaLugarRepository.findByRuta(ruta).stream()
                    .sorted(Comparator.comparing(RutaLugar::getOrden))
                    .map(ref -> ref.getLugar().getId())
                    .collect(Collectors.toList());

            return rutaMapper.toDTO(ruta, lugarIds);
        }).collect(Collectors.toList());
    }
    
    
    @Transactional
    public Ruta guardarRuta(RutaDTO dto) {
        Ruta ruta = (dto.getId() != null)
            ? rutaRepository.findById(dto.getId()).orElse(new Ruta())
            : new Ruta();

        ruta.setNombre(dto.getNombre());
        ruta.setCategoria(dto.getCategoria());
        ruta.setModoTransporte(dto.getModoTransporte());
        ruta.setPolylineCodificada(dto.getPolylineCodificada());
        ruta.setOrigenLat(dto.getOrigenLat());
        ruta.setOrigenLng(dto.getOrigenLng());
        ruta.setDestinoLat(dto.getDestinoLat());
        ruta.setDestinoLng(dto.getDestinoLng());
        ruta.setLugaresIntermedios(dto.getLugaresIntermedios());
        ruta.setUbicacionId(dto.getUbicacionId());

        ruta = rutaRepository.save(ruta);

        // Si es edición, limpiar antiguos
        rutaLugarRepository.deleteByRutaId(ruta.getId());

        // Guardar lugares con orden
        List<String> lugares = dto.getLugarIdsOrdenados();
        if (lugares != null) {
            for (int i = 0; i < lugares.size(); i++) {
                RutaLugar rl = new RutaLugar();
                rl.setRuta(ruta);

                Lugar lugar = new Lugar();
                lugar.setId(lugares.get(i)); // ID como string desde el DTO
                rl.setLugar(lugar);

                rl.setOrden(i);
                rutaLugarRepository.save(rl);
            }
        }

        return ruta;
    }
    
    @Override
    public List<RutaDTO> listarTodas() {
        return rutaRepository.findAll()
            .stream()
            .map(rutaMapper::toDTO)
            .collect(Collectors.toList());
    }


    @Override
    public long count() {
        return rutaRepository.count();
    }
    
    
    
    
    
}
