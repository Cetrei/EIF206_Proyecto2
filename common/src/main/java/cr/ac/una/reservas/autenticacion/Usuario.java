package cr.ac.una.reservas.autenticacion;

import cr.ac.una.reservas.funcionario.Funcionario;

import java.io.Serializable;

public abstract class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String id;

    protected String clave;

    protected RolUsuario rol;

    public Usuario() {
    }
    public Usuario(String id, String clave, RolUsuario rol) {
        this.id = id;
        this.clave = clave;
        this.rol = rol;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getClave() {
        return clave;
    }
    public void setClave(String clave) {
        this.clave = clave;
    }

    public RolUsuario getRol() {
        return rol;
    }
    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }

    @Override
    public String toString() {
        return String.format("Usuario{id=%s, rol=%s}", id, rol);
    }
}
