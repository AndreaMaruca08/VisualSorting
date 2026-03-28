
package graphics.utilities;

import lombok.extern.java.Log;

import java.awt.*;
import java.io.InputStream;
import java.util.Objects;

/**
 * @author Andrea Maruca
 */
@Log
public final class FontLoader {

    private FontLoader() {}

    private static Font customFont;

    public static Font getCustomFont(float size) {
        if (customFont == null) {
            customFont = new Font("Monospaced", Font.PLAIN, 21);
        }
        return customFont.deriveFont(size);
    }
}