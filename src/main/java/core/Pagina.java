package core;

import core.utilities.Dimensione;
import core.utilities.Grafica;
import core.utilities.GestorePagine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public abstract class Pagina extends JPanel {
    protected final GestorePagine gestorePagine;

    protected Pagina(GestorePagine gestorePagine) {
        this.gestorePagine = gestorePagine;
        setLayout(null);
    }
    protected Pagina() {
        this(null);
    }

    protected void creaComando(String action, Runnable runnable, String... key){
        for(String k : key){
            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(k), action);
        }
        getActionMap().put(action, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runnable.run();
                paintImmediately(getBounds());
            }
        });
    }

    public void aggiornaImmediatamente(Dimensione d){
        paintImmediately(d.toRectangle(this));
    }
    public void aggiorna(Dimensione d){
        repaint(d.toRectangle(this));
    }

    protected abstract void draw(Grafica grafica);

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Grafica ge = new Grafica(this, (Graphics2D) g);
        draw(ge);
    }

}
