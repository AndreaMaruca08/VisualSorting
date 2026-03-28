package graphics.components;

import graphics.utilities.Dimensione;
import graphics.utilities.GestoreGrafico;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class AlgComponent {
    protected Dimensione dimensione;
    public abstract void draw(GestoreGrafico gestoreGrafico);
}
