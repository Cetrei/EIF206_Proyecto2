package cr.ac.una.reservas.servidor;

import cr.ac.una.reservas.protocol.Notificacion;
import cr.ac.una.reservas.protocol.SesionTerminada;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ServidorBackend implements AutoCloseable {
    private static final Logger REGISTRO = Logger.getLogger(ServidorBackend.class.getName());
    private static final String MENSAJE_CUENTA_ELIMINADA =
            "Su cuenta fue eliminada por un administrador. Se cerró su sesión.";

    private final Despachador despachador;
    private final Set<ConexionCliente> clientes = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private ServerSocket servidor;
    private ExecutorService hilosDeClientes;
    private Thread hiloAceptador;

    public ServidorBackend(Despachador despachador) {
        this.despachador = despachador;
        despachador.alEliminarFuncionario(this::terminarSesionesDe);
    }

    void terminarSesionesDe(String idUsuario) {
        for (ConexionCliente cliente : clientes) {
            if (cliente.esDelUsuario(idUsuario)) {
                cliente.terminarSesion(new SesionTerminada(MENSAJE_CUENTA_ELIMINADA));
            }
        }
    }

    public void iniciar(int puerto) throws IOException {
        servidor = new ServerSocket(puerto);
        hilosDeClientes = Executors.newVirtualThreadPerTaskExecutor();
        hiloAceptador = Thread.ofPlatform().name("aceptador-de-clientes").start(this::aceptarClientes);
        REGISTRO.info("Servidor escuchando en el puerto " + servidor.getLocalPort());
    }

    public int getPuertoLocal() {
        return servidor.getLocalPort();
    }

    public int cantidadClientes() {
        return clientes.size();
    }

    public void difundir(Notificacion notificacion) {
        for (ConexionCliente cliente : clientes) {
            if (cliente.tieneSesion()) {
                cliente.enviar(notificacion);
            }
        }
    }

    public void difundirExcepto(Notificacion notificacion, String idUsuarioExcluido) {
        for (ConexionCliente cliente : clientes) {
            if (cliente.tieneSesion() && !cliente.esDelUsuario(idUsuarioExcluido)) {
                cliente.enviar(notificacion);
            }
        }
    }

    private void aceptarClientes() {
        while (!servidor.isClosed()) {
            try {
                Socket socket = servidor.accept();
                ConexionCliente cliente = new ConexionCliente(socket, despachador, clientes::remove);
                clientes.add(cliente);
                hilosDeClientes.submit(cliente);
            } catch (IOException excepcion) {
                if (!servidor.isClosed()) {
                    REGISTRO.log(Level.WARNING, "Error al aceptar un cliente", excepcion);
                }
            }
        }
    }

    @Override
    public void close() {
        try {
            servidor.close();
        } catch (IOException excepcion) {
            REGISTRO.log(Level.FINE, "Error al cerrar el servidor", excepcion);
        }
        for (ConexionCliente cliente : clientes) {
            cliente.close();
        }
        hilosDeClientes.close();
        try {
            hiloAceptador.join();
        } catch (InterruptedException excepcion) {
            Thread.currentThread().interrupt();
        }
    }
}
