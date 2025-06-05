package com.aventura.api.service;


import com.aventura.api.dto.UsuarioDTO;
import com.aventura.api.entity.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioService {
	
    List<Usuario> findAll();
    Optional<Usuario> findById(UUID id);
    Usuario save(Usuario usuario);
    UsuarioDTO crearUsuario(UsuarioDTO dto);
    void deleteById(UUID id);
    UsuarioDTO updateUsuario(UUID id, UsuarioDTO dto);
    UsuarioDTO getById(UUID id);
    Optional<Usuario> findByEmail(String email);
    long count();
    

   


    
}
