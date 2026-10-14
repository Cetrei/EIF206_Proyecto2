package cr.ac.una.reservas.servidor;

import cr.ac.una.reservas.autenticacion.AutenticacionService;
import cr.ac.una.reservas.autenticacion.Usuario;
import cr.ac.una.reservas.protocol.Accion;
import cr.ac.una.reservas.protocol.Rutas;

import java.io.Serializable;

final class RutasAutenticacion {
    private RutasAutenticacion() {
    }

    static void registrar(Despachador despachador, AutenticacionService servicio) {
        despachador.registrar(Accion.POST, Rutas.SESION, (cuerpo, sesion) -> {
            Serializable[] argumentos = (Serializable[]) cuerpo;
            Usuario usuario = servicio.autenticar((String) argumentos[0], (String) argumentos[1]);
            sesion.iniciar(usuario);
            return usuario;
        });
        despachador.registrar(Accion.DELETE, Rutas.SESION, (cuerpo, sesion) -> {
            sesion.cerrar();
            return null;
        });
        despachador.registrar(Accion.PUT, Rutas.CLAVE, (cuerpo, sesion) -> {
            Serializable[] argumentos = (Serializable[]) cuerpo;
            servicio.cambiarClave((String) argumentos[0], (String) argumentos[1], (String) argumentos[2]);
            return null;
        });
    }
}
