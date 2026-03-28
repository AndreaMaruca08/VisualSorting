package graphics.components;

import graphics.utilities.Dimensione;
import graphics.utilities.GestoreGrafico;
import utilities.ArraysFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Board extends AlgComponent {

    public final List<Column> columns = new ArrayList<>();
    private final long[] arr;

    public Board(Dimensione dimensione, long[] arr) {
        super(dimensione);
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

    @Override
    public void draw(GestoreGrafico gestoreGrafico) {
        double colW = dimensione.width() / arr.length;
        double usableHeight = dimensione.height() - (dimensione.height() / 7);

        for (int i = 0; i < arr.length; i++) {
            Column col = columns.get(i);
            long l = arr[i];

            double x = dimensione.x() + i * colW;
            double barHeight = usableHeight * (l / 100.0);
            double y = dimensione.y() + usableHeight - barHeight;

            col.setVal(l);
            col.setDimensione(new Dimensione(x, y, colW - 1, barHeight));

            gestoreGrafico.draw(col);
        }
    }
}