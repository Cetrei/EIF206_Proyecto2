package cr.ac.una.reservas.persistence.falso;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class DaoFalsoNumerico<T> extends DaoFalso<T, Integer> {
    private final Function<T, Integer> extractorId;
    private int contador;

    public DaoFalsoNumerico(Function<T, Integer> extractorId, BiConsumer<T, Integer> asignadorId) {
        super(extractorId, asignadorId);
        this.extractorId = extractorId;
    }

    @Override
    protected Integer generarId() {
        int mayor = listarTodos().stream().mapToInt(entidad -> extractorId.apply(entidad)).max().orElse(0);
        contador = Math.max(contador, mayor) + 1;
        return contador;
    }
}
