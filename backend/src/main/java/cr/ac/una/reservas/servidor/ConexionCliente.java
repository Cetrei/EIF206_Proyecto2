package cr.ac.una.reservas.servidor;

import cr.ac.una.reservas.protocol.Notificacion;
import cr.ac.una.reservas.protocol.Peticion;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

final class ConexionCliente implements Runnable, AutoCloseable {
    private static final Logger REGISTRO = Logger.getLogger(ConexionCliente.class.getName());

    private final Socket socket;
    private final Despachador despachador;
    private final Consumer<ConexionCliente> alCerrar;
    private final SesionCliente sesion = new SesionCliente();
    private final ReentrantLock bloqueoEscritura = new ReentrantLock();
    private volatile ObjectOutputStream salida;

    ConexionCliente(Socket socket, Despachador despachador, Consumer<ConexionCliente> alCerrar) {
        this.socket = socket;
        this.despachador = despachador;
        this.alCerrar = alCerrar;
    }

    boolean tieneSesion() {
        return sesion.tieneSesion();
    }

    boolean esDelUsuario(String idUsuario) {
        var usuario = sesion.getUsuario();
        return usuario != null && usuario.getId().equals(idUsuario);
    }

    void enviar(Notificacion notificacion) {
        escribir(notificacion);
    }

    void terminarSesion(Notificacion aviso) {
        sesion.cerrar();
        escribir(aviso);
    }

    @Override
    public void run() {
        try (socket) {
            ObjectOutputStream flujoSalida = new ObjectOutputStream(socket.getOutputStream());
            flujoSalida.flush();
            salida = flujoSalida;
            ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
            while (!socket.isClosed()) {
                Object recibido = entrada.readObject();
                if (recibido instanceof Peticion peticion) {
                    escribir(despachador.despachar(peticion, sesion));
                }
            }
        } catch (IOException | ClassNotFoundException excepcion) {
            if (!socket.isClosed()) {
                REGISTRO.log(Level.FINE, "Conexión de cliente terminada", excepcion);
            }
        } finally {
            sesion.cerrar();
            alCerrar.accept(this);
        }
    }

    private void escribir(Serializable mensaje) {
        ObjectOutputStream flujo = salida;
        if (flujo == null) {
            return;
        }
        bloqueoEscritura.lock();
        try {
            flujo.writeUnshared(mensaje);
            flujo.reset();
            flujo.flush();
        } catch (IOException excepcion) {
            REGISTRO.log(Level.FINE, "No se pudo escribir al cliente", excepcion);
            cerrarSilenciosamente();
        } finally {
            bloqueoEscritura.unlock();
        }
    }

    @Override
    public void close() {
        cerrarSilenciosamente();
    }

    private void cerrarSilenciosamente() {
        try {
            socket.close();
        } catch (IOException excepcion) {
            REGISTRO.log(Level.FINE, "Error al cerrar el socket del cliente", excepcion);
        }
    }
}
