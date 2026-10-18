package cr.ac.una.reservas.persistence.mysql;

import cr.ac.una.reservas.persistence.GenericDAO;
import cr.ac.una.reservas.persistence.PersistenciaException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class DaoMySqlBase<T, ID> implements GenericDAO<T, ID> {
    @FunctionalInterface
    protected interface ParametrosSql {
        void asignar(PreparedStatement sentencia) throws SQLException;
    }

    private final ConexionBd conexion;

    protected DaoMySqlBase(ConexionBd conexion) {
        this.conexion = conexion;
    }

    protected abstract String getSqlBuscarPorId();

    protected abstract String getSqlListarTodos();

    protected abstract String getSqlInsertar();

    protected abstract String getSqlActualizar();

    protected abstract String getSqlEliminar();

    protected abstract void setParametrosInsertar(PreparedStatement sentencia, T entidad) throws SQLException;

    protected abstract void setParametrosActualizar(PreparedStatement sentencia, T entidad) throws SQLException;

    protected abstract T mapearFila(ResultSet fila) throws SQLException;

    protected abstract ID extraerId(T entidad);

    protected boolean idGenerado() {
        return false;
    }

    protected void asignarIdGenerado(T entidad, ResultSet llaves) throws SQLException {
        throw new PersistenciaException("La entidad no admite ids generados");
    }

    protected final ConexionBd conexion() {
        return conexion;
    }

    @Override
    public Optional<T> buscarPorId(ID id) {
        return consultarUno(getSqlBuscarPorId(), sentencia -> sentencia.setObject(1, id));
    }

    @Override
    public List<T> listarTodos() {
        return consultarLista(getSqlListarTodos(), sentencia -> { });
    }

    @Override
    public T insertar(T entidad) {
        boolean generaId = idGenerado() && extraerId(entidad) == null;
        int opcion = generaId ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS;
        try (Connection enlace = conexion.obtener();
             PreparedStatement sentencia = enlace.prepareStatement(getSqlInsertar(), opcion)) {
            setParametrosInsertar(sentencia, entidad);
            sentencia.executeUpdate();
            if (generaId) {
                try (ResultSet llaves = sentencia.getGeneratedKeys()) {
                    if (llaves.next()) {
                        asignarIdGenerado(entidad, llaves);
                    }
                }
            }
            return entidad;
        } catch (SQLException excepcion) {
            throw fallo("insertar", excepcion);
        }
    }

    @Override
    public boolean actualizar(T entidad) {
        return ejecutar(getSqlActualizar(), sentencia -> setParametrosActualizar(sentencia, entidad)) > 0;
    }

    @Override
    public boolean eliminar(ID id) {
        return ejecutar(getSqlEliminar(), sentencia -> sentencia.setObject(1, id)) > 0;
    }

    protected final List<T> consultarLista(String sql, ParametrosSql parametros) {
        try (Connection enlace = conexion.obtener();
             PreparedStatement sentencia = enlace.prepareStatement(sql)) {
            parametros.asignar(sentencia);
            try (ResultSet filas = sentencia.executeQuery()) {
                List<T> resultado = new ArrayList<>();
                while (filas.next()) {
                    resultado.add(mapearFila(filas));
                }
                return resultado;
            }
        } catch (SQLException excepcion) {
            throw fallo("consultar", excepcion);
        }
    }

    protected final Optional<T> consultarUno(String sql, ParametrosSql parametros) {
        List<T> resultado = consultarLista(sql, parametros);
        return resultado.isEmpty() ? Optional.empty() : Optional.of(resultado.get(0));
    }

    protected final int ejecutar(String sql, ParametrosSql parametros) {
        try (Connection enlace = conexion.obtener();
             PreparedStatement sentencia = enlace.prepareStatement(sql)) {
            parametros.asignar(sentencia);
            return sentencia.executeUpdate();
        } catch (SQLException excepcion) {
            throw fallo("ejecutar", excepcion);
        }
    }

    protected static PersistenciaException fallo(String operacion, SQLException excepcion) {
        return new PersistenciaException("Fallo de base de datos al " + operacion, excepcion);
    }

    protected static String patronContiene(String texto) {
        String limpio = texto == null ? "" : texto.trim();
        return "%" + limpio.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
    }
}
