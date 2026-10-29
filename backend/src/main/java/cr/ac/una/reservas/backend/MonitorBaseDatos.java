package cr.ac.una.reservas.backend;

import cr.ac.una.reservas.persistence.PersistenciaException;
import cr.ac.una.reservas.persistence.mysql.ConexionBd;
import cr.ac.una.reservas.persistence.mysql.InicializadorBd;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

final class MonitorBaseDatos {
    private static final long INTERVALO_SEGUNDOS = 3;
    private static final int SEGUNDOS_VALIDACION = 2;
    private static final String MENSAJE_SESION_CERRADA =
            "El servicio no está disponible por el momento. Se cerró su sesión; intente de nuevo en unos minutos.";
    private static final String[] PUNTOS = {".", "..", "..."};

    private final ConexionBd conexion;
    private final PrintStream salida;
    private final Consumer<String> alPerderBase;
    private final Runnable mostrarOpciones;
    private final BooleanSupplier consolaLibre;
    private final ScheduledExecutorService ejecutor = Executors.newScheduledThreadPool(2, tarea -> {
        Thread hilo = new Thread(tarea, "monitor-base-datos");
        hilo.setDaemon(true);
        return hilo;
    });
    private volatile boolean disponible;
    private volatile boolean esperando;
    private boolean huboCaida;
    private String ultimoMensaje = "";
    private int paso;

    MonitorBaseDatos(
            ConexionBd conexion,
            PrintStream salida,
            Consumer<String> alPerderBase,
            Runnable mostrarOpciones,
            BooleanSupplier consolaLibre
    ) {
        this.conexion = conexion;
        this.salida = salida;
        this.alPerderBase = alPerderBase;
        this.mostrarOpciones = mostrarOpciones;
        this.consolaLibre = consolaLibre;
    }

    boolean disponible() {
        return disponible;
    }

    void iniciar() {
        ejecutor.scheduleWithFixedDelay(this::verificar, 0, INTERVALO_SEGUNDOS, TimeUnit.SECONDS);
        ejecutor.scheduleWithFixedDelay(this::animar, 1, 1, TimeUnit.SECONDS);
    }

    void verificarAhora() {
        ejecutor.execute(this::verificar);
    }

    private synchronized void verificar() {
        try {
            if (disponible) {
                comprobar();
            } else {
                InicializadorBd.preparar(conexion);
                registrarRecuperacion();
            }
        } catch (RuntimeException excepcion) {
            registrarFalla(String.valueOf(excepcion.getMessage()));
        }
    }

    private void animar() {
        if (!esperando || disponible || !consolaLibre.getAsBoolean()) {
            return;
        }
        salida.print(Ansi.BORRAR_LINEA + Ansi.tenue("Esperando la base de datos" + String.format("%-3s", PUNTOS[paso % PUNTOS.length])));
        salida.flush();
        paso++;
    }

    private void comprobar() {
        try (Connection enlace = conexion.obtener()) {
            if (!enlace.isValid(SEGUNDOS_VALIDACION)) {
                throw new PersistenciaException("La conexion con MySQL no responde.");
            }
        } catch (SQLException excepcion) {
            throw new PersistenciaException("La conexion con MySQL se perdio: " + excepcion.getMessage(), excepcion);
        }
    }

    private void registrarRecuperacion() {
        disponible = true;
        esperando = false;
        ultimoMensaje = "";
        salida.print(Ansi.BORRAR_LINEA);
        if (huboCaida) {
            salida.println(Ansi.exito("Reconectado a la base de datos. El Backend atiende peticiones de nuevo."));
        } else {
            salida.println(Ansi.exito("Base de datos disponible. El Backend atiende peticiones."));
        }
        mostrarOpciones.run();
    }

    private void registrarFalla(String mensaje) {
        boolean estabaDisponible = disponible;
        disponible = false;
        if (estabaDisponible) {
            huboCaida = true;
            salida.print(Ansi.BORRAR_LINEA);
            salida.println(Ansi.advertencia("Se perdio la conexion con la base de datos. Se cierran las sesiones activas."));
            alPerderBase.accept(MENSAJE_SESION_CERRADA);
        }
        if (!mensaje.equals(ultimoMensaje)) {
            ultimoMensaje = mensaje;
            salida.print(Ansi.BORRAR_LINEA);
            salida.println(Ansi.error("Base de datos no disponible: " + mensaje));
            salida.println(Ansi.advertencia(
                    "El Backend sigue activo y revisa la base cada " + INTERVALO_SEGUNDOS + " segundos."
            ));
            mostrarOpciones.run();
        }
        esperando = true;
    }
}
