package cr.ac.una.reservas.estadistica;

import cr.ac.una.reservas.excepcion.ReglaDeNegocioException;
import cr.ac.una.reservas.persistence.CategoriaDao;
import cr.ac.una.reservas.persistence.DaoFactory;
import cr.ac.una.reservas.persistence.RecursoDao;
import cr.ac.una.reservas.persistence.ReservaDao;
import cr.ac.una.reservas.recurso.Recurso;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EstadisticaServiceImpl implements EstadisticaService {
    private final ReservaDao reservaDao;
    private final RecursoDao recursoDao;
    private final CategoriaDao categoriaDao;

    public EstadisticaServiceImpl() {
        this(DaoFactory.obtenerReservaDao(), DaoFactory.obtenerRecursoDao(), DaoFactory.obtenerCategoriaDao());
    }

    public EstadisticaServiceImpl(ReservaDao reservaDao, RecursoDao recursoDao, CategoriaDao categoriaDao) {
        this.reservaDao = reservaDao;
        this.recursoDao = recursoDao;
        this.categoriaDao = categoriaDao;
    }

    public List<EstadisticaCategoria> recursosReservadosEnPeriodo(LocalDate desde, LocalDate hasta) {
        validarRango(desde, hasta);

        Map<String, Recurso> recursosPorId = new LinkedHashMap<>();
        for (Recurso recurso : recursoDao.listarTodos()) {
            recursosPorId.put(recurso.getId(), recurso);
        }

        Map<Integer, Long> cantidadPorCategoria = new LinkedHashMap<>();
        reservaDao.contarRecursosReservados(desde, hasta).forEach((idRecurso, cantidad) -> {
            Recurso recurso = recursosPorId.get(idRecurso);
            if (recurso != null) {
                cantidadPorCategoria.merge(recurso.getIdCategoria(), cantidad, Long::sum);
            }
        });

        List<EstadisticaCategoria> resultado = new ArrayList<>();
        for (Map.Entry<Integer, Long> entrada : cantidadPorCategoria.entrySet()) {
            categoriaDao.buscarPorId(entrada.getKey())
                    .ifPresent(categoria -> resultado.add(new EstadisticaCategoria(categoria, entrada.getValue())));
        }
        resultado.sort(Comparator.comparingLong(EstadisticaCategoria::getCantidad).reversed()
                .thenComparing(estadistica -> estadistica.getCategoria().getDescripcion()));
        return resultado;
    }

    public List<EstadisticaSemana> actividadesPorSemanaEnPeriodo(LocalDate desde, LocalDate hasta) {
        validarRango(desde, hasta);

        List<EstadisticaSemana> resultado = new ArrayList<>();
        reservaDao.contarActividadesPorSemana(desde, hasta)
                .forEach((inicioSemana, cantidad) -> resultado.add(new EstadisticaSemana(inicioSemana, cantidad)));
        resultado.sort(Comparator.comparing(EstadisticaSemana::getInicioSemana));
        return resultado;
    }

    private void validarRango(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw new ReglaDeNegocioException("Debe indicar las fechas \"desde\" y \"hasta\".");
        }
        if (hasta.isBefore(desde)) {
            throw new ReglaDeNegocioException("La fecha \"hasta\" no puede ser anterior a la fecha \"desde\".");
        }
    }
}
