package cr.ac.una.reservas.proxy;

import cr.ac.una.reservas.autenticacion.AutenticacionService;
import cr.ac.una.reservas.autenticacion.Usuario;
import cr.ac.una.reservas.protocol.Accion;
import cr.ac.una.reservas.protocol.Rutas;

public final class AutenticacionServiceProxy extends ProxyBase implements AutenticacionService {

    public AutenticacionServiceProxy(ConexionBackend conexion) {
        super(conexion);
    }

    @Override
    public Usuario autenticar(String id, String clave) {
        return (Usuario) llamar(Accion.POST, Rutas.SESION, argumentos(id, clave));
    }

    @Override
    public void cambiarClave(String idUsuario, String claveActual, String claveNueva) {
        llamar(Accion.PUT, Rutas.CLAVE, argumentos(idUsuario, claveActual, claveNueva));
    }

    public void cerrarSesion() {
        llamar(Accion.DELETE, Rutas.SESION, null);
    }
}
