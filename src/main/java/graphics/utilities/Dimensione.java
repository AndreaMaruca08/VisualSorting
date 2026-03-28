package graphics.utilities;

import java.awt.*;

/**
 * <h4>Rappresentazione di una dimensione grafica da 0 a 100</h4>
 * <p>Questo record rappresenta le dimensioni in una quantità immaginaria che viene trasformata in pixel solo alla fine.
 * Questo per garantire che i rapporti tra componenti rimangano identici indipendentemente dalla risoluzione</p>
 * @author Andrea Maruca
 */
public record Dimensione(
        double x,
        double y,
        double width,
        double height
){

    public Dimensione abbassa(double quanto){
        return new Dimensione(x, y + quanto, width, height);
    }
    /**
     * Da un {@link Rectangle} rapportato a i valori della {@link Dimensione}, quindi in pixel
     * @param component componente che contiene il Rectangle
     * @return Rectangle
     */
    public Rectangle toRectangle(Component component) {
        return new Rectangle(
                GestoreGrafico.getX(x, component),
                GestoreGrafico.getY(y, component),
                GestoreGrafico.getX(width, component),
                GestoreGrafico.getY(height, component)
        );
    }
    public Rectangle toRectangleSmaller(Component component) {
        int dim = 2;
        return new Rectangle(
                GestoreGrafico.getX(x + dim, component),
                GestoreGrafico.getY(y + dim, component),
                GestoreGrafico.getX(width - dim*2, component),
                GestoreGrafico.getY(height - dim*2, component)
        );
    }

    /**
     * Controlla che una coordinata sia all'interno della {@link Dimensione},
     * @param x coordinata x
     * @param y coordinata y
     * @param component componente che contiene la {@link Dimensione}
     * @return true se la coordinata è all'interno della {@link Dimensione}
     */
    public boolean contiene(double x, double y, Component component) {
        return toRectangle(component).contains(x, y);
    }

    public static Dimensione FULLSCREEN = new Dimensione(0, 0, 100, 100);


    /**
     * Crea una nuova Dimensione con stessa width e height, ma con nuove coordinate.
     * @param x coordinata x
     * @param y coordinata y
     * @return nuova Dimensione
     */
    public Dimensione se (double x, double y) {
        return new Dimensione(
                x, y,
                width, height
        );
    }

    public Dimensione ingrandisci(double x, double y) {
        return new Dimensione(
                this.x - x, this.y - y,
                width + x*2, height + y*2
        );
    }

    public Dimensione rimpicciolisci(double x, double y) {
        return new Dimensione(
                this.x + x, this.y + y,
                width - x*2, height - y*2
        );
    }

}
