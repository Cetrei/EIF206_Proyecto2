package cr.ac.una.reservas.autenticacion;


public class Administrador extends Usuario {
    private static final long serialVersionUID = 1L;

    public Administrador() {
        super();
    }
    public Administrador(String id, String clave) {
        super(id, clave, RolUsuario.ADMINISTRADOR);
    }

    @Override
    public String toString() {
        return String.format("Administrador{id=%s}", id);
    }
}
