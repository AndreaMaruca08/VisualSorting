package graphics.components;

import graphics.utilities.Dimensione;
import graphics.utilities.GestoreGrafico;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Abstract base class for algorithm components in the graphics system.
 * Provides common functionality and properties for graphics components.
 *
 * @author Andrea Maruca
 */
@Getter
@Setter
@AllArgsConstructor
public abstract class AlgComponent {
    protected Dimensione dimensione;
    public abstract void draw(GestoreGrafico gestoreGrafico);
}
