package cr.ac.una.reservas.servidor;

import java.io.Serializable;

@FunctionalInterface
public interface Ruta {
    Serializable ejecutar(Serializable cuerpo, SesionCliente sesion);
}
