package cr.ac.una.reservas.reporte;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ReporteFactoryTest {

    @Test
    void devuelveReporteDeFuncionariosParaElTipoFuncionarios() {
        GeneradorReporte<?> generador = ReporteFactory.obtenerGenerador(ReporteFactory.TipoReporte.FUNCIONARIOS);
        assertInstanceOf(ReporteFuncionarios.class, generador);
    }

    @Test
    void devuelveReporteDeCategoriasParaElTipoCategorias() {
        GeneradorReporte<?> generador = ReporteFactory.obtenerGenerador(ReporteFactory.TipoReporte.CATEGORIAS);
        assertInstanceOf(ReporteCategorias.class, generador);
    }

    @Test
    void devuelveReporteDeRecursosParaElTipoRecursos() {
        GeneradorReporte<?> generador = ReporteFactory.obtenerGenerador(ReporteFactory.TipoReporte.RECURSOS);
        assertInstanceOf(ReporteRecursos.class, generador);
    }

    @Test
    void devuelveReporteDeReservasParaElTipoReservas() {
        GeneradorReporte<?> generador = ReporteFactory.obtenerGenerador(ReporteFactory.TipoReporte.RESERVAS);
        assertInstanceOf(ReporteReservas.class, generador);
    }

    @Test
    void devuelveReporteDeCalendarizacionParaElTipoCalendarizacion() {
        GeneradorReporte<?> generador = ReporteFactory.obtenerGenerador(ReporteFactory.TipoReporte.CALENDARIZACION);
        assertInstanceOf(ReporteCalendarizacion.class, generador);
    }

    @Test
    void devuelveReporteDeActividadesParaElTipoActividades() {
        GeneradorReporte<?> generador = ReporteFactory.obtenerGenerador(ReporteFactory.TipoReporte.ACTIVIDADES);
        assertInstanceOf(ReporteActividades.class, generador);
    }

    @Test
    void devuelveReporteDeEstadisticasParaElTipoEstadisticas() {
        GeneradorReporte<?> generador = ReporteFactory.obtenerGenerador(ReporteFactory.TipoReporte.ESTADISTICAS);
        assertInstanceOf(ReporteEstadisticas.class, generador);
    }

    @Test
    void cadaLlamadaDevuelveUnaInstanciaNueva() {
        GeneradorReporte<?> primera = ReporteFactory.obtenerGenerador(ReporteFactory.TipoReporte.CATEGORIAS);
        GeneradorReporte<?> segunda = ReporteFactory.obtenerGenerador(ReporteFactory.TipoReporte.CATEGORIAS);

        org.junit.jupiter.api.Assertions.assertNotSame(primera, segunda);
    }
}
