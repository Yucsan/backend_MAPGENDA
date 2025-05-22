package com.aventura.api.entity;



import java.io.Serializable;
import java.util.Objects;

/* xq no lombok en esta clase
 - Desaparece la advertencia de Hibernate
 - Es más robusto para usar con claves compuestas
 - No dependés de comportamiento interno de Lombok para algo tan sensible
 */


public class LugarCategoriaId implements Serializable {

    private String lugar;
    private Long categoria;

    public LugarCategoriaId() {}

    public LugarCategoriaId(String lugar, Long categoria) {
        this.lugar = lugar;
        this.categoria = categoria;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LugarCategoriaId)) return false;
        LugarCategoriaId that = (LugarCategoriaId) o;
        return Objects.equals(lugar, that.lugar) &&
               Objects.equals(categoria, that.categoria);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lugar, categoria);
    }
}
