package cr.ac.una.reservas.servidor;

import cr.ac.una.reservas.autenticacion.Usuario;
import cr.ac.una.reservas.backend.BackendServiceFactory;
import cr.ac.una.reservas.ia.ExtractorReservaService;

public final class SesionCliente {
    private volatile Usuario usuario;
    private ExtractorReservaService extractor;

    public boolean tieneSesion() {
        return usuario != null;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void iniciar(Usuario nuevoUsuario) {
        this.usuario = nuevoUsuario;
    }

    public void cerrar() {
        this.usuario = null;
    }

    public synchronized ExtractorReservaService extractor() {
        if (extractor == null) {
            extractor = BackendServiceFactory.obtenerExtractorReservaService();
        }
        return extractor;
    }
}
