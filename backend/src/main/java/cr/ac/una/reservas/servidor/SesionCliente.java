package cr.ac.una.reservas.servidor;

import cr.ac.una.reservas.autenticacion.RolUsuario;
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

    public boolean esAdministrador() {
        Usuario actual = usuario;
        return actual != null && actual.getRol() == RolUsuario.ADMINISTRADOR;
    }

    public boolean esFuncionario() {
        Usuario actual = usuario;
        return actual != null && actual.getRol() == RolUsuario.FUNCIONARIO;
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
