package cr.ac.una.reservas.ia;

import java.util.Optional;

public interface ConfiguracionIaService {

    Optional<ConfiguracionIa> obtenerConfiguracion();
    void guardar(String apiKey, String modelo);
}
