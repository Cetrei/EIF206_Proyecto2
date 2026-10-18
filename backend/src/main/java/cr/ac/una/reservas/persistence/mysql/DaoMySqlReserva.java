package cr.ac.una.reservas.persistence.mysql;

import cr.ac.una.reservas.persistence.PersistenciaException;
import cr.ac.una.reservas.persistence.ReservaDao;
import cr.ac.una.reservas.reserva.EstadoReserva;
import cr.ac.una.reservas.reserva.Reserva;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class DaoMySqlReserva extends DaoMySqlBase<Reserva, Integer> implements ReservaDao {
    private static final String SELECCION =
            "SELECT id, id_funcionario, actividad, fecha, hora_inicio, hora_fin, estado FROM reserva";
    private static final String INSERTAR_DETALLE = "INSERT INTO reserva_recurso (id_reserva, id_recurso) VALUES (?, ?)";
    private static final String ELIMINAR_DETALLE = "DELETE FROM reserva_recurso WHERE id_reserva = ? AND id_recurso = ?";
    private static final String ELIMINAR_DETALLES = "DELETE FROM reserva_recurso WHERE id_reserva = ?";
    private static final String DETALLE_DE_UNA = "SELECT id_recurso FROM reserva_recurso WHERE id_reserva = ?";
    private static final String DETALLE_CON_CATEGORIA =
            "SELECT rr.id_reserva, rr.id_recurso, r.id_categoria FROM reserva_recurso rr "
                    + "JOIN recurso r ON r.id = rr.id_recurso WHERE rr.id_reserva IN (%s) "
                    + "ORDER BY rr.id_reserva, rr.id_recurso";

    public DaoMySqlReserva(ConexionBd conexion) {
        super(conexion);
    }

    @Override
    public Optional<Reserva> buscarPorId(Integer id) {
        Optional<Reserva> reserva = super.buscarPorId(id);
        reserva.ifPresent(encontrada -> cargarDetalle(List.of(encontrada)));
        return reserva;
    }

    @Override
    public List<Reserva> listarTodos() {
        return cargarDetalle(super.listarTodos());
    }

    @Override
    public List<Reserva> listarPorFuncionario(String idFuncionario) {
        return cargarDetalle(consultarLista(
                SELECCION + " WHERE id_funcionario = ? ORDER BY id",
                sentencia -> sentencia.setString(1, idFuncionario)
        ));
    }

    @Override
    public Reserva insertar(Reserva entidad) {
        super.insertar(entidad);
        try {
            insertarDetalle(entidad.getId(), entidad.getIdsRecursosAsignados());
        } catch (PersistenciaException excepcion) {
            ejecutar(ELIMINAR_DETALLES, sentencia -> sentencia.setInt(1, entidad.getId()));
            ejecutar(getSqlEliminar(), sentencia -> sentencia.setInt(1, entidad.getId()));
            throw excepcion;
        }
        return entidad;
    }

    @Override
    public boolean actualizar(Reserva entidad) {
        if (!super.actualizar(entidad)) {
            return false;
        }
        Set<String> actuales = idsRecursos(entidad.getId());
        Set<String> nuevos = new LinkedHashSet<>(entidad.getIdsRecursosAsignados());
        Set<String> porAgregar = new LinkedHashSet<>(nuevos);
        porAgregar.removeAll(actuales);
        Set<String> porQuitar = new LinkedHashSet<>(actuales);
        porQuitar.removeAll(nuevos);
        insertarDetalle(entidad.getId(), porAgregar);
        for (String idRecurso : porQuitar) {
            ejecutar(ELIMINAR_DETALLE, sentencia -> {
                sentencia.setInt(1, entidad.getId());
                sentencia.setString(2, idRecurso);
            });
        }
        return true;
    }

    @Override
    public boolean eliminar(Integer id) {
        ejecutar(ELIMINAR_DETALLES, sentencia -> sentencia.setInt(1, id));
        return super.eliminar(id);
    }

    @Override
    protected String getSqlBuscarPorId() {
        return SELECCION + " WHERE id = ?";
    }

    @Override
    protected String getSqlListarTodos() {
        return SELECCION + " ORDER BY id";
    }

    @Override
    protected String getSqlInsertar() {
        return "INSERT INTO reserva (id, id_funcionario, actividad, fecha, hora_inicio, hora_fin, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected String getSqlActualizar() {
        return "UPDATE reserva SET id_funcionario = ?, actividad = ?, fecha = ?, hora_inicio = ?, hora_fin = ?, "
                + "estado = ? WHERE id = ?";
    }

    @Override
    protected String getSqlEliminar() {
        return "DELETE FROM reserva WHERE id = ?";
    }

    @Override
    protected void setParametrosInsertar(PreparedStatement sentencia, Reserva entidad) throws SQLException {
        sentencia.setObject(1, entidad.getId());
        sentencia.setString(2, entidad.getIdFuncionario());
        sentencia.setString(3, entidad.getActividad());
        sentencia.setObject(4, entidad.getFecha());
        sentencia.setObject(5, entidad.getHoraInicio());
        sentencia.setObject(6, entidad.getHoraFin());
        sentencia.setString(7, entidad.getEstado().name());
    }

    @Override
    protected void setParametrosActualizar(PreparedStatement sentencia, Reserva entidad) throws SQLException {
        sentencia.setString(1, entidad.getIdFuncionario());
        sentencia.setString(2, entidad.getActividad());
        sentencia.setObject(3, entidad.getFecha());
        sentencia.setObject(4, entidad.getHoraInicio());
        sentencia.setObject(5, entidad.getHoraFin());
        sentencia.setString(6, entidad.getEstado().name());
        sentencia.setInt(7, entidad.getId());
    }

    @Override
    protected Reserva mapearFila(ResultSet fila) throws SQLException {
        Reserva reserva = new Reserva();
        reserva.setId(fila.getInt("id"));
        reserva.setIdFuncionario(fila.getString("id_funcionario"));
        reserva.setActividad(fila.getString("actividad"));
        reserva.setFecha(fila.getObject("fecha", LocalDate.class));
        reserva.setHoraInicio(fila.getObject("hora_inicio", LocalTime.class));
        reserva.setHoraFin(fila.getObject("hora_fin", LocalTime.class));
        reserva.setEstado(EstadoReserva.valueOf(fila.getString("estado")));
        return reserva;
    }

    @Override
    protected Integer extraerId(Reserva entidad) {
        return entidad.getId();
    }

    @Override
    protected boolean idGenerado() {
        return true;
    }

    @Override
    protected void asignarIdGenerado(Reserva entidad, ResultSet llaves) throws SQLException {
        entidad.setId(llaves.getInt(1));
    }

    private List<Reserva> cargarDetalle(List<Reserva> reservas) {
        if (reservas.isEmpty()) {
            return reservas;
        }
        Map<Integer, Reserva> porId = new HashMap<>();
        reservas.forEach(reserva -> porId.put(reserva.getId(), reserva));
        String marcas = porId.keySet().stream().map(id -> "?").collect(Collectors.joining(","));
        try (Connection enlace = conexion().obtener();
             PreparedStatement sentencia = enlace.prepareStatement(String.format(DETALLE_CON_CATEGORIA, marcas))) {
            int posicion = 1;
            for (Integer id : porId.keySet()) {
                sentencia.setInt(posicion++, id);
            }
            try (ResultSet filas = sentencia.executeQuery()) {
                while (filas.next()) {
                    Reserva reserva = porId.get(filas.getInt("id_reserva"));
                    reserva.getIdsRecursosAsignados().add(filas.getString("id_recurso"));
                    Integer idCategoria = filas.getInt("id_categoria");
                    if (!reserva.getIdsCategoriasRequeridas().contains(idCategoria)) {
                        reserva.getIdsCategoriasRequeridas().add(idCategoria);
                    }
                }
            }
        } catch (SQLException excepcion) {
            throw fallo("consultar el detalle de reservas", excepcion);
        }
        return reservas;
    }

    private Set<String> idsRecursos(Integer idReserva) {
        try (Connection enlace = conexion().obtener();
             PreparedStatement sentencia = enlace.prepareStatement(DETALLE_DE_UNA)) {
            sentencia.setInt(1, idReserva);
            try (ResultSet filas = sentencia.executeQuery()) {
                Set<String> ids = new LinkedHashSet<>();
                while (filas.next()) {
                    ids.add(filas.getString("id_recurso"));
                }
                return ids;
            }
        } catch (SQLException excepcion) {
            throw fallo("consultar los recursos de la reserva", excepcion);
        }
    }

    private void insertarDetalle(Integer idReserva, Collection<String> idsRecursos) {
        Set<String> ids = new LinkedHashSet<>(idsRecursos);
        if (ids.isEmpty()) {
            return;
        }
        try (Connection enlace = conexion().obtener();
             PreparedStatement sentencia = enlace.prepareStatement(INSERTAR_DETALLE)) {
            for (String idRecurso : ids) {
                sentencia.setInt(1, idReserva);
                sentencia.setString(2, idRecurso);
                sentencia.addBatch();
            }
            sentencia.executeBatch();
        } catch (SQLException excepcion) {
            throw fallo("insertar los recursos de la reserva", excepcion);
        }
    }
}
