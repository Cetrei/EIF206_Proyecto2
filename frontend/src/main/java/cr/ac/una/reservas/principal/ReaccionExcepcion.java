package cr.ac.una.reservas.principal;

import cr.ac.una.reservas.autenticacion.SesionControl;
import cr.ac.una.reservas.excepcion.ConexionException;
import cr.ac.una.reservas.excepcion.ReservaAppException;
import cr.ac.una.reservas.excepcion.SesionNoIniciadaException;
import cr.ac.una.reservas.ui.componentes.Popup;

import javax.swing.SwingUtilities;
import java.awt.Frame;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ReaccionExcepcion {
    private static final AtomicBoolean ATENDIENDO = new AtomicBoolean(false);
    private static volatile Runnable recuperarConexion;
    private static volatile Runnable volverALogin;

    private ReaccionExcepcion() {
    }

    public static void instalar(Runnable recuperarConexion, Runnable volverALogin) {
        ReaccionExcepcion.recuperarConexion = recuperarConexion;
        ReaccionExcepcion.volverALogin = volverALogin;
    }

    public static void mostrar(Frame ventana, String titulo, ReservaAppException excepcion) {
        if (excepcion instanceof ConexionException && recuperarConexion != null) {
            conexionPerdida();
            return;
        }
        if (excepcion instanceof SesionNoIniciadaException && volverALogin != null) {
            sesionNoIniciada(ventana);
            return;
        }
        Popup.mostrarAviso(ventana, Popup.Tipo.ERROR, titulo, excepcion.getMessage());
    }

    public static boolean atender(Frame ventana, Throwable error) {
        Throwable actual = error;
        while (actual != null) {
            if (actual instanceof ReservaAppException excepcion) {
                mostrar(ventana, "No se pudo completar la acción", excepcion);
                return true;
            }
            actual = actual.getCause() == actual ? null : actual.getCause();
        }
        return false;
    }

    public static void conexionPerdida() {
        Runnable accion = recuperarConexion;
        if (accion == null) {
            return;
        }
        ejecutarEnVistaUnaVez(accion);
    }

    private static void sesionNoIniciada(Frame ventana) {
        ejecutarEnVistaUnaVez(() -> {
            SesionControl.obtenerInstancia().cerrarSesion();
            volverALogin.run();
            Popup.mostrarAviso(
                    ventana,
                    Popup.Tipo.ERROR,
                    "Sesión no iniciada",
                    "Debe iniciar sesión para continuar."
            );
        });
    }

    private static void ejecutarEnVistaUnaVez(Runnable accion) {
        Runnable protegida = () -> {
            if (!ATENDIENDO.compareAndSet(false, true)) {
                return;
            }
            try {
                accion.run();
            } finally {
                ATENDIENDO.set(false);
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            protegida.run();
        } else {
            SwingUtilities.invokeLater(protegida);
        }
    }
}
