package graphics.components;

import core.utilities.Dimensione;
import core.utilities.Grafica;
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
    private ColumnState state;

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
        state = ColumnState.NOT_SELECTED;
        this.arrSize = arrSize;
    }

    public void setVal(long val) {
        if(val < 0) throw new IllegalArgumentException("COLUMN val \"" + val + " \" < 0");
        if(val > 1_000_000_000) throw new IllegalArgumentException("COLUMN val \"" + val + " \" > 1_000_000_000");
        this.val = val;
    }

    public void select(){
        state = ColumnState.SELECTED;
    }
    public void specialSelect(){
        state = ColumnState.SPECIAL_SELECT;
    }
    public void deselect(){
        state = ColumnState.NOT_SELECTED;
    }

    @Override
    public void draw(Grafica g) {
        Color color = state.colore;
        g.spessoreDisegno(5);
        g.bordoRettangolo(dimensione, new Color(0,0,0,80));
        g.rettangolo(dimensione, color);

        if(arrSize <= 50){
            g.font(10.0 / (arrSize/2));
            g.testo(testo, String.valueOf(val), color);
        }
    }
}
