package cr.ac.una.reservas.reporte;

import cr.ac.una.reservas.excepcion.ReservaAppException;

public class ReporteException extends ReservaAppException {
    public ReporteException(String mensaje) {
        super(mensaje);
    }

    public ReporteException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
