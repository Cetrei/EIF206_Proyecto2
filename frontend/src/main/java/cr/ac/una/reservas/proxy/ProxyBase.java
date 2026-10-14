package cr.ac.una.reservas.proxy;

import cr.ac.una.reservas.protocol.Accion;

import java.io.Serializable;
import java.util.List;

abstract class ProxyBase {
    private final ConexionBackend conexion;

    ProxyBase(ConexionBackend conexion) {
        this.conexion = conexion;
    }

    final Serializable llamar(Accion accion, String ruta, Serializable cuerpo) {
        return conexion.enviar(accion, ruta, cuerpo);
    }

    static Serializable[] argumentos(Serializable... valores) {
        return valores;
    }

    @SuppressWarnings("unchecked")
    static <T> List<T> lista(Serializable cuerpo) {
        return (List<T>) cuerpo;
    }
}
