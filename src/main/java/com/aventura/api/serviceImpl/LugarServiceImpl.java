package com.aventura.api.serviceImpl;

import com.aventura.api.dto.LugarDTO;
import com.aventura.api.entity.Lugar;
import com.aventura.api.entity.Usuario;
import com.aventura.api.mapper.LugarMapper;
import com.aventura.api.repository.LugarRepository;
import com.aventura.api.repository.UsuarioRepository;
import com.aventura.api.service.LugarService;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LugarServiceImpl implements LugarService {

    @Autowired
    private LugarRepository lugarRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LugarMapper lugarMapper;

    @Override
    public List<LugarDTO> findAll() {
        return lugarRepository.findAll()
                .stream()
                .map(lugarMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<LugarDTO> findById(String id) {
        return lugarRepository.findById(id)
                .map(lugarMapper::toDTO);
    }

    @Override
    public LugarDTO save(LugarDTO dto) {
        Lugar lugar = lugarMapper.toEntity(dto);
        // 💡 Buscar el usuario por ID
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        lugar.setUsuario(usuario);
        Lugar guardado = lugarRepository.save(lugar);
        return lugarMapper.toDTO(guardado);
    }

    @Override
    public void deleteById(String id) {
        lugarRepository.deleteById(id);
    }

	@Override
	public LugarDTO update(String id, LugarDTO dto) {
		 Lugar lugarExistente = lugarRepository.findById(id)
			        .orElseThrow(() -> new EntityNotFoundException("Lugar no encontrado con id: " + id));

			    // 🔁 Actualizá los campos que querés permitir modificar
			    lugarExistente.setNombre(dto.getNombre());
			    lugarExistente.setLatitud(dto.getLatitud());
			    lugarExistente.setLongitud(dto.getLongitud());
			    lugarExistente.setDireccion(dto.getDireccion());
			    lugarExistente.setTipo(dto.getTipo());
			    lugarExistente.setCalificacion(dto.getCalificacion());
			    lugarExistente.setFotoUrl(dto.getFotoUrl());
			    lugarExistente.setAbiertoAhora(dto.isAbiertoAhora());
			    lugarExistente.setDuracionEstimadaMinutos(dto.getDuracionEstimadaMinutos());

			    // Podés mapear otros campos si es necesario

			    Lugar guardado = lugarRepository.save(lugarExistente);
			    return lugarMapper.toDTO(guardado);
	}
	
	
	@Override
	public List<LugarDTO> findByUsuarioId(UUID usuarioId) {
	    List<Lugar> lugares = lugarRepository.findByUsuario_Id(usuarioId);
	    return lugares.stream()
	                  .map(lugarMapper::toDTO)
	                  .toList();
	}
	@Override
	public long count() {
	    return lugarRepository.count();
	}
	
	@Override
	public Map<String, Long> contarPorTipo() {
	    List<Object[]> resultados = lugarRepository.contarPorTipo();
	    return resultados.stream()
	        .collect(Collectors.toMap(
	            obj -> (String) obj[0],
	            obj -> ((Number) obj[1]).longValue()
	        ));
	}
	
	@Override
	public Map<String, Long> contarPorMes() {
	    List<Object[]> resultados = lugarRepository.contarPorMes();
	    return resultados.stream()
	            .collect(Collectors.toMap(
	                row -> (String) row[0],   // mes
	                row -> ((Number) row[1]).longValue() // cantidad
	            ));
	}

	
	


	

	
	

	
}
