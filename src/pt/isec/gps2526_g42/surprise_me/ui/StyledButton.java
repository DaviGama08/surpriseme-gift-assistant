package pt.isec.gps2526_g42.surprise_me.ui;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import pt.isec.gps2526_g42.surprise_me.ui.res.IconManager;

public class StyledButton extends Button {
    private SVGPath iconShape;
    private Label textLabel;
    private AppColors baseColor;
    private AppColors textColor;
    private boolean simpleButton;
    private static final double TARGET_ICON_SIZE = 18; // preferable height of icon in px
    Group iconGroup = null;

    public StyledButton(AppColors baseColor, String text, AppColors textColor, String iconFileName, boolean simpleButton) {
        super();
        this.baseColor = baseColor;
        this.textColor = textColor;
        this.simpleButton = simpleButton;

        // Text label
        textLabel = new Label(text);
        if(simpleButton) {
            textLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        } else {
            textLabel.setFont(new Font(18));
        }
        textLabel.setStyle("-fx-text-fill:" + textColor.getHexValue() + ";");

        // Optional icon
        if (iconFileName != null && !iconFileName.isEmpty()) {
            iconShape = IconManager.loadSvgIcon(iconFileName);
            iconShape.setFill(textColor.getColor()); // icon with same color as text

            double viewBoxHeight;
            if(simpleButton){
                viewBoxHeight = 700;
            } else {
                viewBoxHeight = 640;
            }
            double scale = TARGET_ICON_SIZE / viewBoxHeight;
            iconShape.setScaleX(scale);
            iconShape.setScaleY(scale);

            iconGroup = new Group(iconShape);
        } else {
            iconShape = null;
        }

        // HBox layout (with or without icon)
        HBox content;
        if (iconShape != null) {
            content = new HBox(8, iconGroup, textLabel);
        } else {
            content = new HBox(8, textLabel);
        }
        if (simpleButton) {
            content.setAlignment(Pos.CENTER);
        } else {
            content.setAlignment(Pos.CENTER_LEFT);
        }

        // Button base setup
        this.setGraphic(content);
        if(simpleButton){
            this.setMinWidth(150);
        } else {
            this.setPrefWidth(180);
        }
        this.setStyle("-fx-background-color:" + baseColor.getHexValue() + ";");

        // Hover effects
        this.setOnMouseEntered(e -> onHover());
        this.setOnMouseExited(e -> onExit());
    }

    private void onHover() {
        if (simpleButton) {
            DropShadow shadow = new DropShadow();
            shadow.setRadius(10);
            shadow.setOffsetX(0);
            shadow.setOffsetY(2);
            shadow.setColor(AppColors.LIGHT_GRAY.getColor());
            this.setEffect(shadow);
        } else {
            setStyle("-fx-background-color:" + AppColors.BLUE.getHexValue() + ";");
            textLabel.setStyle("-fx-text-fill:" + AppColors.CRISTAL_BLUE.getHexValue() + ";");
            if (iconShape != null) {
                iconShape.setFill(AppColors.CRISTAL_BLUE.getColor());
            }
        }
    }

    private void onExit() {
        if (simpleButton) {
            this.setEffect(null);
        } else {
            setStyle("-fx-background-color: transparent;");
            textLabel.setStyle("-fx-text-fill:" + AppColors.LIGHT_GRAY.getHexValue() + ";");
            if (iconShape != null) {
                iconShape.setFill(AppColors.LIGHT_GRAY.getColor());
            }
        }
    }

    // Helper to easily assign a click action
    public void setClickAction(Runnable action) {
        this.setOnAction(e -> action.run());
    }
}
