package cr.ac.una.reservas.proxy;

import cr.ac.una.reservas.autenticacion.AutenticacionService;
import cr.ac.una.reservas.categoria.CategoriaService;
import cr.ac.una.reservas.estadistica.EstadisticaService;
import cr.ac.una.reservas.funcionario.FuncionarioService;
import cr.ac.una.reservas.ia.ConfiguracionIaService;
import cr.ac.una.reservas.ia.ExtractorReservaService;
import cr.ac.una.reservas.recurso.RecursoService;
import cr.ac.una.reservas.reserva.ReservaService;

public final class ServiceFactory {
    private static volatile ProveedorServicios proveedor;

    private ServiceFactory() {
    }

    public static void instalar(ProveedorServicios nuevoProveedor) {
        proveedor = nuevoProveedor;
    }

    public static boolean hayProveedor() {
        return proveedor != null;
    }

    public static CategoriaService obtenerCategoriaService() {
        return actual().categoria();
    }

    public static ReservaService obtenerReservaService() {
        return actual().reserva();
    }

    public static RecursoService obtenerRecursoService() {
        return actual().recurso();
    }

    public static FuncionarioService obtenerFuncionarioService() {
        return actual().funcionario();
    }

    public static AutenticacionService obtenerAutenticacionService() {
        return actual().autenticacion();
    }

    public static EstadisticaService obtenerEstadisticaService() {
        return actual().estadistica();
    }

    public static ConfiguracionIaService obtenerConfiguracionIaService() {
        return actual().configuracionIa();
    }

    public static ExtractorReservaService obtenerExtractorReservaService() {
        return actual().extractorReserva();
    }

    private static ProveedorServicios actual() {
        ProveedorServicios actual = proveedor;
        if (actual == null) {
            throw new IllegalStateException("No hay un proveedor de servicios instalado.");
        }
        return actual;
    }
}
