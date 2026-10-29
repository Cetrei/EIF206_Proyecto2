package cr.ac.una.reservas.persistence.mysql;

import cr.ac.una.reservas.persistence.PersistenciaException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class InicializadorBd {
    private static final String SCRIPT = "/base_datos.sql";
    private static final String SQL_TABLAS_EXISTENTES =
            "SELECT COUNT(*) FROM information_schema.tables "
                    + "WHERE table_schema = ? AND table_name IN ('usuario', 'configuracion_ia')";
    private static final int TABLAS_ESPERADAS = 2;

    private InicializadorBd() {
    }

    public static void preparar(ConexionBd conexion) {
        try (Connection servidor = conexion.obtenerServidor()) {
            if (!existeEsquema(servidor, conexion.base())) {
                ejecutarScript(servidor);
            }
        } catch (SQLException excepcion) {
            throw new PersistenciaException("No se pudo preparar la base de datos: " + excepcion.getMessage(), excepcion);
        }
        try (Connection comprobacion = conexion.obtener()) {
            comprobacion.isValid(2);
        } catch (SQLException excepcion) {
            throw new PersistenciaException("No se pudo verificar la base de datos: " + excepcion.getMessage(), excepcion);
        }
    }

    private static boolean existeEsquema(Connection servidor, String base) throws SQLException {
        try (PreparedStatement sentencia = servidor.prepareStatement(SQL_TABLAS_EXISTENTES)) {
            sentencia.setString(1, base);
            try (ResultSet filas = sentencia.executeQuery()) {
                return filas.next() && filas.getInt(1) == TABLAS_ESPERADAS;
            }
        }
    }

    private static void ejecutarScript(Connection servidor) throws SQLException {
        String sql = leerScript();
        try (Statement sentencia = servidor.createStatement()) {
            boolean hayResultado = sentencia.execute(sql);
            while (hayResultado || sentencia.getUpdateCount() != -1) {
                hayResultado = sentencia.getMoreResults();
            }
        }
    }

    private static String leerScript() {
        try (InputStream entrada = InicializadorBd.class.getResourceAsStream(SCRIPT)) {
            if (entrada == null) {
                throw new PersistenciaException("No se encontro base_datos.sql dentro de la aplicacion.");
            }
            return new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException excepcion) {
            throw new PersistenciaException("No se pudo leer base_datos.sql.", excepcion);
        }
    }
}
