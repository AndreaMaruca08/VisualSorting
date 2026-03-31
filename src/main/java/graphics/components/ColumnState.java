package graphics.components;

import java.awt.*;

public enum ColumnState {
    SELECTED(new Color(150,50,50)),
    SPECIAL_SELECT(new Color(100,100,200)),
    NOT_SELECTED(new Color(50,150,50));

    public final Color colore;

    ColumnState(Color colore) {
        this.colore = colore;
    }
}
