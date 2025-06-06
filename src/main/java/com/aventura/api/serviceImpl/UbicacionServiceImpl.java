package com.aventura.api.serviceImpl;

import com.aventura.api.dto.UbicacionDTO;
import com.aventura.api.dto.UsuarioMesDTO;

import org.springframework.http.HttpStatus;

import com.aventura.api.entity.Ubicacion;
import com.aventura.api.entity.Usuario;
import com.aventura.api.exception.UbicacionDuplicadaException;
import com.aventura.api.mapper.UbicacionMapper;

import com.aventura.api.repository.UsuarioRepository;
import com.aventura.api.repository.UbicacionRepository;
import com.aventura.api.service.UbicacionService;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
public class UbicacionServiceImpl implements UbicacionService {

    @Autowired
    private UbicacionRepository ubicacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UbicacionMapper ubicacionMapper;

    @Override
    public UbicacionDTO save(UbicacionDTO dto) {
        System.out.printf("📍 Recibido: lat = %.10f, lon = %.10f%n",
                dto.getLatitud(), dto.getLongitud());

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar si ya existe una ubicación cercana o prácticamente igual
        List<Ubicacion> ubicacionesExistentes = ubicacionRepository.findByUsuario_Id(dto.getUsuarioId());

        double latRed = redondear(dto.getLatitud(), 5);
        double lonRed = redondear(dto.getLongitud(), 5);

        for (Ubicacion existente : ubicacionesExistentes) {
            double latDbRed = redondear(existente.getLatitud(), 5);
            double lonDbRed = redondear(existente.getLongitud(), 5);

            // Comprobación por distancia geográfica
            double distancia = calcularDistancia(dto.getLatitud(), dto.getLongitud(),
            		existente.getLatitud(), existente.getLongitud());
            
            // Comprobación por redondeo exacto
            if (latRed == latDbRed && lonRed == lonDbRed) {
                System.out.println("⚠️ Ubicación con coordenadas prácticamente idénticas ya existe.");
                throw new UbicacionDuplicadaException("Ubicación duplicada: " + existente.getNombre() + " a " + String.format("%.2f", distancia) + " m");

            }


            System.out.printf("Comparando con: %s (%f, %f) -> Distancia: %.2f metros%n",
                    existente.getNombre(),
                    existente.getLatitud(),
                    existente.getLongitud(),
                    distancia
            );

            if (distancia <= 80000) { // Aumentamos el margen para evitar falsos negativos
                System.out.printf("⚠️ Ubicación '%s' está muy cerca (%.2f m) — NO se guarda%n",
                        existente.getNombre(), distancia);
                throw new UbicacionDuplicadaException("Ya existe una ubicación cercana: " + existente.getNombre()+" distancia "+ String.format("%.2f", distancia) + " m");

            }
        }

        // Si pasa todas las validaciones, se guarda
        Ubicacion nueva = ubicacionMapper.toEntity(dto, usuario);
        return ubicacionMapper.toDTO(ubicacionRepository.save(nueva));
    }

    
    private double redondear(double valor, int decimales) {
        return Math.round(valor * Math.pow(10, decimales)) / Math.pow(10, decimales);
    }

    
    private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }



    @Override
    public List<UbicacionDTO> findByUsuarioId(UUID usuarioId) {
        return ubicacionRepository.findByUsuario_Id(usuarioId).stream()
                .map(ubicacionMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    private boolean estaCerca(double lat1, double lon1, double lat2, double lon2, double maxDistanciaMetros) {
        double R = 6371000; // radio de la Tierra en metros
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distancia = R * c;
        return distancia <= maxDistanciaMetros;
    }


  
  
    @Override
    public Optional<UbicacionDTO> findById(UUID id) {
        return ubicacionRepository.findById(id)
                .map(ubicacionMapper::toDTO);
    }


    @Override
    public UbicacionDTO update(UUID id, UbicacionDTO dto) {
        Ubicacion existente = ubicacionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ubicación no encontrada"));

        existente.setNombre(dto.getNombre());
        existente.setLatitud(dto.getLatitud());
        existente.setLongitud(dto.getLongitud());
        existente.setTipo(dto.getTipo());

        return ubicacionMapper.toDTO(ubicacionRepository.save(existente));
    }


    @Override
    public void deleteById(UUID id) {
        ubicacionRepository.deleteById(id);
    }
    
    @Override
    public Page<UbicacionDTO> findAll(Pageable pageable) {
        return ubicacionRepository.findAll(pageable)
                .map(ubicacionMapper::toDTO);
    }
    
    @Override
    public long count() {
        return ubicacionRepository.count();
    }
    
 

    
 




    
}
