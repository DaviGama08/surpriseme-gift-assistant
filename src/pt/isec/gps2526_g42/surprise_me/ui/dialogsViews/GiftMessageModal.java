package pt.isec.gps2526_g42.surprise_me.ui.dialogsViews;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import pt.isec.gps2526_g42.surprise_me.ui.AppColors;
import pt.isec.gps2526_g42.surprise_me.ui.StyledButton;

public class GiftMessageModal {
    private GiftMessageModal() {
    }

    public static boolean askForGiftMessage(Stage owner) {
        Stage stage = new Stage(StageStyle.TRANSPARENT);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(owner);

        // Blue header
        HBox header = new HBox();
        header.setStyle("-fx-background-color: " + AppColors.BLUE.getHexValue() + "; -fx-padding: 10 15; -fx-alignment: CENTER_LEFT; -fx-background-radius: 10 10 0 0;");

        // Header title
        Label lbTitle = new Label("Gift Message");
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

        // Header layout
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(lbTitle, spacer, lbClose);

        // White content
        VBox content = new VBox(20);
        content.setStyle("-fx-background-color: " + AppColors.WHITE.getHexValue() + "; -fx-padding: 20; -fx-background-radius: 0 0 10 10;");

        Label message = new Label("Would you like to create a personalized message to accompany your gift?");
        message.setStyle("-fx-text-fill: " + AppColors.BLACK.getHexValue() + "; -fx-font-size: 14; -fx-wrap-text: true;");
        message.setWrapText(true);
        message.setMaxWidth(350);

        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER);

        StyledButton yesButton = new StyledButton(AppColors.GREEN, "Yes", AppColors.WHITE, "check-solid-full.svg", true);
        StyledButton noButton = new StyledButton(AppColors.RED, "No", AppColors.WHITE, "circle-xmark-regular-full.svg", true);
        buttons.getChildren().addAll(yesButton, noButton);

        content.getChildren().addAll(message, buttons);

        // Main container
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

        // Button effects
        final boolean[] result = new boolean[1];
        yesButton.setOnAction(e -> {
            result[0] = true;
            stage.close();
        });
        noButton.setOnAction(e -> {
            result[0] = false;
            stage.close();
        });

        // Pressing the "X" has the same effect as No button
        lbClose.setOnMouseClicked(e -> {
            result[0] = false;
            stage.close();
        });

        stage.showAndWait();
        return result[0];
    }

    public static boolean showGiftMessage(Stage owner, String generatedMessage) {
        Stage stage = new Stage(StageStyle.TRANSPARENT);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(owner);

        // Blue header
        HBox header = new HBox();
        header.setStyle("-fx-background-color: " + AppColors.BLUE.getHexValue() + "; -fx-padding: 10 15; -fx-alignment: CENTER_LEFT; -fx-background-radius: 10 10 0 0;");

        // Header title
        Label lbTitle = new Label("Generated Gift Message");
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

        // Header layout
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(lbTitle, spacer, lbClose);

        // White content
        VBox content = new VBox(20);
        content.setStyle("-fx-background-color: " + AppColors.WHITE.getHexValue() + "; -fx-padding: 20; -fx-background-radius: 0 0 10 10;");
        content.setPrefHeight(Region.USE_COMPUTED_SIZE);
        content.setMinHeight(Region.USE_PREF_SIZE);

        Label messageLabel = new Label("Here is your personalized gift message:");
        messageLabel.setStyle("-fx-text-fill: " + AppColors.BLACK.getHexValue() + "; -fx-font-size: 13; -fx-font-weight: bold;");

        // Message content in a styled container
        VBox messageContainer = new VBox(5);
        messageContainer.setStyle("-fx-background-color: " + AppColors.CRISTAL_BLUE.getHexValue() +
                "; -fx-padding: 15; -fx-background-radius: 8; -fx-border-radius: 8;");
        messageContainer.setPrefHeight(Region.USE_COMPUTED_SIZE);
        messageContainer.setMinHeight(Region.USE_PREF_SIZE);

        Label messageContent = new Label(generatedMessage != null ? generatedMessage : "Could not generate message");
        messageContent.setStyle("-fx-text-fill: " + AppColors.DARK_GREY.getHexValue() +
                "; -fx-font-size: 14; -fx-font-style: italic; -fx-wrap-text: true;");
        messageContent.setWrapText(true);
        messageContent.setMaxWidth(350);
        messageContent.setPrefHeight(Region.USE_COMPUTED_SIZE);
        messageContent.setMinHeight(Region.USE_PREF_SIZE);

        // Add message content to container first
        messageContainer.getChildren().add(messageContent);

        // Copy button with proper copy icon (using Unicode squares)
        Button btnCopyPaste = new Button("⧉");  // Unicode "two squares" copy icon
        btnCopyPaste.setStyle("-fx-font-size: 14px; -fx-padding: 6 10; -fx-background-color: " +
                AppColors.BLUE.getHexValue() + "; -fx-text-fill: " + AppColors.WHITE.getHexValue()
                + "; -fx-background-radius: 4; -fx-cursor: hand; " + "-fx-border-radius: 4; -fx-font-family: monospace;");

        Tooltip ttCopyPaste = new Tooltip("Copy message to clipboard");
        ttCopyPaste.setStyle("-fx-font-size: 12; -fx-background-color: " + AppColors.BLUE.getHexValue()
                + "; -fx-text-fill: " + AppColors.WHITE.getHexValue() + ";");
        btnCopyPaste.setTooltip(ttCopyPaste);

        // Hover effects
        btnCopyPaste.setOnMouseEntered(e -> btnCopyPaste.setStyle("-fx-font-size: 14px; -fx-padding: 6 10; " +
                "-fx-background-color: " + AppColors.LIGHT_GRAY.getHexValue() + "; -fx-text-fill:" + AppColors.WHITE.getHexValue() +
                "-fx-background-radius: 4; -fx-cursor: hand; -fx-border-radius: 4; -fx-font-family: monospace;"));
        btnCopyPaste.setOnMouseExited(e -> btnCopyPaste.setStyle("-fx-font-size: 14px; -fx-padding: 6 10; " +
                "-fx-background-color: " + AppColors.BLUE.getHexValue() + "; -fx-text-fill:" + AppColors.WHITE.getHexValue() +
                "-fx-background-radius: 4; -fx-cursor: hand; -fx-border-radius: 4; -fx-font-family: monospace;"));

        // Timeline for copy feedback animation
        Timeline copyFeedback = new Timeline(
                new KeyFrame(Duration.millis(0), ev -> {
                    // Message container feedback - green border and light background
                    messageContainer.setStyle("-fx-background-color: " + AppColors.CRISTAL_BLUE.getHexValue() +
                            "; -fx-padding: 15; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: " +
                            AppColors.GREEN.getHexValue() + "; -fx-border-width: 2;");
                    // Button feedback - checkmark and green
                    btnCopyPaste.setText("✓");
                    btnCopyPaste.setStyle("-fx-font-size: 14px; -fx-padding: 6 10; -fx-background-color: " +
                            AppColors.GREEN.getHexValue() + "; -fx-text-fill: white; -fx-background-radius: 4; " +
                            "-fx-border-radius: 4; -fx-font-family: monospace;");
                    Tooltip copiedTooltip = new Tooltip("Copied!");
                    copiedTooltip.setStyle("-fx-font-size: 12;");
                    btnCopyPaste.setTooltip(copiedTooltip);
                }),
                new KeyFrame(Duration.millis(1200), ev -> {
                    // Reset message container style
                    messageContainer.setStyle("-fx-background-color: " + AppColors.CRISTAL_BLUE.getHexValue() +
                            "; -fx-padding: 15; -fx-background-radius: 8; -fx-border-radius: 8;");
                    // Reset button
                    btnCopyPaste.setText("⧉");
                    btnCopyPaste.setStyle("-fx-font-size: 14px; -fx-padding: 6 10; -fx-background-color: " +
                            AppColors.BLUE.getHexValue() + "; -fx-text-fill: white; -fx-background-radius: 4; " +
                            "-fx-cursor: hand; -fx-border-radius: 4; -fx-font-family: monospace;");
                    btnCopyPaste.setTooltip(ttCopyPaste);
                })
        );

        // Copy functionality
        btnCopyPaste.setOnAction(e -> {
            if (generatedMessage != null && !generatedMessage.trim().isEmpty()) {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent clipboardContent = new ClipboardContent();
                clipboardContent.putString(generatedMessage);
                clipboard.setContent(clipboardContent);
                copyFeedback.play();
            }
        });

        HBox lbBox = new HBox(messageLabel);
        lbBox.setAlignment(Pos.CENTER_LEFT);
        HBox copyButtonBox = new HBox(15, lbBox, btnCopyPaste);
        HBox.setHgrow(lbBox, Priority.ALWAYS);
        copyButtonBox.setAlignment(Pos.CENTER_LEFT);

        StyledButton acceptButton = new StyledButton(AppColors.GREEN, "Accept", AppColors.WHITE, "check-solid-full.svg", true);
        StyledButton rejectButton = new StyledButton(AppColors.RED, "Reject", AppColors.WHITE, "circle-xmark-regular-full.svg", true);

        HBox buttons = new HBox(15, acceptButton, rejectButton);
        buttons.setAlignment(Pos.CENTER);

        content.getChildren().addAll(copyButtonBox, messageContainer, buttons);

        // Main container
        VBox root = new VBox(header, content);
        root.setStyle("-fx-background-color: " + AppColors.WHITE.getHexValue()
                + "; -fx-border-color: " + AppColors.LIGHT_GRAY.getHexValue()
                + "; -fx-border-width: 1.5; -fx-border-radius: 10; -fx-background-radius: 10;"
        );

        Scene scene = new Scene(root, 480, 380);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.setMinWidth(480);
        stage.setMinHeight(380);

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

        // Button effects
        final boolean[] result = new boolean[1];
        acceptButton.setOnAction(e -> {
            result[0] = true;
            stage.close();
        });
        rejectButton.setOnAction(e -> {
            result[0] = false;
            stage.close();
        });

        // Pressing the "X" has the same effect as Reject button
        lbClose.setOnMouseClicked(e -> {
            result[0] = false;
            stage.close();
        });

        stage.showAndWait();
        return result[0];
    }
}
