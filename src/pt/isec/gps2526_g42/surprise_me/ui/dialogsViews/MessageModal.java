package pt.isec.gps2526_g42.surprise_me.ui.dialogsViews;

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

public class MessageModal {

    public MessageModal(Stage owner, String title, String message) {
        Stage stage = new Stage(StageStyle.TRANSPARENT); // removes Windows bar
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(owner);

        // Blue header
        HBox header = new HBox();
        header.setStyle("-fx-background-color: " + AppColors.BLUE.getHexValue() + "; -fx-padding: 10 15; -fx-alignment: CENTER_LEFT; -fx-background-radius: 10 10 0 0;");

        // Header title
        Label headerLabel = new Label(title);
        headerLabel.setStyle("-fx-text-fill: " + AppColors.WHITE.getHexValue() + "; -fx-font-size: 16; -fx-font-weight: bold;");

        // "X" button to close
        Label closeLabel = new Label("X");
        closeLabel.setStyle("-fx-text-fill: " + AppColors.WHITE.getHexValue() + "; -fx-font-size: 16; -fx-font-weight: bold; -fx-cursor: hand;");
        closeLabel.setOnMouseEntered(e -> closeLabel.setStyle(
                "-fx-text-fill: " + AppColors.LIGHT_GRAY.getHexValue()
                        + "; -fx-font-size: 16; -fx-font-weight: bold; -fx-cursor: hand;"
        ));
        closeLabel.setOnMouseExited(e -> closeLabel.setStyle(
                "-fx-text-fill: " + AppColors.WHITE.getHexValue()
                        + "; -fx-font-size: 16; -fx-font-weight: bold; -fx-cursor: hand;"
        ));

        // Header layout: title on the left, spacing in the middle and "X" on the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(headerLabel, spacer, closeLabel);

        // White content
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-text-fill: " + AppColors.BLACK.getHexValue() + "; -fx-font-size: 13;");

        StyledButton gotItButton = new StyledButton(AppColors.PURPLE, "Got it", AppColors.WHITE, null, true);

        VBox content = new VBox(15, messageLabel, gotItButton);
        content.setStyle("-fx-background-color: " + AppColors.WHITE.getHexValue() + "; -fx-padding: 20; -fx-background-radius: 0 0 10 10;");
        content.setAlignment(Pos.CENTER);

        // Main container (whole window)
        VBox root = new VBox(header, content);
        root.setStyle("-fx-background-color: " + AppColors.WHITE.getHexValue()
                + "; -fx-border-color: " + AppColors.LIGHT_GRAY.getHexValue()
                + "; -fx-border-width: 1.5; -fx-border-radius: 10; -fx-background-radius: 10;"
        );

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);

        // Window is draggable
        final double[] offset = new double[2];
        root.setOnMousePressed(e -> {
            offset[0] = e.getSceneX();
            offset[1] = e.getSceneY();
        });
        root.setOnMouseDragged(e -> {
            stage.setX(e.getScreenX() - offset[0]);
            stage.setY(e.getScreenY() - offset[1]);
        });

        // Effect of button
        gotItButton.setOnAction(e -> {
            stage.close();
        });

        // Pressing the "X" has the same effect as "got it" button
        closeLabel.setOnMouseClicked(e -> {
            stage.close();
        });

        stage.showAndWait();
    }
}
