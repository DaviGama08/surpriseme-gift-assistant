package pt.isec.gps2526_g42.surprise_me.ui.gifts;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pt.isec.gps2526_g42.surprise_me.model.Feedback;
import pt.isec.gps2526_g42.surprise_me.model.Status;
import pt.isec.gps2526_g42.surprise_me.application.SurpriseMeManager;
import pt.isec.gps2526_g42.surprise_me.model.data.Gift;
import pt.isec.gps2526_g42.surprise_me.ui.AppColors;
import pt.isec.gps2526_g42.surprise_me.ui.StyledButton;
import pt.isec.gps2526_g42.surprise_me.ui.dialogs.MessageModal;
import pt.isec.gps2526_g42.surprise_me.ui.dialogs.OkCancelModal;

import java.time.format.DateTimeFormatter;

public class GiftDetailsModal extends Stage {
    private SurpriseMeManager manager;
    private Gift gift;

    // UI components
    private TextField tfEnjoyer;
    private Label lbEnjoyer;
    private TextField tfOccasion;
    private Label lbOccasion;
    private TextField tfType;
    private Label lbType;
    private ComboBox<String> cbStatus;
    private Label lbStatus;
    private ComboBox<String> cbFeedback;
    private Label lbFeedback;
    private TextField tfDate;
    private Label lbDate;
    private TextArea taGiftcard;
    private Label lbGiftcard;
    private StyledButton btnSave;
    private StyledButton btnClose;
    private Label lbClose;
    private Button btnCopyPaste;

    public GiftDetailsModal(SurpriseMeManager manager, Gift gift) {
        this.manager = manager;
        this.gift = gift;
        this.initStyle(StageStyle.TRANSPARENT);
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        // Blue header
        HBox header = new HBox();
        header.setStyle("-fx-background-color: " + AppColors.BLUE.getHexValue() + "; -fx-padding: 10 15; -fx-alignment: CENTER_LEFT; -fx-background-radius: 10 10 0 0;");

        // Header title
        Label headerLabel = new Label(gift.getName());
        headerLabel.setStyle("-fx-text-fill: " + AppColors.WHITE.getHexValue() + "; -fx-font-size: 24; -fx-font-weight: bold;");

        // "X" button to close
        lbClose = new Label("X");
        lbClose.setStyle("-fx-text-fill: " + AppColors.WHITE.getHexValue() + "; -fx-font-size: 24; -fx-font-weight: bold; -fx-cursor: hand;");
        lbClose.setOnMouseEntered(e -> lbClose.setStyle(
                "-fx-text-fill: " + AppColors.LIGHT_GRAY.getHexValue()
                        + "; -fx-font-size: 24; -fx-font-weight: bold; -fx-cursor: hand;"
        ));
        lbClose.setOnMouseExited(e -> lbClose.setStyle(
                "-fx-text-fill: " + AppColors.WHITE.getHexValue()
                        + "; -fx-font-size: 24; -fx-font-weight: bold; -fx-cursor: hand;"
        ));

        // Header layout: title on the left, spacing in the middle and "X" on the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(headerLabel, spacer, lbClose);

        // Enjoyer
        lbEnjoyer = new Label("Enjoyer");
        if (gift.getEnjoyer() == null) {
            tfEnjoyer = new TextField("-");
        } else {
            tfEnjoyer = new TextField(gift.getEnjoyer().getDetails().getName());
        }
        tfEnjoyer.setDisable(true);

        // Occasion
        lbOccasion = new Label("Occasion");
        tfOccasion = new TextField(gift.getOccasion());
        tfOccasion.setDisable(true);

        // Type
        lbType = new Label("Type");
        tfType = new TextField(gift.getType());
        tfType.setDisable(true);

        // Status
        cbStatus = new ComboBox<>();
        cbStatus.getItems().addAll(Status.getAllStatuses());
        cbStatus.setValue(gift.getStatus());
        lbStatus = new Label("Status");

        // Feedback
        lbFeedback = new Label("Feedback");
        cbFeedback = new ComboBox<>();
        cbFeedback.getItems().addAll(Feedback.getAllFeedbacks());
        cbFeedback.setValue(gift.getFeedback());
        cbFeedback.setDisable(cbStatus.getValue().equals(Status.PENDING.getStatus()));

        // Date
        lbDate = new Label("Date");
        tfDate = new TextField(gift.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        tfDate.setDisable(true);

        // Giftcard
        lbGiftcard = new Label("Giftcard message");
        taGiftcard = new TextArea(gift.getGiftMessage());
        taGiftcard.setWrapText(true);
        taGiftcard.setPrefRowCount(4);
        taGiftcard.setDisable(true);

        // Copy and Paste button
        btnCopyPaste = new Button("⧉");  // Unicode "two squares" copy icon
        btnCopyPaste.setStyle("-fx-font-size: 14px; -fx-padding: 6 10; -fx-background-color: " +
                AppColors.BLUE.getHexValue() + "; -fx-text-fill: " + AppColors.WHITE.getHexValue()
                + "; -fx-background-radius: 4; -fx-cursor: hand; " + "-fx-border-radius: 4; -fx-font-family: monospace;");

        Tooltip ttCopyPaste = new Tooltip("Copy message to clipboard");
        ttCopyPaste.setStyle("-fx-font-size: 12; -fx-background-color: " + AppColors.BLUE.getHexValue()
                + "; -fx-text-fill: " + AppColors.WHITE.getHexValue() + ";");
        btnCopyPaste.setTooltip(ttCopyPaste);

        // Grid for enjoyer, occasion, type, status, feedback, date, giftcard message and copy-paste button
        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(10);
        detailsGrid.setVgap(10);
        detailsGrid.setPadding(new Insets(10, 10, 10, 10));
        detailsGrid.add(lbEnjoyer, 0, 0);
        detailsGrid.add(tfEnjoyer, 0, 1);
        detailsGrid.add(lbOccasion, 1, 0);
        detailsGrid.add(tfOccasion, 1, 1);
        detailsGrid.add(lbType, 2, 0);
        detailsGrid.add(tfType, 2, 1);
        detailsGrid.add(lbStatus, 0, 3);
        detailsGrid.add(cbStatus, 0, 4);
        detailsGrid.add(lbFeedback, 1, 3);
        detailsGrid.add(cbFeedback, 1, 4);
        detailsGrid.add(lbDate, 2, 3);
        detailsGrid.add(tfDate, 2, 4);
        detailsGrid.add(lbGiftcard, 0, 5, 2, 1);
        detailsGrid.add(taGiftcard, 0, 6, 3, 1);
        detailsGrid.add(btnCopyPaste, 2, 5, 1, 1);

        // Buttons
        btnSave = new StyledButton(AppColors.GREEN, "Save preferences", AppColors.WHITE, "floppy-disk-solid-full.svg", true);
        btnSave.setDisable(true);
        btnClose = new StyledButton(AppColors.RED, "Close", AppColors.WHITE, "circle-xmark-regular-full.svg", true);
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(btnSave, btnClose);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(15));

        // Create main container
        VBox root = new VBox(header, detailsGrid, buttonBox);
        VBox.setVgrow(detailsGrid, Priority.ALWAYS);
        VBox.setVgrow(buttonBox, Priority.NEVER);
        root.setStyle("-fx-background-color: " + AppColors.WHITE.getHexValue()
                + "; -fx-border-color: " + AppColors.LIGHT_GRAY.getHexValue()
                + "; -fx-border-width: 1.5; -fx-border-radius: 10; -fx-background-radius: 10;"
        );

        // Window is draggable
        final double[] offset = new double[2];
        root.setOnMousePressed(e -> {
            offset[0] = e.getSceneX();
            offset[1] = e.getSceneY();
        });
        root.setOnMouseDragged(e -> {
            this.setX(e.getScreenX() - offset[0]);
            this.setY(e.getScreenY() - offset[1]);
        });

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        this.setScene(scene);
        this.setResizable(false);
    }

    private void registerHandlers() {
        // Copy and Paste button
        btnCopyPaste.setOnAction(e -> {
            if (taGiftcard != null && !taGiftcard.getText().trim().isEmpty()) {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent clipboardContent = new ClipboardContent();
                clipboardContent.putString(taGiftcard.getText());
                clipboard.setContent(clipboardContent);
            }
        });

        // Hover effects
        btnCopyPaste.setOnMouseEntered(e -> btnCopyPaste.setStyle("-fx-font-size: 14px; -fx-padding: 6 10; " +
                "-fx-background-color: " + AppColors.LIGHT_GRAY.getHexValue() + "; -fx-text-fill:" + AppColors.WHITE.getHexValue() +
                ";-fx-background-radius: 4; -fx-cursor: hand; -fx-border-radius: 4; -fx-font-family: monospace;"));

        btnCopyPaste.setOnMouseExited(e -> btnCopyPaste.setStyle("-fx-font-size: 14px; -fx-padding: 6 10; " +
                "-fx-background-color: " + AppColors.BLUE.getHexValue() + "; -fx-text-fill:" + AppColors.WHITE.getHexValue() +
                ";-fx-background-radius: 4; -fx-cursor: hand; -fx-border-radius: 4; -fx-font-family: monospace;"));

        cbStatus.setOnAction(event -> {
            btnSave.setDisable(false);
            if (cbStatus.getValue().equals(Status.PENDING.getStatus())) {
                cbFeedback.setValue(Feedback.UNKNOWN.getFeedback());
                cbFeedback.setDisable(true);
            } else {
                cbFeedback.setDisable(false);
            }
        });

        cbFeedback.setOnAction(event -> btnSave.setDisable(false));

        btnSave.setOnAction(event -> {
            if (cbStatus.getValue().equals(Status.GIFTED.getStatus()) && cbFeedback.getValue().equals(Feedback.UNKNOWN.getFeedback())) {
                new MessageModal(this, "Error saving", "Please define your enjoyer's feedback!");
            } else {
                manager.editGift(gift.getIdGift(), Feedback.convertFromString(cbFeedback.getValue()), Status.convertFromString(cbStatus.getValue()));
                new MessageModal(this, "Gift edited", "Your gift data was successfully updated!");
                this.close();
            }
        });

        btnClose.setOnAction(e -> {
            if (btnSave.isDisable())
                this.close();
            else if (OkCancelModal.show(this, "Close gift details", "Do you want to exit? You'll loose all unsaved data!")) {
                this.close();
            }
        });

        // Pressing the "X" has the same effect as Cancel button
        lbClose.setOnMouseClicked(e -> {
            if (btnSave.isDisable())
                this.close();
            else if (OkCancelModal.show(this, "Close gift details", "Do you want to exit? You'll loose all unsaved data.")) {
                this.close();
            }
        });
    }

    private void update() {

    }
}
