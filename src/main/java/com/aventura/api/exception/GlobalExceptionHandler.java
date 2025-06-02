package com.aventura.api.exception;


import org.springframework.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	 @ExceptionHandler(ResourceNotFoundException.class)
	    public ResponseEntity<Object> handleNotFound(ResourceNotFoundException ex, WebRequest request) {
	        log.warn("❗ Resource not found: {}", ex.getMessage());
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Recurso no encontrado");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

	 @ExceptionHandler(MethodArgumentNotValidException.class)
	    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex) {
	        log.warn("⚠️ Validación fallida: {}", ex.getMessage());
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validación fallida");

        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errores.put(error.getField(), error.getDefaultMessage());
        });
        body.put("errors", errores);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

	 @ExceptionHandler(Exception.class)
	    public ResponseEntity<Object> handleGlobal(Exception ex, WebRequest request) {
	        //log.error("❌ Error interno capturado:", ex); // 🔥 Este es el más importante
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Error interno");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
	 
	 @ExceptionHandler(LugarNotFoundException.class)
	 public ResponseEntity<Object> handleLugarNotFound(LugarNotFoundException ex, WebRequest request) {
	     log.warn("📍 Lugar no encontrado: {}", ex.getMessage());

	     Map<String, Object> body = new HashMap<>();
	     body.put("timestamp", LocalDateTime.now());
	     body.put("status", HttpStatus.NOT_FOUND.value());
	     body.put("error", "Lugar no encontrado");
	     body.put("message", ex.getMessage());
	     body.put("path", request.getDescription(false).replace("uri=", ""));
	     return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
	 }
	 
	 @ExceptionHandler(UbicacionDuplicadaException.class)
	 public ResponseEntity<?> manejarUbicacionDuplicada(UbicacionDuplicadaException ex) {
	     return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
	         "error", "Ubicación duplicada",
	         "mensaje", ex.getMessage()
	     ));
	 }
	 
	 @ResponseStatus(HttpStatus.CONFLICT)
	 public class UbicacionDuplicadaException extends RuntimeException {
	     public UbicacionDuplicadaException(String mensaje) {
	         super(mensaje);
	     }
	 }



	 
}
