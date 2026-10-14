package cr.ac.una.reservas.categoria;

import java.io.Serializable;
import java.util.Objects;

public class Categoria implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String descripcion;

    public Categoria() {}
    public Categoria(Integer id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return String.format("Categoria{id=%s, descripcion=%s}", id, descripcion);
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof Categoria)) {
            return false;
        }
        return Objects.equals(id, ((Categoria) otro).id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
