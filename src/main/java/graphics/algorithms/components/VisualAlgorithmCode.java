package graphics.algorithms.components;

import core.utilities.Dimensione;
import core.utilities.Grafica;
import graphics.components.AlgComponent;
import lombok.Getter;

import java.awt.*;

public class VisualAlgorithmCode extends AlgComponent {
    @Getter
    private final CodeLine[] codeLines;

    public VisualAlgorithmCode(Dimensione dimensione, CodeLine[] codeLines) {
        super(dimensione);
        this.codeLines = codeLines;
    }

    public void deselect(int... lines){
        for(int line : lines) codeLines[line].deselect();
    }
    public void select(int... lines){
        for(int line : lines) codeLines[line].select();
    }

    @Override
    public void draw(Grafica g) {
        Color color;
        Dimensione dim;
        g.font(0.7);
        g.colore(Color.WHITE);
        double altezzaRiga = dimensione.height() / codeLines.length;
        
        for (int i = 0; i < codeLines.length; i++) {
            CodeLine l = codeLines[i];
            color = l.isSelected() ? new Color(50, 50, 50) : Color.BLACK;

            dim = new Dimensione(
                    dimensione.x(),
                    dimensione.y() + (i * altezzaRiga),
                    dimensione.width(),
                    altezzaRiga
            );

            g.rettangolo(dim, color);
            g.testo(dim, l.getLine());
        }
    }
}
