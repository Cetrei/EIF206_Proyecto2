package cr.ac.una.reservas.backend;

import cr.ac.una.reservas.servidor.RutasBackend;
import cr.ac.una.reservas.servidor.ServidorBackend;
import cr.ac.una.reservas.util.ArchivoConfiguracion;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class MainBackend {
    private static final int PUERTO_POR_DEFECTO = 5050;

    private MainBackend() {
    }

    public static void main(String[] args) throws IOException {
        ServidorBackend servidor = new ServidorBackend(RutasBackend.crearDespachador());
        RutasBackend.instalarDifusor(servidor);
        Runtime.getRuntime().addShutdownHook(new Thread(servidor::close));
        servidor.iniciar(leerPuerto());
    }

    private static int leerPuerto() throws IOException {
        Path archivo = ArchivoConfiguracion.ubicar("backend");
        if (!Files.exists(archivo)) {
            return PUERTO_POR_DEFECTO;
        }
        Properties propiedades = new Properties();
        try (InputStream entrada = Files.newInputStream(archivo)) {
            propiedades.load(entrada);
        }
        return Integer.parseInt(propiedades.getProperty("puerto", String.valueOf(PUERTO_POR_DEFECTO)).trim());
    }
}
