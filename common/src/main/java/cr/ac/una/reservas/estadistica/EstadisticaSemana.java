package cr.ac.una.reservas.estadistica;

import java.io.Serializable;
import java.time.LocalDate;

public final class EstadisticaSemana implements Serializable {
    private static final long serialVersionUID = 1L;

    private final LocalDate inicioSemana;
    private final long cantidad;

    public EstadisticaSemana(LocalDate inicioSemana, long cantidad) {
        this.inicioSemana = inicioSemana;
        this.cantidad = cantidad;
    }

    public LocalDate getInicioSemana() {
        return inicioSemana;
    }

    public long getCantidad() {
        return cantidad;
    }
}
