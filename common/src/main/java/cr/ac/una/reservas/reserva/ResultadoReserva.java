package cr.ac.una.reservas.reserva;

import cr.ac.una.reservas.categoria.Categoria;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ResultadoReserva implements Serializable {
    private static final long serialVersionUID = 1L;

    private final boolean exitoso;
    private final Reserva reserva;
    private final List<Categoria> categoriasNoDisponibles;

    private ResultadoReserva(boolean exitoso, Reserva reserva, List<Categoria> categoriasNoDisponibles) {
        this.exitoso = exitoso;
        this.reserva = reserva;
        this.categoriasNoDisponibles = categoriasNoDisponibles;
    }

    public static ResultadoReserva exito(Reserva reserva) {
        return new ResultadoReserva(true, reserva, Collections.emptyList());
    }

    public static ResultadoReserva fracaso(List<Categoria> categoriasNoDisponibles) {
        return new ResultadoReserva(false, null, new ArrayList<>(categoriasNoDisponibles));
    }

    public boolean isExitoso() {
        return exitoso;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public List<Categoria> getCategoriasNoDisponibles() {
        return categoriasNoDisponibles;
    }
}
