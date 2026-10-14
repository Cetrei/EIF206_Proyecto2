package cr.ac.una.reservas.persistence;

import cr.ac.una.reservas.excepcion.ReservaAppException;

public class PersistenciaException extends ReservaAppException {
    public PersistenciaException(String mensaje) {
        super(mensaje);
    }

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
