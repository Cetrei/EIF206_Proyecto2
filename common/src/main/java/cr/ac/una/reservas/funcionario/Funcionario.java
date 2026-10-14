package cr.ac.una.reservas.funcionario;

import cr.ac.una.reservas.autenticacion.RolUsuario;
import cr.ac.una.reservas.autenticacion.Usuario;


public class Funcionario extends Usuario {
    private static final long serialVersionUID = 1L;

    private String nombre;

    private String telefono;

    public Funcionario() {
        super();
    }
    public Funcionario(String id, String clave, String nombre, String telefono) {
        super(id, clave, RolUsuario.FUNCIONARIO);
        this.nombre = nombre;
        this.telefono = telefono;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return String.format("Funcionario{id=%s, nombre=%s, telefono=%s}", id, nombre, telefono);
    }
}
