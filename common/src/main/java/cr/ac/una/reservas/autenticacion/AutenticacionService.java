package cr.ac.una.reservas.autenticacion;

public interface AutenticacionService {

    Usuario autenticar(String id, String clave);
    void cambiarClave(String idUsuario, String claveActual, String claveNueva);
}
