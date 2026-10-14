package cr.ac.una.reservas.recurso;

import cr.ac.una.reservas.categoria.Categoria;

import java.io.Serializable;

public class Recurso implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;

    private Integer idCategoria;

    private String descripcion;

    private Categoria categoria;

    public Recurso() {
    }
    public Recurso(String id, Integer idCategoria, String descripcion) {
        this.id = id;
        this.idCategoria = idCategoria;
        this.descripcion = descripcion;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }
    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Categoria getCategoria() {
        return categoria;
    }
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
        if (categoria != null) {
            this.idCategoria = categoria.getId();
        }
    }

    @Override
    public String toString() {
        return String.format("Recurso{id=%s, idCategoria=%s, descripcion=%s}", id, idCategoria, descripcion);
    }
}
