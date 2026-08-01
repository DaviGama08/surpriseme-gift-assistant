package pt.isec.gps2526_g42.surprise_me.ui.eventsViews;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pt.isec.gps2526_g42.surprise_me.model.Occasion;
import pt.isec.gps2526_g42.surprise_me.model.data.SurpriseMeManager;
import pt.isec.gps2526_g42.surprise_me.model.data.EnjoyerDetails;
import pt.isec.gps2526_g42.surprise_me.ui.AppColors;
import pt.isec.gps2526_g42.surprise_me.ui.StyledButton;
import pt.isec.gps2526_g42.surprise_me.ui.dialogsViews.MessageModal;
import pt.isec.gps2526_g42.surprise_me.ui.dialogsViews.OkCancelModal;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class EventDetailsModal extends Stage {
    private final SurpriseMeManager manager;
    private final Integer eventId;
    private final HashMap<String, Integer> enjoyerNameToIdMap;

    private Label lbClose;
    private TextField tfName;
    private ComboBox<String> cbEnjoyer;
    private ComboBox<String> cbOccasion;
    private DatePicker dpDate;
    private StyledButton btnClose;
    private StyledButton btnSave;
    private StyledButton btnDelete;
    private String baseTextFieldStyle;
    private String baseComboBoxStyle;
    private Label lbEventNameCtr;

    public EventDetailsModal(SurpriseMeManager manager, Integer eventId) {
        this.manager = manager;
        this.eventId = eventId;
        this.enjoyerNameToIdMap = new HashMap<>();
        initStyle(StageStyle.TRANSPARENT);
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        HBox header = new HBox();
        header.setStyle("-fx-background-color: " + AppColors.BLUE.getHexValue() +
                "; -fx-padding: 10 15; -fx-alignment: CENTER_LEFT; -fx-background-radius: 10 10 0 0;");

        // Title with the enjoyer name
        String stageTitle = "Event";
        if (eventId != null) {
            String fullName = manager.getEventEnjoyerNameById(eventId);
            Occasion occasion = manager.getEventOccasionById(eventId);
            String formattedName;
            String[] parts = fullName.trim().split("\\s+");
            if (parts.length > 1) {
                formattedName = parts[0] + " " + parts[parts.length - 1];
            } else {
                formattedName = fullName;
            }
            if (!fullName.isBlank()) {
                if (occasion != null) {
                    stageTitle = formattedName + " - " + occasion.getOccasion();
                } else {
                    stageTitle = formattedName;
                }
            }
        }

        Label headerLabel = new Label(stageTitle);
        headerLabel.setStyle("-fx-text-fill: " + AppColors.WHITE.getHexValue() +
                "; -fx-font-size: 24; -fx-font-weight: bold;");

        lbClose = new Label("X");
        lbClose.setStyle("-fx-text-fill: " + AppColors.WHITE.getHexValue() +
                "; -fx-font-size: 24; -fx-font-weight: bold; -fx-cursor: hand;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(headerLabel, spacer, lbClose);

        tfName = new TextField();
        baseTextFieldStyle = tfName.getStyle();

        lbEventNameCtr = new Label(tfName.getText().length() + "/50");
        lbEventNameCtr.setStyle("-fx-text-fill:" + AppColors.LIGHT_GRAY.getHexValue() + "; -fx-font-size: 12px;");

        VBox eventNameBox = new VBox(10, new Label("Event"), tfName, lbEventNameCtr);
        VBox.setVgrow(eventNameBox, Priority.ALWAYS);

        Label lbEnjoyer = new Label("Enjoyer");
        cbEnjoyer = new ComboBox<>();
        cbEnjoyer.setPromptText("Pick an enjoyer");
        cbEnjoyer.setMinWidth(200);

        Label lbOccasion = new Label("Occasion");
        cbOccasion = new ComboBox<>();
        cbOccasion.getItems().addAll(Occasion.getAllOccasions());
        cbOccasion.setMinWidth(200);
        baseComboBoxStyle = cbOccasion.getStyle();

        Label lbDate = new Label("Date");
        dpDate = new DatePicker();

        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(10);
        detailsGrid.setVgap(10);
        detailsGrid.add(eventNameBox, 0, 0, 2, 1);
        detailsGrid.add(lbEnjoyer, 0, 1);
        detailsGrid.add(cbEnjoyer, 0, 2);
        detailsGrid.add(lbOccasion, 1, 1);
        detailsGrid.add(cbOccasion, 1, 2);
        detailsGrid.add(lbDate, 0, 3);
        detailsGrid.add(dpDate, 0, 4);

        btnClose = new StyledButton(AppColors.RED, "Cancel", AppColors.WHITE, "circle-xmark-regular-full.svg", true);

        btnSave = new StyledButton(AppColors.GREEN, "Save", AppColors.WHITE, "floppy-disk-solid-full.svg", true);
        btnSave.setDisable(true);

        btnDelete = new StyledButton(AppColors.ORANGE, "Delete", AppColors.WHITE, "trash-can-solid-full.svg", true);

        HBox buttonBox;
        if (eventId != null) {
            buttonBox = new HBox(10, btnSave, btnDelete, btnClose);
        } else {
            buttonBox = new HBox(10, btnSave, btnClose);
        }
        buttonBox.setAlignment(Pos.CENTER);

        VBox content = new VBox(20, detailsGrid, buttonBox);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-background-color:" + AppColors.WHITE.getHexValue() + ";");

        VBox root = new VBox(header, content);
        root.setPadding(new Insets(0, 0, 15, 0));
        root.setStyle("-fx-background-color: " + AppColors.WHITE.getHexValue() +
                "; -fx-border-color: " + AppColors.LIGHT_GRAY.getHexValue() +
                "; -fx-border-width: 1.5; -fx-border-radius:10; -fx-background-radius:10; ");

        final double[] offset = new double[2];
        root.setOnMousePressed(e -> {
            offset[0] = e.getSceneX();
            offset[1] = e.getSceneY();
        });
        root.setOnMouseDragged(e -> {
            setX(e.getScreenX() - offset[0]);
            setY(e.getScreenY() - offset[1]);
        });

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        this.setScene(scene);
        this.setResizable(false);
    }

    private void registerHandlers() {
        tfName.textProperty().addListener((observable, oldValue, newValue) -> {
            String text = newValue == null ? "" : newValue;
            if (text.length() > 50) {
                tfName.setText(text.substring(0, 50));
            }
            lbEventNameCtr.setText(tfName.getText().length() + "/50");
            tfName.setStyle(baseTextFieldStyle);
            updateSaveButtonState();
        });

        lbClose.setOnMouseEntered(e -> lbClose.setStyle(
                "-fx-text-fill: " + AppColors.LIGHT_GRAY.getHexValue() +
                        "; -fx-font-size: 24; -fx-font-weight: bold; -fx-cursor: hand;"
        ));

        lbClose.setOnMouseExited(e -> lbClose.setStyle(
                "-fx-text-fill: " + AppColors.WHITE.getHexValue() +
                        "; -fx-font-size: 24; -fx-font-weight: bold; -fx-cursor: hand;"
        ));

        lbClose.setOnMouseClicked(e -> {
            if (OkCancelModal.show(this, "Close event", "Do you want to exit?")) {
                this.close();
            }
        });

        btnClose.setOnAction(e -> {
            if (OkCancelModal.show(this, "Close event", "Do you want to exit?")) {
                this.close();
            }
        });

        cbEnjoyer.valueProperty().addListener((obs, o, n) -> {
            cbEnjoyer.setStyle(baseComboBoxStyle);
            updateSaveButtonState();
        });

        cbOccasion.valueProperty().addListener((obs, o, n) -> {
            cbOccasion.setStyle(baseComboBoxStyle);
            updateSaveButtonState();
        });

        dpDate.valueProperty().addListener((obs, o, n) -> {
            dpDate.getEditor().setStyle("");
            updateSaveButtonState();
        });

        btnSave.setOnAction(e -> {
            if (!isFormValid()) {
                highlightInvalidFields();
                return;
            }

            String name = tfName.getText().trim();
            LocalDate date = dpDate.getValue();
            String occasionStr = cbOccasion.getValue();
            String enjoyerName = cbEnjoyer.getValue();

            int enjoyerId = -1;
            if (enjoyerName != null && !enjoyerName.isBlank()) {
                Integer id = enjoyerNameToIdMap.get(enjoyerName);
                if (id != null) {
                    enjoyerId = id;
                }
            }

            Occasion occasion = Occasion.convertFromString(occasionStr);

            if (eventId == null) {
                if (manager.addEvent(name, date, occasion, enjoyerId)) {
                    new MessageModal((Stage) this.getScene().getWindow(),
                            "Event added", "Your event was successfully added!");
                } else {
                    new MessageModal((Stage) this.getScene().getWindow(),
                            "Event not added", "We couldn't add your event, please try again.");
                }
            } else {
                if (manager.editEvent(eventId, name, date, occasion, enjoyerId)) {
                    new MessageModal((Stage) this.getScene().getWindow(),
                            "Event edited", "Your event was successfully edited!");
                } else {
                    new MessageModal((Stage) this.getScene().getWindow(),
                            "Event not updated", "We couldn't update your event, please try again.");
                }
            }

            this.close();
        });

        btnDelete.setOnAction(e -> {
            if (eventId == null) {
                return;
            }

            String eventName = manager.getEventNameById(eventId);
            Stage owner = (Stage) this.getScene().getWindow();
            boolean confirm = OkCancelModal.show(owner, "Confirm deletion", "Are you sure you want to delete \"" + eventName + "\"? This action is irreversible.");

            if (!confirm) {
                return;
            }

            boolean success = manager.removeEvent(eventId);
            if (success) {
                manager.save();
                new MessageModal(owner, "Event deleted", "The event was successfully deleted.");
                this.close();
            } else {
                new MessageModal(owner, "Error", "Failed to delete event.");
            }
        });
    }

    private void update() {
        loadEnjoyers();

        if (eventId == null) {
            tfName.clear();
            cbEnjoyer.getSelectionModel().clearSelection();
            cbOccasion.setValue(Occasion.OTHER.getOccasion());
            dpDate.setValue(LocalDate.now());
        } else {
            String name = manager.getEventNameById(eventId);
            String enjoyerName = manager.getEventEnjoyerNameById(eventId);
            Occasion occasion = manager.getEventOccasionById(eventId);
            LocalDate date = manager.getEventDateById(eventId);

            tfName.setText(name != null ? name : "");

            if (enjoyerName != null && !enjoyerName.isBlank() &&
                    cbEnjoyer.getItems().contains(enjoyerName)) {
                cbEnjoyer.setValue(enjoyerName);
            } else {
                cbEnjoyer.getSelectionModel().clearSelection();
            }

            if (occasion != null) {
                cbOccasion.setValue(occasion.getOccasion());
            } else {
                cbOccasion.getSelectionModel().clearSelection();
            }

            dpDate.setValue(date != null ? date : LocalDate.now());
        }

        updateSaveButtonState();
    }

    private boolean isFormValid() {
        String eventName = tfName.getText() == null ? "" : tfName.getText().trim();
        String occasionStr = cbOccasion.getValue();
        LocalDate date = dpDate.getValue();

        return !eventName.isEmpty()
                && occasionStr != null
                && !occasionStr.isBlank()
                && date != null;
    }

    private void updateSaveButtonState() {
        btnSave.setDisable(!isFormValid());
    }

    private void highlightInvalidFields() {
        String name = tfName.getText() == null ? "" : tfName.getText().trim();
        if (name.isEmpty()) {
            tfName.clear();
            tfName.setStyle("-fx-background-color: " + AppColors.RED.getHexValue() +
                    "; -fx-prompt-text-fill: " + AppColors.WHITE.getHexValue() + ";");
        }

        if (cbOccasion.getValue() == null) {
            cbOccasion.setStyle("-fx-background-color: " + AppColors.RED.getHexValue() +
                    "; -fx-prompt-text-fill: " + AppColors.WHITE.getHexValue() + ";");
        }

        if (dpDate.getValue() == null) {
            dpDate.getEditor().setStyle("-fx-background-color: " + AppColors.RED.getHexValue() +
                    "; -fx-prompt-text-fill: " + AppColors.WHITE.getHexValue() + ";");
        }
    }

    private void loadEnjoyers() {
        cbEnjoyer.getItems().clear();
        enjoyerNameToIdMap.clear();

        Map<Integer, EnjoyerDetails> enjoyers = manager.getEnjoyers();
        if (enjoyers == null) {
            return;
        }

        for (Map.Entry<Integer, EnjoyerDetails> entry : enjoyers.entrySet()) {
            Integer id = entry.getKey();
            EnjoyerDetails details = entry.getValue();
            if (details == null || details.getName() == null) {
                continue;
            }
            String name = details.getName();
            enjoyerNameToIdMap.put(name, id);
            cbEnjoyer.getItems().add(name);
        }
    }
}
