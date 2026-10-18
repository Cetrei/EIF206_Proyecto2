package cr.ac.una.reservas.persistence.mysql;

import cr.ac.una.reservas.autenticacion.Administrador;
import cr.ac.una.reservas.autenticacion.RolUsuario;
import cr.ac.una.reservas.persistence.AdministradorDao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DaoMySqlAdministrador extends DaoMySqlBase<Administrador, String> implements AdministradorDao {
    private static final String COLUMNAS = "SELECT id, clave FROM usuario WHERE rol = '" + RolUsuario.ADMINISTRADOR.name() + "'";

    public DaoMySqlAdministrador(ConexionBd conexion) {
        super(conexion);
    }

    @Override
    protected String getSqlBuscarPorId() {
        return COLUMNAS + " AND id = ?";
    }

    @Override
    protected String getSqlListarTodos() {
        return COLUMNAS + " ORDER BY id";
    }

    @Override
    protected String getSqlInsertar() {
        return "INSERT INTO usuario (id, clave, rol) VALUES (?, ?, '" + RolUsuario.ADMINISTRADOR.name() + "')";
    }

    @Override
    protected String getSqlActualizar() {
        return "UPDATE usuario SET clave = ? WHERE id = ? AND rol = '" + RolUsuario.ADMINISTRADOR.name() + "'";
    }

    @Override
    protected String getSqlEliminar() {
        return "DELETE FROM usuario WHERE id = ? AND rol = '" + RolUsuario.ADMINISTRADOR.name() + "'";
    }

    @Override
    protected void setParametrosInsertar(PreparedStatement sentencia, Administrador entidad) throws SQLException {
        sentencia.setString(1, entidad.getId());
        sentencia.setString(2, entidad.getClave());
    }

    @Override
    protected void setParametrosActualizar(PreparedStatement sentencia, Administrador entidad) throws SQLException {
        sentencia.setString(1, entidad.getClave());
        sentencia.setString(2, entidad.getId());
    }

    @Override
    protected Administrador mapearFila(ResultSet fila) throws SQLException {
        return new Administrador(fila.getString("id"), fila.getString("clave"));
    }

    @Override
    protected String extraerId(Administrador entidad) {
        return entidad.getId();
    }
}
