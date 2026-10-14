package cr.ac.una.reservas.estadistica;

import cr.ac.una.reservas.categoria.Categoria;

import java.io.Serializable;

public final class EstadisticaCategoria implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Categoria categoria;
    private final long cantidad;

    public EstadisticaCategoria(Categoria categoria, long cantidad) {
        this.categoria = categoria;
        this.cantidad = cantidad;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public long getCantidad() {
        return cantidad;
    }
}
