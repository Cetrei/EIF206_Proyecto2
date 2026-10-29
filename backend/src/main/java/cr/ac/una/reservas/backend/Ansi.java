package cr.ac.una.reservas.backend;

final class Ansi {
    private static final boolean ACTIVO = System.getenv("NO_COLOR") == null && terminalCompatible();
    static final String BORRAR_LINEA = ACTIVO ? "\r\u001B[2K" : "\r";

    private static final String RESET = "\u001B[0m";
    private static final String ROJO = "\u001B[31m";
    private static final String VERDE = "\u001B[32m";
    private static final String AMARILLO = "\u001B[33m";
    private static final String CIAN = "\u001B[36m";
    private static final String TENUE = "\u001B[2m";

    private Ansi() {
    }

    private static boolean terminalCompatible() {
        boolean windows = System.getProperty("os.name", "").toLowerCase().contains("win");
        if (!windows) {
            return true;
        }
        return System.getenv("WT_SESSION") != null
                || System.getenv("TERM") != null
                || System.getenv("ANSICON") != null
                || System.console() == null;
    }

    static String error(String texto) {
        return envolver(ROJO, texto);
    }

    static String exito(String texto) {
        return envolver(VERDE, texto);
    }

    static String advertencia(String texto) {
        return envolver(AMARILLO, texto);
    }

    static String info(String texto) {
        return envolver(CIAN, texto);
    }

    static String tenue(String texto) {
        return envolver(TENUE, texto);
    }

    private static String envolver(String codigo, String texto) {
        return ACTIVO ? codigo + texto + RESET : texto;
    }
}
