package com.aventura.api.controller;

import com.aventura.api.dto.LugarDTO;
import com.aventura.api.dto.UbicacionDTO;
import com.aventura.api.dto.UsuarioDTO;
import com.aventura.api.dto.UsuarioMesDTO;
import com.aventura.api.entity.Ruta;
import com.aventura.api.entity.Usuario;
import com.aventura.api.mapper.UsuarioMapper;
import com.aventura.api.repository.RutaLugarRepository;
import com.aventura.api.repository.RutaRepository;
import com.aventura.api.repository.UsuarioRepository;
import com.aventura.api.security.JwtTokenProvider;
import com.aventura.api.service.LugarService;
import com.aventura.api.service.UbicacionService;
import com.aventura.api.service.UsuarioService;
import com.aventura.api.serviceImpl.UsuarioServiceImpl;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.google.api.client.json.jackson2.JacksonFactory;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("usuarios")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UsuarioController {

	private final UsuarioService usuarioService;
	private final UsuarioMapper usuarioMapper;
	private final JwtTokenProvider tokenProvider;
	private final PasswordEncoder passwordEncoder;
	private final UsuarioRepository usuarioRepository;
	private final RutaRepository rutaRepository;
	private final RutaLugarRepository rutaLugarRepository;
	private final UbicacionService ubicacionService;
	private final LugarService lugarService;

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
		return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
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

		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
				.setAudience(Collections
						.singletonList("1015355618690-npaigcljk1q1bec5tdsab0a088rnri4n.apps.googleusercontent.com")) // 👈
																														// importante
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

					if (!usuario.getVerificado()) {
						return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Cuenta desactivada");
					}

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
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(usuario, null,
						null);

				String jwt = tokenProvider.generateToken(authToken);

				// 🔍 Imprimir token en consola
				System.out.println("🔐 JWT generado: " + jwt);

				// ✅ Respuesta combinada: token + datos del usuario
				Map<String, Object> respuesta = Map.of("token", jwt, "usuario", usuarioMapper.toDTO(usuario));

				return ResponseEntity.ok(respuesta);

			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token de Google inválido.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error al procesar el token.");
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Map<String, String> creds) {
		String email = creds.get("email");
		String password = creds.get("password");

		Optional<Usuario> usuarioOpt = usuarioService.findByEmail(email);
		if (usuarioOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
		}

		Usuario usuario = usuarioOpt.get();

		if (!"ADMIN".equalsIgnoreCase(usuario.getRol())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Solo administradores pueden ingresar");
		}

		if (usuario.getContrasena() == null || !passwordEncoder.matches(password, usuario.getContrasena())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña inválida");
		}

		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(usuario, null, null);

		String jwt = tokenProvider.generateToken(authToken);

		return ResponseEntity.ok(Map.of("token", jwt, "usuario", usuarioMapper.toDTO(usuario)));
	}

	@PostConstruct
	public void testPasswordEncoder() {
		String hash = "$2a$10$vMincc2zw/E3PeZZk8cyYeb3CNDuAFk5ZgCpXVAqg5IgBWZH7vx6e";
		boolean ok = passwordEncoder.matches("admin123", hash);
		System.out.println("¿Contraseña válida? " + ok);
	}
	/*
	 * @PostConstruct public void generarHash() { String rawPassword = "admin123";
	 * String hash = passwordEncoder.encode(rawPassword);
	 * System.out.println("Nuevo hash generado: " + hash); }
	 */

	// metodos Para el Dashboard

	@GetMapping("/count")
	public ResponseEntity<Long> contarUsuarios() {
		return ResponseEntity.ok(usuarioService.count());
	}

	@GetMapping("/estadisticas-mensuales")
	public ResponseEntity<List<UsuarioMesDTO>> obtenerEstadisticasMensuales() {
		return ResponseEntity.ok(usuarioService.obtenerEstadisticasMensuales());
	}

	@DeleteMapping("/{id}/eliminar-con-datos")
	public ResponseEntity<?> eliminarUsuarioConDatos(@PathVariable UUID id) {
		Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

		if (!usuarioOpt.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
		}

		Usuario usuario = usuarioOpt.get();

		try {
			// 1. Eliminar rutas y sus relaciones con lugares
			List<Ruta> rutas = rutaRepository.findByUsuarioId(usuario.getId());
			for (Ruta ruta : rutas) {
				rutaLugarRepository.eliminarPorRutaId(ruta.getId());
				rutaRepository.delete(ruta);
			}

			// 2. Eliminar lugares del usuario
			List<LugarDTO> lugares = lugarService.findByUsuarioId(usuario.getId());
			for (LugarDTO lugar : lugares) {
				lugarService.deleteById(lugar.getId());
			}

			// 3. Eliminar ubicaciones del usuario
			List<UbicacionDTO> ubicaciones = ubicacionService.findByUsuarioId(usuario.getId());
			for (UbicacionDTO ubicacion : ubicaciones) {
				ubicacionService.deleteById(ubicacion.getId());
			}

			// 4. Eliminar al usuario
			usuarioRepository.delete(usuario);

			return ResponseEntity.noContent().build();

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al eliminar el usuario y sus datos: " + e.getMessage());
		}
	}

	// tratamiento de cuentas
	@PutMapping("/{id}/desactivar")
	public ResponseEntity<?> desactivarCuenta(@PathVariable UUID id) {
		Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
		if (usuarioOpt.isPresent()) {
			Usuario usuario = usuarioOpt.get();
			usuario.setVerificado(false);
			usuarioRepository.save(usuario);
			return ResponseEntity.ok("Cuenta desactivada");
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping("/{id}/reactivar")
	public ResponseEntity<?> reactivarCuenta(@PathVariable UUID id) {
		Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
		if (usuarioOpt.isPresent()) {
			Usuario usuario = usuarioOpt.get();
			usuario.setVerificado(true);
			usuarioRepository.save(usuario);
			return ResponseEntity.ok("Cuenta reactivada");
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/email/{email}")
	public ResponseEntity<UsuarioDTO> getUsuarioByEmail(@PathVariable String email) {
		Optional<Usuario> usuarioOpt = usuarioService.findByEmail(email);
		if (usuarioOpt.isPresent()) {
			Usuario usuario = usuarioOpt.get();
			return ResponseEntity.ok(usuarioMapper.toDTO(usuario));
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@PostMapping("/eliminar-imagen")
	public ResponseEntity<?> eliminarImagen(@RequestHeader("Authorization") String authHeader,
	                                        @RequestBody Map<String, String> body) {
		
		System.out.println("📥 Endpoint /usuarios/eliminar-imagen alcanzado correctamente.");

	    try {
	        String token = authHeader.replace("Bearer ", "");

	        if (!tokenProvider.validateToken(token)) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
	        }

	        // ✅ Obtener el usuario autenticado
	        String usuarioId = tokenProvider.getUserIdFromToken(token);
	        if (usuarioId == null) {
	            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token sin ID de usuario válido");
	        }

	        // 🔒 En producción aquí deberías verificar que el usuario sea dueño de la imagen
	        String publicId = body.get("public_id");

	        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
	            "cloud_name", System.getenv("CLOUDINARY_CLOUD_NAME"),
	            "api_key", System.getenv("CLOUDINARY_API_KEY"),
	            "api_secret", System.getenv("CLOUDINARY_API_SECRET")
	        ));

	        Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

	        return ResponseEntity.ok(Map.of(
	            "success", true,
	            "result", result
	        ));

	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Map.of("success", false, "error", e.getMessage()));
	    }
	}


	
	

}
