package graphics.algorithms.components;

import lombok.Getter;

@Getter
public class CodeLine {
    private final String line;
    private boolean selected;

    public void select() {
        selected = true;
    }
    public void deselect() {
        selected = false;
    }

    public CodeLine(String line) {
        this.line = line;
        selected = false;
    }
}
