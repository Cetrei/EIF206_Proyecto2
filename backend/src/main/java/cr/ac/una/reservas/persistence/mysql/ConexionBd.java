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
    private static final int ERROR_ACCESO_DENEGADO = 1045;
    private static final int ERROR_SIN_PERMISO = 1044;
    private static final int ERROR_BASE_INEXISTENTE = 1049;
    private static final String ESTADO_CONEXION = "08";

    private volatile Datos datos;

    private record Datos(String host, int puerto, String base, String url, String usuario, String clave) {
    }

    public ConexionBd(String host, int puerto, String base, String usuario, String clave) {
        this.datos = new Datos(
                host, puerto, base, "jdbc:mysql://" + host + ":" + puerto + "/" + base + PARAMETROS_URL, usuario, clave
        );
    }

    public void reconfigurar(ConexionBd otra) {
        this.datos = otra.datos;
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

    public String base() {
        return datos.base();
    }

    public Connection obtener() {
        Datos actual = datos;
        return abrir(actual, actual.url());
    }

    public Connection obtenerServidor() {
        Datos actual = datos;
        return abrir(
                actual,
                "jdbc:mysql://" + actual.host() + ":" + actual.puerto() + "/" + PARAMETROS_URL + "&allowMultiQueries=true"
        );
    }

    private Connection abrir(Datos actual, String direccion) {
        try {
            return DriverManager.getConnection(direccion, actual.usuario(), actual.clave());
        } catch (SQLException excepcion) {
            throw new PersistenciaException(explicar(actual, excepcion), excepcion);
        }
    }

    private static String explicar(Datos actual, SQLException excepcion) {
        int codigo = excepcion.getErrorCode();
        String estado = excepcion.getSQLState();
        if (codigo == ERROR_ACCESO_DENEGADO) {
            return "MySQL rechazo el usuario o la clave. Revise bd.usuario y bd.clave en config.properties.";
        }
        if (codigo == ERROR_BASE_INEXISTENTE) {
            return "No existe la base de datos '" + actual.base() + "' en MySQL. Revise bd.base en config.properties.";
        }
        if (codigo == ERROR_SIN_PERMISO) {
            return "El usuario '" + actual.usuario() + "' no tiene permiso para usar la base de datos '"
                    + actual.base() + "'.";
        }
        if (estado != null && estado.startsWith(ESTADO_CONEXION)) {
            return "No se pudo conectar con MySQL en " + actual.host() + ":" + actual.puerto()
                    + ". Verifique que MySQL este encendido y que bd.host y bd.puerto en config.properties sean correctos.";
        }
        return "Fallo la conexion con MySQL: " + excepcion.getMessage();
    }

    private static int puerto(String texto) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException excepcion) {
            throw new PersistenciaException("El puerto de la base de datos no es valido: " + texto, excepcion);
        }
    }
}
