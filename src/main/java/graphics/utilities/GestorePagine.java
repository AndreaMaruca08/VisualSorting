package graphics.utilities;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * <h4>Gestore di {@link JPanel} come pagine</h4>
 * @author Andrea Maruca
 */
public class GestorePagine extends CardLayout {
    public static JPanel container;
    public GestorePagine() {
        super();
        container = new JPanel(this);
    }

    /**
     * Cambia la pagina attiva
     * @param nomePagina nome della pagina
     */
    public void cambiaPagina(String nomePagina) {
        show(container, nomePagina);
    }

    /**
     * Aggiunge una pagina alla lista, se già presente non lo aggiunge di nuovo
     * @param nomePagina nome della pagina
     * @param pagina pagina da aggiungere
     */
    public void addPagina(String nomePagina, JComponent pagina) {
        if(Arrays.stream(container.getComponents()).toList().contains(pagina))
            return;
        container.add(pagina, nomePagina);
    }

}
