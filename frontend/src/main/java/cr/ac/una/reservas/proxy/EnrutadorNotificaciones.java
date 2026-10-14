package cr.ac.una.reservas.proxy;

import cr.ac.una.reservas.protocol.Notificacion;

import java.util.function.Consumer;

public final class EnrutadorNotificaciones implements Consumer<Notificacion> {
    private static final EnrutadorNotificaciones INSTANCIA = new EnrutadorNotificaciones();

    private volatile Consumer<Notificacion> destino = notificacion -> { };

    private EnrutadorNotificaciones() {
    }

    public static EnrutadorNotificaciones obtenerInstancia() {
        return INSTANCIA;
    }

    public void establecerDestino(Consumer<Notificacion> nuevoDestino) {
        this.destino = nuevoDestino;
    }

    public void quitarDestino() {
        this.destino = notificacion -> { };
    }

    @Override
    public void accept(Notificacion notificacion) {
        destino.accept(notificacion);
    }
}
