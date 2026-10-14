package cr.ac.una.reservas.persistence.falso;

import cr.ac.una.reservas.autenticacion.Administrador;
import cr.ac.una.reservas.persistence.AdministradorDao;

public class AdministradorDaoFalso extends DaoFalso<Administrador, String> implements AdministradorDao {
    public AdministradorDaoFalso() {
        super(Administrador::getId);
    }
}
