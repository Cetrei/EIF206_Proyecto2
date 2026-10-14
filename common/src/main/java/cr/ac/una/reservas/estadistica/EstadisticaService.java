package cr.ac.una.reservas.estadistica;

import java.time.LocalDate;
import java.util.List;

public interface EstadisticaService {

    List<EstadisticaCategoria> recursosReservadosEnPeriodo(LocalDate desde, LocalDate hasta);
    List<EstadisticaSemana> actividadesPorSemanaEnPeriodo(LocalDate desde, LocalDate hasta);
}
