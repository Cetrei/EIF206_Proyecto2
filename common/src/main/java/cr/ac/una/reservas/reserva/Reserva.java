package cr.ac.una.reservas.reserva;

import cr.ac.una.reservas.categoria.Categoria;
import cr.ac.una.reservas.funcionario.Funcionario;
import cr.ac.una.reservas.recurso.Recurso;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Reserva implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String idFuncionario;

    private String actividad;

    private LocalDate fecha;

    private LocalTime horaInicio;

    private LocalTime horaFin;

    private List<Integer> idsCategoriasRequeridas = new ArrayList<>();

    private List<String> idsRecursosAsignados = new ArrayList<>();

    private EstadoReserva estado;

    private Funcionario funcionario;

    private List<Categoria> categoriasRequeridas = new ArrayList<>();

    private List<Recurso> recursosAsignados = new ArrayList<>();

    public Reserva() {
    }
    public Reserva(DatosNuevaReserva datos) {
        this.id = datos.getId();
        this.idFuncionario = datos.getIdFuncionario();
        this.actividad = datos.getActividad();
        this.fecha = datos.getFecha();
        this.horaInicio = datos.getHoraInicio();
        this.horaFin = datos.getHoraFin();
        this.idsCategoriasRequeridas = datos.getIdsCategoriasRequeridas() == null
                ? new ArrayList<>()
                : new ArrayList<>(datos.getIdsCategoriasRequeridas());
        this.estado = EstadoReserva.ACTIVA;
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
        this.idsCategoriasRequeridas = idsCategoriasRequeridas == null
                ? new ArrayList<>()
                : new ArrayList<>(idsCategoriasRequeridas);
    }

    public List<String> getIdsRecursosAsignados() {
        return idsRecursosAsignados;
    }
    public void setIdsRecursosAsignados(List<String> idsRecursosAsignados) {
        this.idsRecursosAsignados = idsRecursosAsignados == null
                ? new ArrayList<>()
                : new ArrayList<>(idsRecursosAsignados);
    }

    public EstadoReserva getEstado() {
        return estado;
    }
    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }
    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
        if (funcionario != null) {
            this.idFuncionario = funcionario.getId();
        }
    }

    public List<Categoria> getCategoriasRequeridas() {
        return categoriasRequeridas;
    }
    public void setCategoriasRequeridas(List<Categoria> categoriasRequeridas) {
        this.categoriasRequeridas = categoriasRequeridas;
    }

    public List<Recurso> getRecursosAsignados() {
        return recursosAsignados;
    }

    public void setRecursosAsignados(List<Recurso> recursosAsignados) {
        this.recursosAsignados = recursosAsignados;
    }

    @Override
    public String toString() {
        return String.format(
                "Reserva{id=%s, actividad=%s, fecha=%s, horaInicio=%s, horaFin=%s, estado=%s}",
                id,
                actividad,
                fecha,
                horaInicio,
                horaFin,
                estado
        );
    }
}
