package cr.ac.una.reservas.ui.componentes;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

final class FiltroCampo extends DocumentFilter {
    private boolean soloDigitos;
    private int longitudMaxima = Integer.MAX_VALUE;
    private boolean libre;

    void setSoloDigitos(boolean soloDigitos) {
        this.soloDigitos = soloDigitos;
    }

    void setLongitudMaxima(int longitudMaxima) {
        this.longitudMaxima = longitudMaxima > 0 ? longitudMaxima : Integer.MAX_VALUE;
    }

    void ejecutarLibre(Runnable accion) {
        libre = true;
        try {
            accion.run();
        } finally {
            libre = false;
        }
    }

    @Override
    public void insertString(FilterBypass paso, int posicion, String texto, AttributeSet atributos)
            throws BadLocationException {
        replace(paso, posicion, 0, texto, atributos);
    }

    @Override
    public void replace(FilterBypass paso, int posicion, int largo, String texto, AttributeSet atributos)
            throws BadLocationException {
        if (libre || texto == null || texto.isEmpty()) {
            super.replace(paso, posicion, largo, texto, atributos);
            return;
        }
        String limpio = soloDigitos ? texto.replaceAll("\\D", "") : texto;
        int disponible = longitudMaxima - (paso.getDocument().getLength() - largo);
        if (disponible <= 0 || limpio.isEmpty()) {
            return;
        }
        if (limpio.length() > disponible) {
            limpio = limpio.substring(0, disponible);
        }
        super.replace(paso, posicion, largo, limpio, atributos);
    }
}
