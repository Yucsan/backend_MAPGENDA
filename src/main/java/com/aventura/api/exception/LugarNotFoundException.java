package com.aventura.api.exception;



public class LugarNotFoundException extends RuntimeException {
    public LugarNotFoundException(String id) {
        super("Lugar no encontrado con ID: " + id);
    }
}
