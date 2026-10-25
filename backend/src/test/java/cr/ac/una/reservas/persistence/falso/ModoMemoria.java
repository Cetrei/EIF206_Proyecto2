package cr.ac.una.reservas.persistence.falso;

public final class ModoMemoria {
    private static final String PROPIEDAD_MODO = "reservas.dao";
    private static final String MODO_MEMORIA = "memoria";

    private ModoMemoria() {
    }

    public static void activar() {
        System.setProperty(PROPIEDAD_MODO, MODO_MEMORIA);
    }
}
