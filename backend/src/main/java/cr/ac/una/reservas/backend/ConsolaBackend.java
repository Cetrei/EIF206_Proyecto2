package cr.ac.una.reservas.backend;

import cr.ac.una.reservas.persistence.DaoFactory;
import cr.ac.una.reservas.persistence.PersistenciaException;
import cr.ac.una.reservas.persistence.mysql.ConexionBd;
import cr.ac.una.reservas.util.ArchivoConfiguracion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

final class ConsolaBackend {
    private static final String MODULO = "backend";
    private static final String NOMBRE_ARCHIVO = "config.properties";
    private static final String OPCIONES = "Opciones: c = cambiar credenciales de MySQL | s = apagar el Backend";

    private final BufferedReader entrada;
    private final PrintStream salida;
    private final Path archivo;
    private volatile boolean escribiendo;

    ConsolaBackend(BufferedReader entrada, PrintStream salida) {
        this.entrada = entrada;
        this.salida = salida;
        this.archivo = ubicarArchivo();
    }

    boolean asegurarConfiguracion() throws IOException {
        if (!Files.exists(archivo)) {
            salida.println(Ansi.advertencia("No se encontro " + archivo
                    + ". Ingrese los datos de conexion a MySQL (Enter usa el valor entre corchetes)."));
            if (!pedirYGuardar(new Properties())) {
                return false;
            }
        }
        while (true) {
            try {
                ConexionBd.cargar(archivo);
                return true;
            } catch (PersistenciaException excepcion) {
                salida.println(Ansi.error(excepcion.getMessage()));
                if (!pedirYGuardar(cargarPropiedades())) {
                    return false;
                }
            }
        }
    }

    void mostrarOpciones() {
        salida.println(Ansi.info(OPCIONES));
    }

    boolean libre() {
        return !escribiendo;
    }

    void atenderOpciones(ConexionBd conexion, Runnable verificarAhora) {
        Thread.ofPlatform().daemon().name("consola-backend").start(() -> {
            String linea;
            while ((linea = leerLinea()) != null) {
                String opcion = linea.trim();
                if (opcion.equalsIgnoreCase("s")) {
                    System.exit(0);
                } else if (opcion.equalsIgnoreCase("c")) {
                    cambiarCredenciales(conexion, verificarAhora);
                } else if (!opcion.isEmpty()) {
                    mostrarOpciones();
                }
            }
        });
    }

    private void cambiarCredenciales(ConexionBd conexion, Runnable verificarAhora) {
        try {
            if (!pedirYGuardar(cargarPropiedades())) {
                return;
            }
            conexion.reconfigurar(ConexionBd.cargar(archivo));
            verificarAhora.run();
        } catch (IOException | PersistenciaException excepcion) {
            salida.println(Ansi.error("No se pudieron cambiar las credenciales: " + excepcion.getMessage()));
        }
    }

    private Path ubicarArchivo() {
        String indicado = System.getProperty(DaoFactory.PROPIEDAD_CONFIGURACION);
        if (indicado != null) {
            return Path.of(indicado);
        }
        Path ubicado = ArchivoConfiguracion.ubicar(MODULO);
        if (Files.exists(ubicado) || !Files.isDirectory(Path.of(MODULO))) {
            return ubicado;
        }
        return Path.of(MODULO, NOMBRE_ARCHIVO);
    }

    private Properties cargarPropiedades() throws IOException {
        Properties propiedades = new Properties();
        if (Files.exists(archivo)) {
            try (InputStream lectura = Files.newInputStream(archivo)) {
                propiedades.load(lectura);
            }
        }
        return propiedades;
    }

    private boolean pedirYGuardar(Properties propiedades) throws IOException {
        escribiendo = true;
        try {
            return solicitarCampos(propiedades);
        } finally {
            escribiendo = false;
        }
    }

    private boolean solicitarCampos(Properties propiedades) throws IOException {
        salida.print(Ansi.BORRAR_LINEA);
        String[][] campos = {
                {"bd.host", "Servidor MySQL", "localhost"},
                {"bd.puerto", "Puerto MySQL", "3306"},
                {"bd.base", "Nombre de la base", "reservas"},
                {"bd.usuario", "Usuario", "root"},
                {"bd.clave", "Clave", ""}
        };
        for (String[] campo : campos) {
            String actual = propiedades.getProperty(campo[0], campo[2]);
            boolean ocultar = campo[0].equals("bd.clave") && !actual.isEmpty();
            String valor = leer(campo[1] + " [" + (ocultar ? "se conserva la actual" : actual) + "]", actual);
            if (valor == null) {
                return false;
            }
            propiedades.setProperty(campo[0], valor);
        }
        propiedades.putIfAbsent("puerto", "5050");
        try (OutputStream escritura = Files.newOutputStream(archivo)) {
            propiedades.store(escritura, "Configuracion del Backend");
        }
        salida.println(Ansi.exito("Configuracion guardada en " + archivo));
        return true;
    }

    private String leer(String etiqueta, String porDefecto) {
        salida.print(etiqueta + ": ");
        salida.flush();
        String linea = leerLinea();
        if (linea == null) {
            return null;
        }
        return linea.isBlank() ? porDefecto : linea.trim();
    }

    private String leerLinea() {
        try {
            return entrada.readLine();
        } catch (IOException excepcion) {
            return null;
        }
    }
}
