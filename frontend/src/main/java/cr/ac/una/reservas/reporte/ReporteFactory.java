package cr.ac.una.reservas.reporte;


public final class ReporteFactory {
    public enum TipoReporte {
        FUNCIONARIOS,
        CATEGORIAS,
        RECURSOS,
        RESERVAS,
        CALENDARIZACION,
        ACTIVIDADES,
        ESTADISTICAS
    }

    private ReporteFactory() {
    }

    public static GeneradorReporte<?> obtenerGenerador(TipoReporte tipoReporte) {
        switch (tipoReporte) {
            case FUNCIONARIOS:
                return new ReporteFuncionarios();
            case CATEGORIAS:
                return new ReporteCategorias();
            case RECURSOS:
                return new ReporteRecursos();
            case RESERVAS:
                return new ReporteReservas();
            case CALENDARIZACION:
                return new ReporteCalendarizacion();
            case ACTIVIDADES:
                return new ReporteActividades();
            case ESTADISTICAS:
                return new ReporteEstadisticas();
            default:
                throw new IllegalStateException(
                        "Todavía no existe un generador de reporte para " + tipoReporte + "."
                );
        }
    }
}
