package cr.ac.una.reservas.servidor;

import cr.ac.una.reservas.estadistica.EstadisticaService;
import cr.ac.una.reservas.protocol.Accion;
import cr.ac.una.reservas.protocol.Rutas;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

final class RutasEstadistica {
    private RutasEstadistica() {
    }

    static void registrar(Despachador despachador, EstadisticaService servicio) {
        despachador.registrar(Accion.GET, Rutas.ESTADISTICAS_RECURSOS, (cuerpo, sesion) -> {
            Serializable[] periodo = (Serializable[]) cuerpo;
            return new ArrayList<>(
                    servicio.recursosReservadosEnPeriodo((LocalDate) periodo[0], (LocalDate) periodo[1])
            );
        });
        despachador.registrar(Accion.GET, Rutas.ESTADISTICAS_SEMANAS, (cuerpo, sesion) -> {
            Serializable[] periodo = (Serializable[]) cuerpo;
            return new ArrayList<>(
                    servicio.actividadesPorSemanaEnPeriodo((LocalDate) periodo[0], (LocalDate) periodo[1])
            );
        });
    }
}
