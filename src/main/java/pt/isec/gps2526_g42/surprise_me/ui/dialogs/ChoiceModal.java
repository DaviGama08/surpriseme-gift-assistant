package pt.isec.gps2526_g42.surprise_me.ui.dialogs;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pt.isec.gps2526_g42.surprise_me.ui.AppColors;
import pt.isec.gps2526_g42.surprise_me.ui.StyledButton;

/**
 * Simple three-option modal that follows the app style.
 * Returns 1 for first option, 2 for second option, 0 for cancel/close.
 */
public class ChoiceModal {
    private ChoiceModal() {}

    public static int show(Stage owner, String title, String message, String opt1, String opt2, String cancelLabel) {
        Stage stage = new Stage(StageStyle.TRANSPARENT);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(owner);

        HBox header = new HBox();
        header.setStyle("-fx-background-color: " + AppColors.BLUE.getHexValue() + "; -fx-padding: 10 15; -fx-alignment: CENTER_LEFT; -fx-background-radius: 10 10 0 0;");

        Label headerLabel = new Label(title);
        headerLabel.setStyle("-fx-text-fill: " + AppColors.WHITE.getHexValue() + "; -fx-font-size: 16; -fx-font-weight: bold;");

        Label closeLabel = new Label("X");
        closeLabel.setStyle("-fx-text-fill: " + AppColors.WHITE.getHexValue() + "; -fx-font-size: 16; -fx-font-weight: bold; -fx-cursor: hand;");
        closeLabel.setOnMouseEntered(e -> closeLabel.setStyle(
                "-fx-text-fill: " + AppColors.LIGHT_GRAY.getHexValue() + "; -fx-font-size: 16; -fx-font-weight: bold; -fx-cursor: hand;"
        ));
        closeLabel.setOnMouseExited(e -> closeLabel.setStyle(
                "-fx-text-fill: " + AppColors.WHITE.getHexValue() + "; -fx-font-size: 16; -fx-font-weight: bold; -fx-cursor: hand;"
        ));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(headerLabel, spacer, closeLabel);

        VBox content = new VBox(15);
        content.setStyle("-fx-background-color: " + AppColors.WHITE.getHexValue() + "; -fx-padding: 20; -fx-background-radius: 0 0 10 10;");
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-text-fill: " + AppColors.BLACK.getHexValue() + "; -fx-font-size: 13;");

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);

        StyledButton b1 = new StyledButton(AppColors.PURPLE, opt1, AppColors.WHITE, null, true);
        StyledButton b2 = new StyledButton(AppColors.PURPLE, opt2, AppColors.WHITE, null, true);
        StyledButton bCancel = new StyledButton(AppColors.PURPLE, cancelLabel, AppColors.WHITE, null, true);
        buttons.getChildren().addAll(b1, b2, bCancel);

        content.getChildren().addAll(messageLabel, buttons);

        VBox root = new VBox(header, content);
        root.setStyle("-fx-background-color: " + AppColors.WHITE.getHexValue()
                + "; -fx-border-color: " + AppColors.LIGHT_GRAY.getHexValue()
                + "; -fx-border-width: 1.5; -fx-border-radius: 10; -fx-background-radius: 10;");

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);

        final int[] result = {0};
        b1.setOnAction(e -> { result[0] = 1; stage.close(); });
        b2.setOnAction(e -> { result[0] = 2; stage.close(); });
        bCancel.setOnAction(e -> { result[0] = 0; stage.close(); });

        closeLabel.setOnMouseClicked(e -> { result[0] = 0; stage.close(); });

        // draggable
        final double[] offset = new double[2];
        root.setOnMousePressed(e -> { offset[0] = e.getSceneX(); offset[1] = e.getSceneY(); });
        root.setOnMouseDragged(e -> { stage.setX(e.getScreenX() - offset[0]); stage.setY(e.getScreenY() - offset[1]); });

        stage.showAndWait();
        return result[0];
    }
}

