package cr.ac.una.reservas.ia;

import cr.ac.una.reservas.autenticacion.SesionControl;
import cr.ac.una.reservas.excepcion.ConexionException;
import cr.ac.una.reservas.excepcion.ReglaDeNegocioException;
import cr.ac.una.reservas.excepcion.ReservaAppException;
import cr.ac.una.reservas.excepcion.SesionNoIniciadaException;
import cr.ac.una.reservas.principal.ReaccionExcepcion;
import cr.ac.una.reservas.proxy.ServiceFactory;
import cr.ac.una.reservas.ui.componentes.Popup;

import java.awt.Frame;
import java.util.Optional;

public class ConfiguracionIaControl {
    private final TabConfiguracionIa vista;
    private final Frame ventanaPropietaria;
    private final ConfiguracionIaService configuracionIaService;

    public ConfiguracionIaControl(TabConfiguracionIa vista, Frame ventanaPropietaria) {
        this(vista, ventanaPropietaria, ServiceFactory.obtenerConfiguracionIaService());
    }

    public ConfiguracionIaControl(
            TabConfiguracionIa vista, Frame ventanaPropietaria, ConfiguracionIaService configuracionIaService
    ) {
        if (!SesionControl.obtenerInstancia().esAdministrador()) {
            throw new ReglaDeNegocioException("Esta funcionalidad solo está disponible para administradores.");
        }

        this.vista = vista;
        this.ventanaPropietaria = ventanaPropietaria;
        this.configuracionIaService = configuracionIaService;

        this.vista.alGuardar(this::guardar);

        cargarConfiguracionActual();
    }

    private void cargarConfiguracionActual() {
        Optional<ConfiguracionIa> configuracion = configuracionIaService.obtenerConfiguracion();
        if (configuracion.isPresent()) {
            vista.mostrarApiKey(configuracion.get().getApiKey());
            vista.mostrarModelo(configuracion.get().getModelo());
        } else {
            vista.mostrarModelo(TabConfiguracionIa.MODELO_GEMINI_3_FLASH_PREVIEW);
        }
    }

    private void guardar() {
        try {
            String apiKey = vista.obtenerApiKey();
            configuracionIaService.guardar(apiKey == null ? null : apiKey.trim(), vista.obtenerModelo());
            Popup.mostrarAviso(
                    ventanaPropietaria,
                    Popup.Tipo.CONFIRMACION,
                    "Configuración de IA",
                    "La configuración se guardó correctamente."
            );
        } catch (ReservaAppException excepcion) {
            if (excepcion instanceof ConexionException || excepcion instanceof SesionNoIniciadaException) {
                ReaccionExcepcion.mostrar(ventanaPropietaria, "No se pudo guardar", excepcion);
                return;
            }
            Popup.mostrarAviso(
                    ventanaPropietaria, Popup.Tipo.ERROR, "No se pudo guardar", excepcion.getMessage()
            );
        }
    }
}
