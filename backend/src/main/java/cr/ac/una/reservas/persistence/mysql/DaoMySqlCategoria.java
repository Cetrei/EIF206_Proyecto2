package cr.ac.una.reservas.persistence.mysql;

import cr.ac.una.reservas.categoria.Categoria;
import cr.ac.una.reservas.persistence.CategoriaDao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DaoMySqlCategoria extends DaoMySqlBase<Categoria, Integer> implements CategoriaDao {
    private static final String SELECCION = "SELECT id, descripcion FROM categoria";

    public DaoMySqlCategoria(ConexionBd conexion) {
        super(conexion);
    }

    @Override
    public List<Categoria> buscarPorDescripcion(String texto) {
        return consultarLista(
                SELECCION + " WHERE descripcion LIKE ? ORDER BY id",
                sentencia -> sentencia.setString(1, patronContiene(texto))
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
        return "INSERT INTO categoria (descripcion) VALUES (?)";
    }

    @Override
    protected String getSqlActualizar() {
        return "UPDATE categoria SET descripcion = ? WHERE id = ?";
    }

    @Override
    protected String getSqlEliminar() {
        return "DELETE FROM categoria WHERE id = ?";
    }

    @Override
    protected void setParametrosInsertar(PreparedStatement sentencia, Categoria entidad) throws SQLException {
        sentencia.setString(1, entidad.getDescripcion());
    }

    @Override
    protected void setParametrosActualizar(PreparedStatement sentencia, Categoria entidad) throws SQLException {
        sentencia.setString(1, entidad.getDescripcion());
        sentencia.setInt(2, entidad.getId());
    }

    @Override
    protected Categoria mapearFila(ResultSet fila) throws SQLException {
        return new Categoria(fila.getInt("id"), fila.getString("descripcion"));
    }

    @Override
    protected Integer extraerId(Categoria entidad) {
        return entidad.getId();
    }

    @Override
    protected boolean idGenerado() {
        return true;
    }

    @Override
    protected void asignarIdGenerado(Categoria entidad, ResultSet llaves) throws SQLException {
        entidad.setId(llaves.getInt(1));
    }
}
