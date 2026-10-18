package cr.ac.una.reservas.persistence.mysql;

import cr.ac.una.reservas.persistence.PersistenciaException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class ConexionBd {
    private static final String PARAMETROS_URL = "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    private final String url;
    private final String usuario;
    private final String clave;

    public ConexionBd(String host, int puerto, String base, String usuario, String clave) {
        this.url = "jdbc:mysql://" + host + ":" + puerto + "/" + base + PARAMETROS_URL;
        this.usuario = usuario;
        this.clave = clave;
    }

    public static ConexionBd desdePropiedades(Properties propiedades) {
        return new ConexionBd(
                propiedades.getProperty("bd.host", "localhost").trim(),
                puerto(propiedades.getProperty("bd.puerto", "3306")),
                propiedades.getProperty("bd.base", "reservas").trim(),
                propiedades.getProperty("bd.usuario", "root").trim(),
                propiedades.getProperty("bd.clave", "")
        );
    }

    public static ConexionBd cargar(Path archivo) {
        Properties propiedades = new Properties();
        try (InputStream entrada = Files.newInputStream(archivo)) {
            propiedades.load(entrada);
        } catch (IOException excepcion) {
            throw new PersistenciaException("No se pudo leer la configuracion de la base de datos: " + archivo, excepcion);
        }
        return desdePropiedades(propiedades);
    }

    public Connection obtener() {
        try {
            return DriverManager.getConnection(url, usuario, clave);
        } catch (SQLException excepcion) {
            throw new PersistenciaException("No se pudo conectar con la base de datos", excepcion);
        }
    }

    private static int puerto(String texto) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException excepcion) {
            throw new PersistenciaException("El puerto de la base de datos no es valido: " + texto, excepcion);
        }
    }
}
