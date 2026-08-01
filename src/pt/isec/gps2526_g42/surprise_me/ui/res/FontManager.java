package pt.isec.gps2526_g42.surprise_me.ui.res;

import javafx.scene.text.Font;
import java.io.InputStream;

public class FontManager {
    private FontManager() { }

    public static Font loadFont(String filename, int size) {
        try(InputStream inputStreamFont =
                    FontManager.class.getResourceAsStream("fonts/" + filename)) {
            return Font.loadFont(inputStreamFont, size);
        } catch (Exception e) {
            return null;
        }
    }
}
