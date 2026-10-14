package cr.ac.una.reservas.reserva;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DatosNuevaReserva implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String idFuncionario;
    private String actividad;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private List<Integer> idsCategoriasRequeridas = new ArrayList<>();

    public DatosNuevaReserva() {
    }

    public DatosNuevaReserva(Integer id, String idFuncionario, String actividad, LocalDate fecha,
                              LocalTime horaInicio, LocalTime horaFin, List<Integer> idsCategoriasRequeridas) {
        this.id = id;
        this.idFuncionario = idFuncionario;
        this.actividad = actividad;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.idsCategoriasRequeridas = idsCategoriasRequeridas;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getIdFuncionario() {
        return idFuncionario;
    }
    public void setIdFuncionario(String idFuncionario) {
        this.idFuncionario = idFuncionario;
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

    public List<Integer> getIdsCategoriasRequeridas() {
        return idsCategoriasRequeridas;
    }
    public void setIdsCategoriasRequeridas(List<Integer> idsCategoriasRequeridas) {
        this.idsCategoriasRequeridas = idsCategoriasRequeridas;
    }
}
