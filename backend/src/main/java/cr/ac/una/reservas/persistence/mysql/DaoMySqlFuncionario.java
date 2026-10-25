package cr.ac.una.reservas.persistence.mysql;

import cr.ac.una.reservas.autenticacion.RolUsuario;
import cr.ac.una.reservas.funcionario.Funcionario;
import cr.ac.una.reservas.persistence.FuncionarioDao;
import cr.ac.una.reservas.persistence.PersistenciaException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DaoMySqlFuncionario extends DaoMySqlBase<Funcionario, String> implements FuncionarioDao {
    private static final String SELECCION =
            "SELECT u.id, u.clave, f.nombre, f.telefono FROM usuario u JOIN funcionario f ON f.id = u.id";
    private static final String INSERTAR_DETALLE = "INSERT INTO funcionario (id, nombre, telefono) VALUES (?, ?, ?)";
    private static final String ELIMINAR_DETALLE = "DELETE FROM funcionario WHERE id = ?";
    private static final String ELIMINAR_RESERVAS_CANCELADAS =
            "DELETE FROM reserva WHERE id_funcionario = ? AND estado = 'CANCELADA'";

    public DaoMySqlFuncionario(ConexionBd conexion) {
        super(conexion);
    }

    @Override
    public Funcionario insertar(Funcionario entidad) {
        super.insertar(entidad);
        try {
            ejecutar(INSERTAR_DETALLE, sentencia -> {
                sentencia.setString(1, entidad.getId());
                sentencia.setString(2, entidad.getNombre());
                sentencia.setString(3, entidad.getTelefono());
            });
        } catch (PersistenciaException excepcion) {
            ejecutar(getSqlEliminar(), sentencia -> sentencia.setString(1, entidad.getId()));
            throw excepcion;
        }
        return entidad;
    }

    @Override
    public boolean eliminar(String id) {
        ejecutar(ELIMINAR_RESERVAS_CANCELADAS, sentencia -> sentencia.setString(1, id));
        ejecutar(ELIMINAR_DETALLE, sentencia -> sentencia.setString(1, id));
        return super.eliminar(id);
    }

    @Override
    public List<Funcionario> buscarPorNombre(String nombre) {
        return consultarLista(
                SELECCION + " WHERE f.nombre LIKE ? ORDER BY u.id",
                sentencia -> sentencia.setString(1, patronContiene(nombre))
        );
    }

    @Override
    protected String getSqlBuscarPorId() {
        return SELECCION + " WHERE u.id = ?";
    }

    @Override
    protected String getSqlListarTodos() {
        return SELECCION + " ORDER BY u.id";
    }

    @Override
    protected String getSqlInsertar() {
        return "INSERT INTO usuario (id, clave, rol) VALUES (?, ?, '" + RolUsuario.FUNCIONARIO.name() + "')";
    }

    @Override
    protected String getSqlActualizar() {
        return "UPDATE usuario u JOIN funcionario f ON f.id = u.id "
                + "SET u.clave = ?, f.nombre = ?, f.telefono = ? WHERE u.id = ?";
    }

    @Override
    protected String getSqlEliminar() {
        return "DELETE FROM usuario WHERE id = ? AND rol = '" + RolUsuario.FUNCIONARIO.name() + "'";
    }

    @Override
    protected void setParametrosInsertar(PreparedStatement sentencia, Funcionario entidad) throws SQLException {
        sentencia.setString(1, entidad.getId());
        sentencia.setString(2, entidad.getClave());
    }

    @Override
    protected void setParametrosActualizar(PreparedStatement sentencia, Funcionario entidad) throws SQLException {
        sentencia.setString(1, entidad.getClave());
        sentencia.setString(2, entidad.getNombre());
        sentencia.setString(3, entidad.getTelefono());
        sentencia.setString(4, entidad.getId());
    }

    @Override
    protected Funcionario mapearFila(ResultSet fila) throws SQLException {
        return new Funcionario(
                fila.getString("id"),
                fila.getString("clave"),
                fila.getString("nombre"),
                fila.getString("telefono")
        );
    }

    @Override
    protected String extraerId(Funcionario entidad) {
        return entidad.getId();
    }
}
