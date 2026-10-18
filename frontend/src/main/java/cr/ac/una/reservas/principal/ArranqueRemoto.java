package cr.ac.una.reservas.principal;

import cr.ac.una.reservas.autenticacion.SesionControl;
import cr.ac.una.reservas.excepcion.ConexionException;
import cr.ac.una.reservas.excepcion.ReservaAppException;
import cr.ac.una.reservas.proxy.ConexionBackend;
import cr.ac.una.reservas.proxy.ConfiguracionConexion;
import cr.ac.una.reservas.proxy.EnrutadorNotificaciones;
import cr.ac.una.reservas.proxy.ProveedorRemoto;
import cr.ac.una.reservas.proxy.ServiceFactory;
import cr.ac.una.reservas.ui.componentes.Popup;

import javax.swing.JFrame;
import java.util.List;

public final class ArranqueRemoto {
    private ArranqueRemoto() {
    }

    public static void iniciar(JFrame ventana, Runnable mostrarLogin) {
        ConfiguracionConexion configuracion = cargarConfiguracion(ventana);
        ConexionBackend conexion = new ConexionBackend(
                configuracion.host(), configuracion.puerto(), EnrutadorNotificaciones.obtenerInstancia()
        );
        ProveedorRemoto proveedor = new ProveedorRemoto(conexion);
        conectarOSalir(ventana, conexion);
        ServiceFactory.instalar(proveedor);
        SesionControl.obtenerInstancia().alCerrarSesion(() -> cerrarSesionRemota(proveedor));
        ReaccionExcepcion.instalar(() -> recuperarConexion(ventana, conexion, mostrarLogin), mostrarLogin);
        conexion.alPerderConexion(ReaccionExcepcion::conexionPerdida);
    }

    private static ConfiguracionConexion cargarConfiguracion(JFrame ventana) {
        try {
            return ConfiguracionConexion.cargar();
        } catch (ConexionException excepcion) {
            Popup.mostrarAviso(ventana, Popup.Tipo.ERROR, "Configuración inválida", excepcion.getMessage());
            System.exit(1);
            return null;
        }
    }

    private static void conectarOSalir(JFrame ventana, ConexionBackend conexion) {
        boolean conectado = false;
        while (!conectado) {
            try {
                conexion.conectar();
                conectado = true;
            } catch (ConexionException excepcion) {
                if (!preguntarReintento(ventana, "No se pudo conectar", excepcion.getMessage())) {
                    System.exit(0);
                }
            }
        }
    }

    private static void recuperarConexion(JFrame ventana, ConexionBackend conexion, Runnable mostrarLogin) {
        if (conexion.estaConectado()) {
            if (!preguntarReintento(ventana, "El servidor no responde", "El servidor no respondió a tiempo.")) {
                System.exit(0);
            }
            return;
        }
        conectarOSalir(ventana, conexion);
        SesionControl.obtenerInstancia().cerrarSesion();
        mostrarLogin.run();
    }

    private static void cerrarSesionRemota(ProveedorRemoto proveedor) {
        EnrutadorNotificaciones.obtenerInstancia().quitarDestino();
        try {
            proveedor.cerrarSesion();
        } catch (ReservaAppException excepcion) {
            return;
        }
    }

    private static boolean preguntarReintento(JFrame ventana, String titulo, String mensaje) {
        boolean[] reintentar = {false};
        Popup popup = new Popup(ventana);
        popup.setTipo(Popup.Tipo.ERROR);
        popup.setTitulo(titulo);
        popup.setMensaje(mensaje);
        popup.setAcciones(List.of(
                new Popup.AccionPopup("Salir", false, null),
                new Popup.AccionPopup("Reintentar", true, () -> reintentar[0] = true)
        ));
        popup.mostrar();
        return reintentar[0];
    }
}
