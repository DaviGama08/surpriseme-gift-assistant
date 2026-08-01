package pt.isec.gps2526_g42.surprise_me.ui;

import javafx.scene.paint.Color;

public enum AppColors {
    BLACK("#000000"),
    WHITE("#FFFFFF"),
    DARK_GREY("#292929"),
    LIGHT_GRAY("#7D7D7D"),
    CRISTAL_BLUE("#E7F9F5"),
    VIVID_BLUE("#00B0F0"),
    BLUE("#9DBCD9"),
    PURPLE("#B3A1C9"),
    RED("#F87E8D"),
    DARK_ROSE("#E85A6A"),
    GREEN("#84E291"),
    ORANGE("#FFAA66");

    private final String hexValue;

    // Constructor
    AppColors(String hexValue) {
        this.hexValue = hexValue;
    }

    // Getter for hex value
    public String getHexValue() {
        return hexValue;
    }

    // Getter for color
    public Color getColor() {
        return Color.web(hexValue);
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase() + " (" + getHexValue() + ")";
    }
}

