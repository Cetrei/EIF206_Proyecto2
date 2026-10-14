package cr.ac.una.reservas.reserva;


public interface ReservaObserver {
    void onReservaCreada(Reserva reserva);
    void onReservaCancelada(Reserva reserva);

    default void onReservaModificada(Reserva reserva) {
        onReservaCreada(reserva);
    }
}
