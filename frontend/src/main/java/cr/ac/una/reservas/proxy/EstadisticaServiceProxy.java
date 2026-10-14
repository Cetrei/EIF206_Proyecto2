package cr.ac.una.reservas.proxy;

import cr.ac.una.reservas.estadistica.EstadisticaCategoria;
import cr.ac.una.reservas.estadistica.EstadisticaSemana;
import cr.ac.una.reservas.estadistica.EstadisticaService;
import cr.ac.una.reservas.protocol.Accion;
import cr.ac.una.reservas.protocol.Rutas;

import java.time.LocalDate;
import java.util.List;

public final class EstadisticaServiceProxy extends ProxyBase implements EstadisticaService {

    public EstadisticaServiceProxy(ConexionBackend conexion) {
        super(conexion);
    }

    @Override
    public List<EstadisticaCategoria> recursosReservadosEnPeriodo(LocalDate desde, LocalDate hasta) {
        return lista(llamar(Accion.GET, Rutas.ESTADISTICAS_RECURSOS, argumentos(desde, hasta)));
    }

    @Override
    public List<EstadisticaSemana> actividadesPorSemanaEnPeriodo(LocalDate desde, LocalDate hasta) {
        return lista(llamar(Accion.GET, Rutas.ESTADISTICAS_SEMANAS, argumentos(desde, hasta)));
    }
}
