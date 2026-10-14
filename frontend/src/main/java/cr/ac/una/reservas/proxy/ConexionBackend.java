package cr.ac.una.reservas.proxy;

import cr.ac.una.reservas.excepcion.ConexionException;
import cr.ac.una.reservas.protocol.Accion;
import cr.ac.una.reservas.protocol.Notificacion;
import cr.ac.una.reservas.protocol.Peticion;
import cr.ac.una.reservas.protocol.Respuesta;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ConexionBackend implements AutoCloseable {
    private static final Logger REGISTRO = Logger.getLogger(ConexionBackend.class.getName());
    private static final int TIEMPO_CONEXION_MS = 5000;
    private static final long TIEMPO_RESPUESTA_SEGUNDOS = 30;
    private static final String MENSAJE_SIN_CONEXION = "No hay conexión con el servidor.";

    private final String host;
    private final int puerto;
    private final Consumer<Notificacion> receptor;
    private final Map<Long, CompletableFuture<Respuesta>> pendientes = new ConcurrentHashMap<>();
    private final AtomicLong siguienteId = new AtomicLong(1);
    private final ReentrantLock bloqueoEscritura = new ReentrantLock();
    private volatile Runnable alPerderConexion = () -> { };
    private volatile Socket socket;
    private volatile ObjectOutputStream salida;

    public ConexionBackend(String host, int puerto, Consumer<Notificacion> receptor) {
        this.host = host;
        this.puerto = puerto;
        this.receptor = receptor;
    }

    public void alPerderConexion(Runnable accion) {
        this.alPerderConexion = accion;
    }

    public synchronized void conectar() {
        if (estaConectado()) {
            return;
        }
        Socket nuevo = new Socket();
        try {
            nuevo.connect(new InetSocketAddress(host, puerto), TIEMPO_CONEXION_MS);
            ObjectOutputStream flujoSalida = new ObjectOutputStream(nuevo.getOutputStream());
            flujoSalida.flush();
            ObjectInputStream entrada = new ObjectInputStream(nuevo.getInputStream());
            socket = nuevo;
            salida = flujoSalida;
            Thread.ofPlatform().daemon().name("lector-backend").start(() -> leer(nuevo, entrada));
        } catch (IOException excepcion) {
            cerrarSocket(nuevo);
            throw new ConexionException("No se pudo conectar con el servidor.", excepcion);
        }
    }

    public boolean estaConectado() {
        Socket actual = socket;
        return actual != null && !actual.isClosed();
    }

    public Serializable enviar(Accion accion, String ruta, Serializable cuerpo) {
        long id = siguienteId.getAndIncrement();
        CompletableFuture<Respuesta> futuro = new CompletableFuture<>();
        pendientes.put(id, futuro);
        try {
            escribir(new Peticion(id, accion, ruta, cuerpo));
            Respuesta respuesta = futuro.get(TIEMPO_RESPUESTA_SEGUNDOS, TimeUnit.SECONDS);
            if (!respuesta.isExitosa()) {
                throw respuesta.getError();
            }
            return respuesta.getCuerpo();
        } catch (TimeoutException excepcion) {
            throw new ConexionException("El servidor no respondió a tiempo.", excepcion);
        } catch (InterruptedException excepcion) {
            Thread.currentThread().interrupt();
            throw new ConexionException("La operación fue interrumpida.", excepcion);
        } catch (ExecutionException excepcion) {
            if (excepcion.getCause() instanceof ConexionException conexion) {
                throw conexion;
            }
            throw new ConexionException(MENSAJE_SIN_CONEXION, excepcion);
        } finally {
            pendientes.remove(id);
        }
    }

    private void escribir(Peticion peticion) {
        ObjectOutputStream flujo = salida;
        if (flujo == null || !estaConectado()) {
            throw new ConexionException(MENSAJE_SIN_CONEXION);
        }
        bloqueoEscritura.lock();
        try {
            flujo.writeUnshared(peticion);
            flujo.reset();
            flujo.flush();
        } catch (IOException excepcion) {
            cerrarConexion();
            throw new ConexionException(MENSAJE_SIN_CONEXION, excepcion);
        } finally {
            bloqueoEscritura.unlock();
        }
    }

    private void leer(Socket origen, ObjectInputStream entrada) {
        try {
            while (!origen.isClosed()) {
                Object recibido = entrada.readObject();
                if (recibido instanceof Respuesta respuesta) {
                    CompletableFuture<Respuesta> futuro = pendientes.get(respuesta.getId());
                    if (futuro != null) {
                        futuro.complete(respuesta);
                    }
                } else if (recibido instanceof Notificacion notificacion) {
                    receptor.accept(notificacion);
                }
            }
        } catch (IOException | ClassNotFoundException excepcion) {
            if (origen == socket) {
                cerrarConexion();
                alPerderConexion.run();
            }
        }
    }

    private void cerrarConexion() {
        Socket actual = socket;
        if (actual != null) {
            cerrarSocket(actual);
        }
        ConexionException causa = new ConexionException(MENSAJE_SIN_CONEXION);
        pendientes.values().forEach(futuro -> futuro.completeExceptionally(causa));
    }

    private static void cerrarSocket(Socket aCerrar) {
        try {
            aCerrar.close();
        } catch (IOException excepcion) {
            REGISTRO.log(Level.FINE, "Error al cerrar el socket", excepcion);
        }
    }

    @Override
    public void close() {
        cerrarConexion();
    }
}
