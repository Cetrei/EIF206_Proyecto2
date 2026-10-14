package cr.ac.una.reservas.ia;

import cr.ac.una.reservas.excepcion.ReglaDeNegocioException;
import cr.ac.una.reservas.persistence.ConfiguracionIaDao;
import cr.ac.una.reservas.persistence.DaoFactory;

import java.util.Optional;

public class ConfiguracionIaServiceImpl implements ConfiguracionIaService {
    private final ConfiguracionIaDao configuracionIaDao;

    public ConfiguracionIaServiceImpl() {
        this(DaoFactory.obtenerConfiguracionIaDao());
    }

    public ConfiguracionIaServiceImpl(ConfiguracionIaDao configuracionIaDao) {
        this.configuracionIaDao = configuracionIaDao;
    }

    public Optional<ConfiguracionIa> obtenerConfiguracion() {
        return configuracionIaDao.buscarPorId(ConfiguracionIa.ID_UNICO);
    }

    public void guardar(String apiKey, String modelo) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ReglaDeNegocioException("La API key no puede estar vacia.");
        }
        if (modelo == null || modelo.isBlank()) {
            throw new ReglaDeNegocioException("Debe seleccionar un modelo de IA.");
        }
        ConfiguracionIa configuracion = new ConfiguracionIa(apiKey.trim(), modelo.trim());
        if (!configuracionIaDao.actualizar(configuracion)) {
            configuracionIaDao.insertar(configuracion);
        }
    }
}
