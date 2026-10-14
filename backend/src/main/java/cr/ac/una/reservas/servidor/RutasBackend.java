package cr.ac.una.reservas.servidor;

import cr.ac.una.reservas.backend.BackendServiceFactory;

public final class RutasBackend {
    private RutasBackend() {
    }

    public static void instalarDifusor(ServidorBackend servidor) {
        BackendServiceFactory.obtenerReservaService().agregarObservador(new DifusorNotificaciones(
                servidor,
                BackendServiceFactory.obtenerFuncionarioService(),
                BackendServiceFactory.obtenerRecursoService()
        ));
    }

    public static Despachador crearDespachador() {
        Despachador despachador = new Despachador();
        RutasAutenticacion.registrar(despachador, BackendServiceFactory.obtenerAutenticacionService());
        RutasCategoria.registrar(despachador, BackendServiceFactory.obtenerCategoriaService());
        RutasFuncionario.registrar(despachador, BackendServiceFactory.obtenerFuncionarioService());
        RutasRecurso.registrar(despachador, BackendServiceFactory.obtenerRecursoService());
        RutasReserva.registrar(despachador, BackendServiceFactory.obtenerReservaService());
        RutasEstadistica.registrar(despachador, BackendServiceFactory.obtenerEstadisticaService());
        RutasIa.registrar(despachador, BackendServiceFactory.obtenerConfiguracionIaService());
        return despachador;
    }
}
