package cr.ac.una.reservas.servidor;

import cr.ac.una.reservas.excepcion.AutorizacionException;
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
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Despachador {
    private static final Logger REGISTRO = Logger.getLogger(Despachador.class.getName());

    private static final Set<String> RUTAS_SIN_SESION = Set.of(
            clave(Accion.POST, Rutas.SESION),
            clave(Accion.PUT, Rutas.CLAVE)
    );

    private static final String MENSAJE_SIN_PERMISO = "No tiene permiso para realizar esta operación.";
    private static final Set<String> RUTAS_SOLO_ADMINISTRADOR = Set.of(
            clave(Accion.GET, Rutas.FUNCIONARIOS),
            clave(Accion.GET, Rutas.FUNCIONARIOS_POR_ID),
            clave(Accion.GET, Rutas.FUNCIONARIOS_BUSCAR),
            clave(Accion.POST, Rutas.FUNCIONARIOS),
            clave(Accion.DELETE, Rutas.FUNCIONARIOS),
            clave(Accion.POST, Rutas.CATEGORIAS),
            clave(Accion.PUT, Rutas.CATEGORIAS),
            clave(Accion.DELETE, Rutas.CATEGORIAS),
            clave(Accion.POST, Rutas.RECURSOS),
            clave(Accion.PUT, Rutas.RECURSOS),
            clave(Accion.DELETE, Rutas.RECURSOS),
            clave(Accion.GET, Rutas.IA_CONFIGURACION),
            clave(Accion.PUT, Rutas.IA_CONFIGURACION)
    );
    private static final Set<String> RUTAS_SOLO_FUNCIONARIO = Set.of(
            clave(Accion.POST, Rutas.RESERVAS),
            clave(Accion.PUT, Rutas.RESERVAS)
    );

    private final Map<String, Ruta> rutas = new HashMap<>();
    private volatile Consumer<String> alEliminarFuncionario = idFuncionario -> { };

    public void alEliminarFuncionario(Consumer<String> accion) {
        this.alEliminarFuncionario = accion;
    }

    void funcionarioEliminado(String idFuncionario) {
        alEliminarFuncionario.accept(idFuncionario);
    }

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
        boolean sinPermiso = (RUTAS_SOLO_ADMINISTRADOR.contains(clave) && !sesion.esAdministrador())
                || (RUTAS_SOLO_FUNCIONARIO.contains(clave) && !sesion.esFuncionario());
        if (sinPermiso) {
            return Respuesta.fracaso(peticion.getId(), sinRastro(new AutorizacionException(MENSAJE_SIN_PERMISO)));
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
