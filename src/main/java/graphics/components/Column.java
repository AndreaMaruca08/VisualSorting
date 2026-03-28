package graphics.components;

import graphics.utilities.Dimensione;
import graphics.utilities.GestoreGrafico;
import lombok.Getter;

import java.awt.*;

/**
 * Represents a column component in the graphics system.
 * Extends AlgComponent and provides functionality for rendering and managing column values.
 *
 * @author Andrea Maruca
 */
@Getter
public class Column extends AlgComponent{
    private long val;
    private final double arrSize;
    private boolean isSelected;

    public final Color COLOR2 = new Color(50,150,50);
    public Color COLOR1 = new Color(150,50,50);

    private final Dimensione testo;

    public Column(long val, double arrSize, Dimensione dimensione) {
        super(dimensione);
        setVal(val);
        testo = new Dimensione(
                dimensione.x(),
                dimensione.y() + dimensione.height() + (arrSize > 25 ? arrSize / 13 : arrSize / 7),
                dimensione.width(),
                1
        );
        isSelected = false;
        this.arrSize = arrSize;
    }

    public void setVal(long val) {
        if(val < 0) throw new IllegalArgumentException("COLUMN val \"" + val + " \" < 0");
        if(val > 1_000_000_000) throw new IllegalArgumentException("COLUMN val \"" + val + " \" > 1_000_000_000");
        this.val = val;
    }

    public void select(){
        isSelected = true;
    }
    public void deselect(){
        isSelected = false;
    }

    @Override
    public void draw(GestoreGrafico g) {
        Color color = isSelected ? COLOR1 : COLOR2;
        g.spessoreDisegno(5);
        g.bordoRettangolo(dimensione, new Color(0,0,0,80));
        g.rettangolo(dimensione, color);

        g.font(10.0 / (arrSize/2));


        g.testo(testo, String.valueOf(val), color);
    }
}
