package cr.ac.una.reservas.reserva;

import cr.ac.una.reservas.categoria.Categoria;
import cr.ac.una.reservas.persistence.memoria.CategoriaDaoEnMemoriaTemporal;
import cr.ac.una.reservas.persistence.memoria.RecursoDaoEnMemoriaTemporal;
import cr.ac.una.reservas.persistence.memoria.ReservaDaoEnMemoriaTemporal;
import cr.ac.una.reservas.recurso.Recurso;
import org.junit.jupiter.api.RepeatedTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReservaConcurrenciaTest {
    private static final int HILOS = 20;

    @RepeatedTest(5)
    void soloUnHiloGanaElMismoRecursoYHorario() throws Exception {
        CategoriaDaoEnMemoriaTemporal categorias = new CategoriaDaoEnMemoriaTemporal();
        RecursoDaoEnMemoriaTemporal recursos = new RecursoDaoEnMemoriaTemporal();
        ReservaDaoEnMemoriaTemporal reservas = new ReservaDaoEnMemoriaTemporal();
        Categoria sala = categorias.insertar(new Categoria(null, "Sala para 10 personas"));
        recursos.insertar(new Recurso("34343", sala.getId(), "Sala 1 primer piso"));
        ReservaService servicio = new ReservaServiceImpl(reservas, recursos, categorias);

        CountDownLatch salida = new CountDownLatch(1);
        List<Callable<Boolean>> tareas = new ArrayList<>();
        for (int i = 0; i < HILOS; i++) {
            DatosNuevaReserva datos = new DatosNuevaReserva();
            datos.setIdFuncionario("F" + i);
            datos.setActividad("Actividad " + i);
            datos.setFecha(LocalDate.now().plusDays(1));
            datos.setHoraInicio(LocalTime.of(8, 0));
            datos.setHoraFin(LocalTime.of(10, 0));
            datos.setIdsCategoriasRequeridas(List.of(sala.getId()));
            tareas.add(() -> {
                salida.await();
                return servicio.intentarReservar(datos).isExitoso();
            });
        }

        ExecutorService ejecutor = Executors.newFixedThreadPool(HILOS);
        try {
            List<Future<Boolean>> resultados = new ArrayList<>();
            for (Callable<Boolean> tarea : tareas) {
                resultados.add(ejecutor.submit(tarea));
            }
            salida.countDown();
            int ganadores = 0;
            for (Future<Boolean> resultado : resultados) {
                if (resultado.get()) {
                    ganadores++;
                }
            }
            assertEquals(1, ganadores);
            assertEquals(1, reservas.listarTodos().size());
        } finally {
            ejecutor.shutdownNow();
        }
    }
}
