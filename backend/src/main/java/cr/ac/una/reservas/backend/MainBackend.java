package cr.ac.una.reservas.backend;

import cr.ac.una.reservas.persistence.DaoFactory;
import cr.ac.una.reservas.persistence.mysql.ConexionBd;
import cr.ac.una.reservas.servidor.Despachador;
import cr.ac.una.reservas.servidor.RutasBackend;
import cr.ac.una.reservas.servidor.ServidorBackend;
import cr.ac.una.reservas.util.ArchivoConfiguracion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.BindException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class MainBackend {
    private static final int PUERTO_POR_DEFECTO = 5050;

    private MainBackend() {
    }

    public static void main(String[] args) throws IOException {
        boolean mysql = usaMySql();
        ConsolaBackend consola = new ConsolaBackend(new BufferedReader(new InputStreamReader(System.in)), System.out);
        if (mysql && !consola.asegurarConfiguracion()) {
            System.exit(1);
            return;
        }
        int puerto = leerPuerto();
        Despachador despachador = RutasBackend.crearDespachador();
        ServidorBackend servidor = new ServidorBackend(despachador);
        RutasBackend.instalarDifusor(servidor);
        try {
            servidor.iniciar(puerto);
        } catch (BindException excepcion) {
            System.err.println(Ansi.error("No se pudo iniciar el Backend: el puerto " + puerto
                    + " ya esta en uso. Cierre el otro Backend o cambie 'puerto' en config.properties."));
            System.exit(1);
            return;
        }
        Runtime.getRuntime().addShutdownHook(new Thread(servidor::close));
        System.out.println(Ansi.exito("Backend escuchando en el puerto " + puerto));
        if (mysql) {
            vigilarBaseDatos(despachador, servidor, consola);
        }
    }

    private static void vigilarBaseDatos(Despachador despachador, ServidorBackend servidor, ConsolaBackend consola) {
        ConexionBd conexion = DaoFactory.obtenerConexionMySql();
        MonitorBaseDatos monitor = new MonitorBaseDatos(
                conexion, System.out, servidor::terminarTodasLasSesiones, consola::mostrarOpciones, consola::libre
        );
        despachador.baseDisponible(monitor::disponible);
        consola.atenderOpciones(conexion, monitor::verificarAhora);
        monitor.iniciar();
    }

    private static boolean usaMySql() {
        return DaoFactory.MODO_MYSQL.equalsIgnoreCase(System.getProperty(DaoFactory.PROPIEDAD_MODO, DaoFactory.MODO_MYSQL).trim());
    }

    private static int leerPuerto() throws IOException {
        String indicado = System.getProperty(DaoFactory.PROPIEDAD_CONFIGURACION);
        Path archivo = indicado != null ? Path.of(indicado) : ArchivoConfiguracion.ubicar("backend");
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
