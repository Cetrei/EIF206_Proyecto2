package cr.ac.una.reservas.proxy;

import cr.ac.una.reservas.ia.ConfiguracionIa;
import cr.ac.una.reservas.ia.ConfiguracionIaService;
import cr.ac.una.reservas.protocol.Accion;
import cr.ac.una.reservas.protocol.Rutas;

import java.util.Optional;

public final class ConfiguracionIaServiceProxy extends ProxyBase implements ConfiguracionIaService {

    public ConfiguracionIaServiceProxy(ConexionBackend conexion) {
        super(conexion);
    }

    @Override
    public Optional<ConfiguracionIa> obtenerConfiguracion() {
        return Optional.ofNullable((ConfiguracionIa) llamar(Accion.GET, Rutas.IA_CONFIGURACION, null));
    }

    @Override
    public void guardar(String apiKey, String modelo) {
        llamar(Accion.PUT, Rutas.IA_CONFIGURACION, argumentos(apiKey, modelo));
    }
}
