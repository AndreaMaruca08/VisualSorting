
package core.utilities;

import java.awt.*;

/**
 * @author Andrea Maruca
 */
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