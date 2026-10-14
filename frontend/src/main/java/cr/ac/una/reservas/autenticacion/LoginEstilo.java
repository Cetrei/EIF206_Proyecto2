package cr.ac.una.reservas.autenticacion;

import cr.ac.una.reservas.ui.iconos.IconoAplicador;
import cr.ac.una.reservas.ui.iconos.IconoSemantico;
import cr.ac.una.reservas.ui.tema.Tema;

import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import java.awt.Font;

final class LoginEstilo {
    private LoginEstilo() {
    }

    static void aplicar(
            Tema tema,
            JLabel iconTitulo,
            JLabel lblTitulo,
            JLabel lblSubTitulo,
            JLabel lblPiePagina,
            JLabel lblIntegrantes
    ) {
        IconoAplicador.aplicar(iconTitulo, IconoSemantico.LOGO_APP.icono(), 32f, tema.colorPrimario());

        lblTitulo.setForeground(tema.colorTexto());
        lblTitulo.setFont(tema.fuenteTitulo());

        lblSubTitulo.setForeground(tema.colorTextoSecundario());
        lblSubTitulo.setFont(tema.fuenteTexto());

        lblPiePagina.setForeground(tema.colorTextoSecundario());
        lblPiePagina.setFont(tema.fuenteTexto().deriveFont(Font.PLAIN, 11f));

        lblIntegrantes.setForeground(tema.colorTextoSecundario());
        lblIntegrantes.setFont(tema.fuenteTexto().deriveFont(Font.PLAIN, 11f));
        lblIntegrantes.setBorder(new EmptyBorder(0, 16, 12, 16));
    }
}
