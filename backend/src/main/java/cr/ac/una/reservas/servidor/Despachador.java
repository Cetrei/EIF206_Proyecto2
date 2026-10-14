package cr.ac.una.reservas.servidor;

import cr.ac.una.reservas.excepcion.ErrorInternoException;
import cr.ac.una.reservas.excepcion.ReservaAppException;
import cr.ac.una.reservas.excepcion.SesionNoIniciadaException;
import cr.ac.una.reservas.persistence.PersistenciaException;
import cr.ac.una.reservas.protocol.Accion;
import cr.ac.una.reservas.protocol.Peticion;
import cr.ac.una.reservas.protocol.Respuesta;
import cr.ac.una.reservas.protocol.Rutas;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Despachador {
    private static final Logger REGISTRO = Logger.getLogger(Despachador.class.getName());

    private static final Set<String> RUTAS_SIN_SESION = Set.of(
            clave(Accion.POST, Rutas.SESION),
            clave(Accion.PUT, Rutas.CLAVE)
    );

    private final Map<String, Ruta> rutas = new HashMap<>();

    public void registrar(Accion accion, String ruta, Ruta manejador) {
        rutas.put(clave(accion, ruta), manejador);
    }

    public Respuesta despachar(Peticion peticion, SesionCliente sesion) {
        String clave = clave(peticion.getAccion(), peticion.getRuta());
        Ruta ruta = rutas.get(clave);
        if (ruta == null) {
            REGISTRO.warning("Ruta desconocida: " + clave);
            return Respuesta.fracaso(peticion.getId(), new ErrorInternoException());
        }
        if (!sesion.tieneSesion() && !RUTAS_SIN_SESION.contains(clave)) {
            return Respuesta.fracaso(
                    peticion.getId(),
                    sinRastro(new SesionNoIniciadaException("Debe iniciar sesión para realizar esta operación."))
            );
        }
        try {
            return Respuesta.exito(peticion.getId(), ruta.ejecutar(peticion.getCuerpo(), sesion));
        } catch (PersistenciaException excepcion) {
            REGISTRO.log(Level.SEVERE, "Falla de persistencia en " + clave, excepcion);
            return Respuesta.fracaso(peticion.getId(), new ErrorInternoException());
        } catch (ReservaAppException excepcion) {
            return Respuesta.fracaso(peticion.getId(), sinRastro(excepcion));
        } catch (RuntimeException excepcion) {
            REGISTRO.log(Level.SEVERE, "Falla no prevista en " + clave, excepcion);
            return Respuesta.fracaso(peticion.getId(), new ErrorInternoException());
        }
    }

    private static String clave(Accion accion, String ruta) {
        return accion + " " + ruta;
    }

    private static ReservaAppException sinRastro(ReservaAppException excepcion) {
        excepcion.setStackTrace(new StackTraceElement[0]);
        return excepcion;
    }
}
