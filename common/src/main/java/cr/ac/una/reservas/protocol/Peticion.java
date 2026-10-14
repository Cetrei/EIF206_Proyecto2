package cr.ac.una.reservas.protocol;

import java.io.Serializable;

public final class Peticion implements Serializable {
    private static final long serialVersionUID = 1L;

    private final long id;
    private final Accion accion;
    private final String ruta;
    private final Serializable cuerpo;

    public Peticion(long id, Accion accion, String ruta, Serializable cuerpo) {
        this.id = id;
        this.accion = accion;
        this.ruta = ruta;
        this.cuerpo = cuerpo;
    }

    public long getId() {
        return id;
    }

    public Accion getAccion() {
        return accion;
    }

    public String getRuta() {
        return ruta;
    }

    public Serializable getCuerpo() {
        return cuerpo;
    }

    @Override
    public String toString() {
        return String.format("Peticion{id=%d, accion=%s, ruta=%s}", id, accion, ruta);
    }
}
