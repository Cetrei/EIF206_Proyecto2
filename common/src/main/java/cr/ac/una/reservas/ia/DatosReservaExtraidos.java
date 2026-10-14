package cr.ac.una.reservas.ia;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DatosReservaExtraidos implements Serializable {
    private static final long serialVersionUID = 1L;

    private String actividad;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private List<Integer> idsCategoriasIdentificadas = new ArrayList<>();

    public DatosReservaExtraidos() {
    }

    public String getActividad() {
        return actividad;
    }

    public void setActividad(String actividad) {
        this.actividad = actividad;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public List<Integer> getIdsCategoriasIdentificadas() {
        return idsCategoriasIdentificadas;
    }

    public void setIdsCategoriasIdentificadas(List<Integer> idsCategoriasIdentificadas) {
        this.idsCategoriasIdentificadas = idsCategoriasIdentificadas;
    }

    @Override
    public String toString() {
        return String.format(
                "DatosReservaExtraidos{actividad=%s, fecha=%s, horaInicio=%s, horaFin=%s, categorias=%s}",
                actividad, fecha, horaInicio, horaFin, idsCategoriasIdentificadas);
    }
}
