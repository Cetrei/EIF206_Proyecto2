package cr.ac.una.reservas.util;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FormatoId {
    public static final String PREFIJO_RESERVA = "RES";
    public static final String PREFIJO_CATEGORIA = "CAT";
    private static final int DIGITOS = 6;

    private FormatoId() {
    }

    public static String reserva(Integer id) {
        return formatear(PREFIJO_RESERVA, id);
    }

    public static String categoria(Integer id) {
        return formatear(PREFIJO_CATEGORIA, id);
    }

    public static Optional<Integer> interpretarReserva(String texto) {
        return interpretar(PREFIJO_RESERVA, texto);
    }

    public static Optional<Integer> interpretarCategoria(String texto) {
        return interpretar(PREFIJO_CATEGORIA, texto);
    }

    private static String formatear(String prefijo, Integer id) {
        if (id == null) {
            return "";
        }
        return String.format("%s-%0" + DIGITOS + "d", prefijo, id);
    }

    private static Optional<Integer> interpretar(String prefijo, String texto) {
        if (texto == null) {
            return Optional.empty();
        }
        Pattern patron = Pattern.compile("^(?:" + prefijo + "-)?(\\d{1,9})$", Pattern.CASE_INSENSITIVE);
        Matcher coincidencia = patron.matcher(texto.trim());
        if (!coincidencia.matches()) {
            return Optional.empty();
        }
        return Optional.of(Integer.parseInt(coincidencia.group(1)));
    }
}
