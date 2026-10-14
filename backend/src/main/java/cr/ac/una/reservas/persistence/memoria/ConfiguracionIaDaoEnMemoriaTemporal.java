package cr.ac.una.reservas.persistence.memoria;

import cr.ac.una.reservas.ia.ConfiguracionIa;
import cr.ac.una.reservas.persistence.ConfiguracionIaDao;

public class ConfiguracionIaDaoEnMemoriaTemporal extends DaoEnMemoriaTemporal<ConfiguracionIa, String> implements ConfiguracionIaDao {
    public ConfiguracionIaDaoEnMemoriaTemporal() {
        super(ConfiguracionIa::getId);
    }

    @Override
    public synchronized ConfiguracionIa insertar(ConfiguracionIa entidad) {
        entidad.setId(ConfiguracionIa.ID_UNICO);
        eliminar(ConfiguracionIa.ID_UNICO);
        return super.insertar(entidad);
    }

    @Override
    public synchronized boolean actualizar(ConfiguracionIa entidad) {
        entidad.setId(ConfiguracionIa.ID_UNICO);
        return super.actualizar(entidad);
    }
}
