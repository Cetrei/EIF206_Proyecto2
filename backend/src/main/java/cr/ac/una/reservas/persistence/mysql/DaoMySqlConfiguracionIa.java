package cr.ac.una.reservas.persistence.mysql;

import cr.ac.una.reservas.ia.ConfiguracionIa;
import cr.ac.una.reservas.persistence.ConfiguracionIaDao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DaoMySqlConfiguracionIa extends DaoMySqlBase<ConfiguracionIa, String> implements ConfiguracionIaDao {
    private static final String SELECCION = "SELECT id, api_key, modelo FROM configuracion_ia";

    public DaoMySqlConfiguracionIa(ConexionBd conexion) {
        super(conexion);
    }

    @Override
    public ConfiguracionIa insertar(ConfiguracionIa entidad) {
        entidad.setId(ConfiguracionIa.ID_UNICO);
        return super.insertar(entidad);
    }

    @Override
    public boolean actualizar(ConfiguracionIa entidad) {
        entidad.setId(ConfiguracionIa.ID_UNICO);
        return super.actualizar(entidad);
    }

    @Override
    protected String getSqlBuscarPorId() {
        return SELECCION + " WHERE id = ?";
    }

    @Override
    protected String getSqlListarTodos() {
        return SELECCION;
    }

    @Override
    protected String getSqlInsertar() {
        return "REPLACE INTO configuracion_ia (id, api_key, modelo) VALUES (?, ?, ?)";
    }

    @Override
    protected String getSqlActualizar() {
        return "UPDATE configuracion_ia SET api_key = ?, modelo = ? WHERE id = ?";
    }

    @Override
    protected String getSqlEliminar() {
        return "DELETE FROM configuracion_ia WHERE id = ?";
    }

    @Override
    protected void setParametrosInsertar(PreparedStatement sentencia, ConfiguracionIa entidad) throws SQLException {
        sentencia.setString(1, entidad.getId());
        sentencia.setString(2, entidad.getApiKey());
        sentencia.setString(3, entidad.getModelo());
    }

    @Override
    protected void setParametrosActualizar(PreparedStatement sentencia, ConfiguracionIa entidad) throws SQLException {
        sentencia.setString(1, entidad.getApiKey());
        sentencia.setString(2, entidad.getModelo());
        sentencia.setString(3, entidad.getId());
    }

    @Override
    protected ConfiguracionIa mapearFila(ResultSet fila) throws SQLException {
        ConfiguracionIa configuracion = new ConfiguracionIa(fila.getString("api_key"), fila.getString("modelo"));
        configuracion.setId(fila.getString("id"));
        return configuracion;
    }

    @Override
    protected String extraerId(ConfiguracionIa entidad) {
        return entidad.getId();
    }
}
