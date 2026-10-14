package cr.ac.una.reservas.proxy;

import cr.ac.una.reservas.excepcion.ConexionException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public record ConfiguracionConexion(String host, int puerto) {
    private static final String ARCHIVO = "config.properties";
    private static final String HOST_POR_DEFECTO = "localhost";
    private static final int PUERTO_POR_DEFECTO = 5050;

    public static ConfiguracionConexion cargar() {
        Path archivo = Path.of(ARCHIVO);
        if (!Files.exists(archivo)) {
            return new ConfiguracionConexion(HOST_POR_DEFECTO, PUERTO_POR_DEFECTO);
        }
        Properties propiedades = new Properties();
        try (InputStream entrada = Files.newInputStream(archivo)) {
            propiedades.load(entrada);
            return new ConfiguracionConexion(
                    propiedades.getProperty("host", HOST_POR_DEFECTO).trim(),
                    Integer.parseInt(propiedades.getProperty("puerto", String.valueOf(PUERTO_POR_DEFECTO)).trim())
            );
        } catch (IOException | NumberFormatException excepcion) {
            throw new ConexionException("El archivo config.properties no se pudo leer o es inválido.", excepcion);
        }
    }
}
