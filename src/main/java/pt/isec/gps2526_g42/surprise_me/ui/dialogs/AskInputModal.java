package pt.isec.gps2526_g42.surprise_me.ui.dialogs;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

public class AskInputModal {
    private String input;

    public boolean show(Stage owner, String title, String question) {
        Stage stage = new Stage(StageStyle.TRANSPARENT); // removes Windows bar
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(owner);

        // Blue header
        HBox header = new HBox();
        header.setStyle("-fx-background-color: " + AppColors.BLUE.getHexValue() + "; -fx-padding: 10 15; -fx-alignment: CENTER_LEFT; -fx-background-radius: 10 10 0 0;");

        // Header title
        Label lbTitle = new Label(title);
        lbTitle.setStyle("-fx-text-fill: " + AppColors.WHITE.getHexValue() + "; -fx-font-size: 16; -fx-font-weight: bold;");

        // "X" button to close
        Label lbClose = new Label("X");
        lbClose.setStyle("-fx-text-fill: " + AppColors.WHITE.getHexValue() + "; -fx-font-size: 16; -fx-font-weight: bold; -fx-cursor: hand;");
        lbClose.setOnMouseEntered(e -> lbClose.setStyle(
                "-fx-text-fill: " + AppColors.LIGHT_GRAY.getHexValue()
                        + "; -fx-font-size: 16; -fx-font-weight: bold; -fx-cursor: hand;"
        ));
        lbClose.setOnMouseExited(e -> lbClose.setStyle(
                "-fx-text-fill: " + AppColors.WHITE.getHexValue()
                        + "; -fx-font-size: 16; -fx-font-weight: bold; -fx-cursor: hand;"
        ));

        // Header layout: title on the left, spacing in the middle and "X" on the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(lbTitle, spacer, lbClose);

        // White content
        VBox content = new VBox(15);
        content.setStyle("-fx-background-color: " + AppColors.WHITE.getHexValue() + "; -fx-padding: 20; -fx-background-radius: 0 0 10 10;");
        Label message = new Label(question);
        message.setStyle("-fx-text-fill: " + AppColors.BLACK.getHexValue() + "; -fx-font-size: 13;");
        TextField tfInput = new TextField();
        HBox inputBox = new HBox(message, tfInput);
        inputBox.setSpacing(10);
        inputBox.setAlignment(Pos.CENTER);

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        StyledButton okButton = new StyledButton(AppColors.PURPLE, "OK", AppColors.WHITE, null, true);
        StyledButton cancelButton = new StyledButton(AppColors.PURPLE, "Cancel", AppColors.WHITE, null, true);
        buttons.getChildren().addAll(okButton, cancelButton);

        // Initially disabled
        okButton.setDisable(true);

        // Enable/disable OK based on text input
        tfInput.textProperty().addListener((obs, oldText, newText) -> okButton.setDisable(newText.trim().isEmpty()));

        content.getChildren().addAll(inputBox, buttons);

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

        // Effects of buttons
        final boolean[] result = new boolean[1];
        okButton.setOnAction(e -> {
            input = tfInput.getText().trim();
            result[0] = true;
            stage.close();
        });
        cancelButton.setOnAction(e -> {
            result[0] = false;
            stage.close();
        });

        // Pressing the "X" has the same effect as Cancel button
        lbClose.setOnMouseClicked(e -> {
            result[0] = false;
            stage.close();
        });

        stage.showAndWait();
        return result[0];
    }

    public String getInput() {
        return input;
    }
}
