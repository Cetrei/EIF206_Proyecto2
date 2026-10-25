package cr.ac.una.reservas.persistence.mysql;

import cr.ac.una.reservas.persistence.RecursoDao;
import cr.ac.una.reservas.recurso.Recurso;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DaoMySqlRecurso extends DaoMySqlBase<Recurso, String> implements RecursoDao {
    private static final String SELECCION = "SELECT id, id_categoria, descripcion FROM recurso";

    private static final String ELIMINAR_ASIGNACIONES_CANCELADAS =
            "DELETE rr FROM reserva_recurso rr JOIN reserva r ON r.id = rr.id_reserva "
                    + "WHERE rr.id_recurso = ? AND r.estado = 'CANCELADA'";

    public DaoMySqlRecurso(ConexionBd conexion) {
        super(conexion);
    }

    @Override
    public boolean eliminar(String id) {
        ejecutar(ELIMINAR_ASIGNACIONES_CANCELADAS, sentencia -> sentencia.setString(1, id));
        return super.eliminar(id);
    }

    @Override
    public List<Recurso> listarPorCategoria(Integer idCategoria) {
        return consultarLista(
                SELECCION + " WHERE id_categoria = ? ORDER BY id",
                sentencia -> sentencia.setInt(1, idCategoria)
        );
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
        return "INSERT INTO recurso (id, id_categoria, descripcion) VALUES (?, ?, ?)";
    }

    @Override
    protected String getSqlActualizar() {
        return "UPDATE recurso SET id_categoria = ?, descripcion = ? WHERE id = ?";
    }

    @Override
    protected String getSqlEliminar() {
        return "DELETE FROM recurso WHERE id = ?";
    }

    @Override
    protected void setParametrosInsertar(PreparedStatement sentencia, Recurso entidad) throws SQLException {
        sentencia.setString(1, entidad.getId());
        sentencia.setInt(2, entidad.getIdCategoria());
        sentencia.setString(3, entidad.getDescripcion());
    }

    @Override
    protected void setParametrosActualizar(PreparedStatement sentencia, Recurso entidad) throws SQLException {
        sentencia.setInt(1, entidad.getIdCategoria());
        sentencia.setString(2, entidad.getDescripcion());
        sentencia.setString(3, entidad.getId());
    }

    @Override
    protected Recurso mapearFila(ResultSet fila) throws SQLException {
        return new Recurso(fila.getString("id"), fila.getInt("id_categoria"), fila.getString("descripcion"));
    }

    @Override
    protected String extraerId(Recurso entidad) {
        return entidad.getId();
    }
}
