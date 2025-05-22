package com.aventura.api.serviceImpl;

import com.aventura.api.dto.FavoritoDTO;
import com.aventura.api.entity.Favorito;
import com.aventura.api.mapper.FavoritoMapper;
import com.aventura.api.repository.FavoritoRepository;
import com.aventura.api.service.FavoritoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Service
public class FavoritoServiceImpl implements FavoritoService {

    @Autowired
    private FavoritoRepository favoritoRepository;

    @Autowired
    private FavoritoMapper favoritoMapper;

    @Override
    public FavoritoDTO guardarFavorito(FavoritoDTO dto) {
        Favorito favorito = favoritoMapper.toEntity(dto);
        favorito.setFechaGuardado(new Timestamp(System.currentTimeMillis()));
        return favoritoMapper.toDTO(favoritoRepository.save(favorito));
    }

    @Override
    public List<FavoritoDTO> listarPorUsuario(UUID usuarioId) {
        return favoritoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(favoritoMapper::toDTO)
                .toList();
    }

    @Override
    public void eliminarFavorito(Long id) {
        favoritoRepository.deleteById(id);
    }
}
