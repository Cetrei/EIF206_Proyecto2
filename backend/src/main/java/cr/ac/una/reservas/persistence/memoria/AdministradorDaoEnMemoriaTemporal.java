package cr.ac.una.reservas.persistence.memoria;

import cr.ac.una.reservas.autenticacion.Administrador;
import cr.ac.una.reservas.persistence.AdministradorDao;

public class AdministradorDaoEnMemoriaTemporal extends DaoEnMemoriaTemporal<Administrador, String> implements AdministradorDao {
    public AdministradorDaoEnMemoriaTemporal() {
        super(Administrador::getId);
    }
}
