package com.aventura.api.controller;

import com.aventura.api.dto.UsuarioDTO;
import com.aventura.api.entity.Usuario;
import com.aventura.api.mapper.UsuarioMapper;
import com.aventura.api.security.JwtTokenProvider;
import com.aventura.api.service.UsuarioService;
import com.aventura.api.serviceImpl.UsuarioServiceImpl;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.google.api.client.json.jackson2.JacksonFactory;


@RestController
@RequestMapping("usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

	private final UsuarioService usuarioService;
	private final UsuarioMapper usuarioMapper;
	private final JwtTokenProvider tokenProvider;

	public UsuarioController(UsuarioService usuarioService, UsuarioMapper usuarioMapper, JwtTokenProvider tokenProvider) {
	    this.usuarioService = usuarioService;
	    this.usuarioMapper = usuarioMapper;
	    this.tokenProvider = tokenProvider;
	}

	@GetMapping
	public List<UsuarioDTO> getAllUsuarios() {
		return usuarioService.findAll().stream().map(usuarioMapper::toDTO).toList();
	}
	

    @GetMapping("/get/{id}")
    public ResponseEntity<UsuarioDTO> getUsuarioById(@PathVariable UUID id) {
    	UsuarioDTO usuario = usuarioService.getById(id);
    	return ResponseEntity.ok(usuario);
    }

    @PostMapping("/registrar")
    public ResponseEntity<UsuarioDTO> createUsuario(@RequestBody UsuarioDTO usuarioDTO) {
    	UsuarioDTO nuevoUsuario = usuarioService.crearUsuario(usuarioDTO);
        return  ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    @DeleteMapping("/{id}")
    public void deleteUsuario(@PathVariable UUID id) {
        usuarioService.deleteById(id);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> updateUsuario(@PathVariable UUID id, @RequestBody UsuarioDTO dto) {
        UsuarioDTO actualizado = usuarioService.updateUsuario(id, dto);
        return ResponseEntity.ok(actualizado);
    }
    
    @PostMapping("/login-google")
    public ResponseEntity<?> loginConGoogle(@RequestBody Map<String, String> body) {
        String idTokenString = body.get("idToken");

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList("1015355618690-npaigcljk1q1bec5tdsab0a088rnri4n.apps.googleusercontent.com")) // 👈 importante
                .build();

        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                String email = payload.getEmail();
                String nombre = (String) payload.get("name");
                String pictureUrl = (String) payload.get("picture");

                // Buscar usuario en BD
                Optional<Usuario> existente = usuarioService.findByEmail(email);
                Usuario usuario;

                if (existente.isPresent()) {
                    usuario = existente.get();
                } else {
                    // Crear nuevo usuario
                    usuario = new Usuario();
                    usuario.setEmail(email);
                    usuario.setNombre(nombre);
                    usuario.setFechaRegistro(new Timestamp(System.currentTimeMillis()));
                    usuario.setVerificado(true);
                    usuario.setRol("USER");
                    usuario = usuarioService.save(usuario);
                }

                // 🔐 Generar JWT
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(usuario.getEmail(), null, null);

                String jwt = tokenProvider.generateToken(authToken);
                
             // 🔍 Imprimir token en consola
                System.out.println("🔐 JWT generado: " + jwt);

                // ✅ Respuesta combinada: token + datos del usuario
                Map<String, Object> respuesta = Map.of(
                    "token", jwt,
                    "usuario", usuarioMapper.toDTO(usuario)
                );

                return ResponseEntity.ok(respuesta);

            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token de Google inválido.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error al procesar el token.");
        }
    }




}
