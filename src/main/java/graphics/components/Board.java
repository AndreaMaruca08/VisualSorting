package graphics.components;

import core.BaseComponent;
import core.utilities.Dimensione;
import core.utilities.Grafica;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a board component in the graphics system.
 * Extends BaseComponent and provides functionality for rendering and managing columns.
 *
 * @author Andrea Maruca
 */
public class Board extends BaseComponent {

    public final List<Column> columns = new ArrayList<>();

    private long[] arr;

    public Board(Dimensione dimensione, long[] arr) {
        super(dimensione);
        this.arr = arr;
        initColumns();
    }

    public void setArr(long[] arr) {
        this.arr = arr;
        initColumns();
    }

    private void initColumns() {
        columns.clear();

        double colW = dimensione.width() / arr.length;
        double usableHeight = dimensione.height() - (dimensione.height() / 7);

        for (int i = 0; i < arr.length; i++) {
            long l = arr[i];

            double x = dimensione.x() + i * colW;
            double barHeight = usableHeight * (l / 100.0);
            double y = dimensione.y() + usableHeight - barHeight;

            Dimensione colonna = new Dimensione(x, y, colW - 1, barHeight);
            columns.add(new Column(l, arr.length, colonna));
        }
    }

    public void deselect() {
        for(Column col : columns) col.deselect();
    }
    public void select() {
        for(Column col : columns) col.select();
    }

    @Override
    public void draw(Grafica grafica) {
        double colW = dimensione.width() / arr.length;
        double usableHeight = dimensione.height() - (dimensione.height() / 7);
        double gap = 0.2;

        for (int i = 0; i < arr.length; i++) {
            Column col = columns.get(i);
            long l = arr[i];

            double x = dimensione.x() + i * colW;
            double barHeight = usableHeight * (l / 100.0);
            double y = dimensione.y() + usableHeight - barHeight;

            col.setVal(l);
            col.setDimensione(new Dimensione(x, y, colW - gap, barHeight));

            grafica.draw(col);
        }
    }
}