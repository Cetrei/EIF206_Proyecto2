package cr.ac.una.reservas.protocol;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public final class AlertaReserva implements Notificacion {
    private static final long serialVersionUID = 1L;

    private final Integer idReserva;
    private final String encargado;
    private final String actividad;
    private final LocalDate fecha;
    private final LocalTime horaInicio;
    private final LocalTime horaFin;
    private final List<String> descripcionesRecursos;

    public AlertaReserva(
            Integer idReserva,
            String encargado,
            String actividad,
            LocalDate fecha,
            LocalTime horaInicio,
            LocalTime horaFin,
            List<String> descripcionesRecursos
    ) {
        this.idReserva = idReserva;
        this.encargado = encargado;
        this.actividad = actividad;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.descripcionesRecursos = List.copyOf(descripcionesRecursos);
    }

    public Integer getIdReserva() {
        return idReserva;
    }

    public String getEncargado() {
        return encargado;
    }

    public String getActividad() {
        return actividad;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public List<String> getDescripcionesRecursos() {
        return descripcionesRecursos;
    }

    @Override
    public String toString() {
        return String.format(
                "AlertaReserva{idReserva=%s, encargado=%s, actividad=%s, fecha=%s}",
                idReserva, encargado, actividad, fecha
        );
    }
}
