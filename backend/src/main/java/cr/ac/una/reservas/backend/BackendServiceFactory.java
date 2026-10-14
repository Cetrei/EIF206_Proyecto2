package cr.ac.una.reservas.backend;

import cr.ac.una.reservas.autenticacion.AutenticacionService;
import cr.ac.una.reservas.autenticacion.AutenticacionServiceImpl;
import cr.ac.una.reservas.categoria.CategoriaService;
import cr.ac.una.reservas.categoria.CategoriaServiceImpl;
import cr.ac.una.reservas.estadistica.EstadisticaService;
import cr.ac.una.reservas.estadistica.EstadisticaServiceImpl;
import cr.ac.una.reservas.funcionario.FuncionarioService;
import cr.ac.una.reservas.funcionario.FuncionarioServiceImpl;
import cr.ac.una.reservas.ia.ConfiguracionIaService;
import cr.ac.una.reservas.ia.ConfiguracionIaServiceImpl;
import cr.ac.una.reservas.ia.ExtractorReservaService;
import cr.ac.una.reservas.ia.ExtractorReservaServiceImpl;
import cr.ac.una.reservas.recurso.RecursoService;
import cr.ac.una.reservas.recurso.RecursoServiceImpl;
import cr.ac.una.reservas.reserva.ReservaService;
import cr.ac.una.reservas.reserva.ReservaServiceImpl;

public final class BackendServiceFactory {
    private static final CategoriaService CATEGORIA_SERVICE = new CategoriaServiceImpl();
    private static final ReservaService RESERVA_SERVICE = new ReservaServiceImpl();
    private static final RecursoService RECURSO_SERVICE = new RecursoServiceImpl();
    private static final FuncionarioService FUNCIONARIO_SERVICE = new FuncionarioServiceImpl();
    private static final AutenticacionService AUTENTICACION_SERVICE = new AutenticacionServiceImpl();
    private static final EstadisticaService ESTADISTICA_SERVICE = new EstadisticaServiceImpl();
    private static final ConfiguracionIaService CONFIGURACION_IA_SERVICE = new ConfiguracionIaServiceImpl();

    private BackendServiceFactory() {
    }

    public static CategoriaService obtenerCategoriaService() {
        return CATEGORIA_SERVICE;
    }

    public static ReservaService obtenerReservaService() {
        return RESERVA_SERVICE;
    }

    public static RecursoService obtenerRecursoService() {
        return RECURSO_SERVICE;
    }

    public static FuncionarioService obtenerFuncionarioService() {
        return FUNCIONARIO_SERVICE;
    }

    public static AutenticacionService obtenerAutenticacionService() {
        return AUTENTICACION_SERVICE;
    }

    public static EstadisticaService obtenerEstadisticaService() {
        return ESTADISTICA_SERVICE;
    }

    public static ConfiguracionIaService obtenerConfiguracionIaService() {
        return CONFIGURACION_IA_SERVICE;
    }

    public static ExtractorReservaService obtenerExtractorReservaService() {
        return new ExtractorReservaServiceImpl();
    }
}
