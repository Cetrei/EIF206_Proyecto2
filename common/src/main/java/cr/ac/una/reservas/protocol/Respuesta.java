package cr.ac.una.reservas.protocol;

import cr.ac.una.reservas.excepcion.ReservaAppException;

import java.io.Serializable;

public final class Respuesta implements Serializable {
    private static final long serialVersionUID = 1L;

    private final long id;
    private final EstadoRespuesta estado;
    private final Serializable cuerpo;
    private final ReservaAppException error;

    private Respuesta(long id, EstadoRespuesta estado, Serializable cuerpo, ReservaAppException error) {
        this.id = id;
        this.estado = estado;
        this.cuerpo = cuerpo;
        this.error = error;
    }

    public static Respuesta exito(long id, Serializable cuerpo) {
        return new Respuesta(id, EstadoRespuesta.OK, cuerpo, null);
    }

    public static Respuesta exito(long id) {
        return new Respuesta(id, EstadoRespuesta.OK, null, null);
    }

    public static Respuesta fracaso(long id, ReservaAppException error) {
        return new Respuesta(id, EstadoRespuesta.ERROR, null, error);
    }

    public long getId() {
        return id;
    }

    public EstadoRespuesta getEstado() {
        return estado;
    }

    public boolean isExitosa() {
        return estado == EstadoRespuesta.OK;
    }

    public Serializable getCuerpo() {
        return cuerpo;
    }

    public ReservaAppException getError() {
        return error;
    }

    @Override
    public String toString() {
        return String.format("Respuesta{id=%d, estado=%s}", id, estado);
    }
}
