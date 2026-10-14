package cr.ac.una.reservas.reserva;

import cr.ac.una.reservas.util.FormatoId;
import cr.ac.una.reservas.autenticacion.SesionControl;
import cr.ac.una.reservas.autenticacion.Usuario;
import cr.ac.una.reservas.categoria.Categoria;
import cr.ac.una.reservas.excepcion.ReglaDeNegocioException;
import cr.ac.una.reservas.ui.componentes.Popup;

import java.awt.Frame;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

final class SolicitudReservaControl {
    private final ReservaModel modelo;
    private final TabReservas vista;
    private final Frame ventanaPropietaria;
    private final ReservaService reservaService;
    private final Runnable alActualizarListado;

    SolicitudReservaControl(
            ReservaModel modelo,
            TabReservas vista,
            Frame ventanaPropietaria,
            ReservaService reservaService,
            Runnable alActualizarListado
    ) {
        this.modelo = modelo;
        this.vista = vista;
        this.ventanaPropietaria = ventanaPropietaria;
        this.reservaService = reservaService;
        this.alActualizarListado = alActualizarListado;
    }

    void solicitar() {
        Usuario usuarioActual = SesionControl.obtenerInstancia().usuarioActual();
        if (usuarioActual == null) return;

        boolean modificando = vista.estaEnModoEdicion();
        Reserva reservaEnEdicion = vista.obtenerReservaEnEdicion();
        String actividad = vista.obtenerActividad();
        LocalDate fecha = vista.obtenerFecha();
        List<Categoria> categoriasSeleccionadas = vista.obtenerCategoriasSeleccionadas();

        if (actividad == null || actividad.isBlank()) {
            mostrarError("No se pudo reservar", "Debe ingresar el nombre de la actividad.");
            return;
        }

        if (fecha == null) {
            mostrarError("No se pudo reservar", "Debe seleccionar una fecha para la reserva.");
            return;
        }

        if (categoriasSeleccionadas == null || categoriasSeleccionadas.isEmpty()) {
            mostrarError("No se pudo reservar", "Debe seleccionar al menos una categoría de recurso.");
            return;
        }

        DatosNuevaReserva datos = new DatosNuevaReserva(
                modificando ? reservaEnEdicion.getId() : null,
                usuarioActual.getId(),
                actividad,
                fecha,
                vista.obtenerHoraInicio(),
                vista.obtenerHoraFin(),
                categoriasSeleccionadas.stream().map(Categoria::getId).collect(Collectors.toList())
        );

        try {
            ResultadoReserva resultado = modificando
                    ? reservaService.intentarModificar(datos)
                    : reservaService.intentarReservar(datos);
            modelo.setResultadoIntento(resultado);

            if (resultado.isExitoso()) {
                modelo.setReservaSeleccionada(null);
                alActualizarListado.run();
                Popup.mostrarAviso(
                        ventanaPropietaria,
                        Popup.Tipo.CONFIRMACION,
                        modificando ? "Modificar reserva" : "Solicitar reserva",
                        modificando
                                ? "La reserva " + FormatoId.reserva(resultado.getReserva().getId()) + " se actualizó correctamente."
                                : "La reserva se registró correctamente con el ID "
                                        + FormatoId.reserva(resultado.getReserva().getId()) + "."
                );
            } else {
                String categoriasFallidas = resultado.getCategoriasNoDisponibles().stream()
                        .map(Categoria::getDescripcion)
                        .collect(Collectors.joining(", "));

                mostrarError(
                        "No hay disponibilidad",
                        "No hay recursos disponibles para: " + categoriasFallidas + ". "
                                + "Puede modificar la reserva e intentar de nuevo."
                );
            }
        } catch (ReglaDeNegocioException excepcion) {
            mostrarError(modificando ? "No se pudo modificar" : "No se pudo reservar", excepcion.getMessage());
        }
    }

    private void mostrarError(String titulo, String mensaje) {
        Popup.mostrarAviso(ventanaPropietaria, Popup.Tipo.ERROR, titulo, mensaje);
    }
}
