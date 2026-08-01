package pt.isec.gps2526_g42.surprise_me.ui.enjoyerViews;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pt.isec.gps2526_g42.surprise_me.model.data.EnjoyerDetails;
import pt.isec.gps2526_g42.surprise_me.ui.AppColors;
import pt.isec.gps2526_g42.surprise_me.ui.StyledButton;
import pt.isec.gps2526_g42.surprise_me.ui.UIPropertyChangeManager;
import pt.isec.gps2526_g42.surprise_me.ui.dialogsViews.AskInputModal;
import pt.isec.gps2526_g42.surprise_me.ui.dialogsViews.OkCancelModal;

import java.util.ArrayList;

public class EnjoyerModal extends Stage {
    private String stageTitle;
    private EnjoyerDetails details;
    private final int enjoyerId;

    private TextField tfName;
    private TextField tfRelationship;
    private DatePicker dpBirthdate;
    private CheckBox cbUnknown;
    private TextField tfCountry;
    private TextField tfCity;
    private FlowPane fpLikes;
    private FlowPane fpAvoid;
    private TextArea taNotes;
    private Label lbNotesCtr;
    private StyledButton btnSave;
    private StyledButton btnReset;
    private StyledButton btnGiftHistory;
    private StyledButton btnClose;
    private Label lbClose;
    private String baseStyle;

    public EnjoyerModal() {
        tfName = new TextField();
        tfRelationship = new TextField();
        dpBirthdate = new DatePicker();
        cbUnknown = new CheckBox("I don't know");
        tfCountry = new TextField();
        tfCity = new TextField();
        taNotes = new TextArea();
        stageTitle = "Add Enjoyer";
        enjoyerId = -1;
        this.initStyle(StageStyle.TRANSPARENT);
        createViews();
        registerHandlers();
        update();
    }

    public EnjoyerModal(int enjoyerId, EnjoyerDetails details) {
        tfName = new TextField(details.getName());
        tfRelationship = new TextField(details.getRelationship());
        dpBirthdate = new DatePicker(details.getBirthDate());
        cbUnknown = new CheckBox("I don't know");
        if (details.getBirthDate() == null) {
            cbUnknown.setSelected(true);
        }
        tfCountry = new TextField(details.getCountry());
        tfCity = new TextField(details.getCity());
        taNotes = new TextArea(details.getNotes());
        stageTitle = "Edit Enjoyer";
        this.initStyle(StageStyle.TRANSPARENT);
        this.details = details;
        this.enjoyerId = enjoyerId;
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        HBox header = new HBox();
        header.setStyle("-fx-background-color: " + AppColors.BLUE.getHexValue() + "; -fx-padding: 10 15; -fx-alignment: CENTER_LEFT; -fx-background-radius: 10 10 0 0;");

        Label headerLabel = new Label(stageTitle);
        headerLabel.setStyle("-fx-text-fill: " + AppColors.WHITE.getHexValue() + "; -fx-font-size: 24; -fx-font-weight: bold;");

        lbClose = new Label("X");
        lbClose.setStyle("-fx-text-fill: " + AppColors.WHITE.getHexValue() + "; -fx-font-size: 24; -fx-font-weight: bold; -fx-cursor: hand;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(headerLabel, spacer, lbClose);

        baseStyle = tfName.getStyle();
        tfName.setPromptText("Name");

        tfRelationship.setPromptText("Relationship");

        dpBirthdate.setPromptText("Select date");
        // estado inicial: se "I don't know" estiver marcado, desativa logo o DatePicker
        dpBirthdate.setDisable(cbUnknown.isSelected());

        tfCountry.setPromptText("Country");
        tfCity.setPromptText("City");

        GridPane leftSideInfo = new GridPane();
        leftSideInfo.setHgap(10);
        leftSideInfo.setVgap(10);
        leftSideInfo.add(new Label("Name (required)"), 0, 0, 2, 1);
        leftSideInfo.add(tfName, 0, 1, 2, 1);
        leftSideInfo.add(new Label("Relationship (required)"), 0, 2);
        leftSideInfo.add(tfRelationship, 0, 3);
        leftSideInfo.add(new Label("Date of Birth"), 1, 2);
        leftSideInfo.add(dpBirthdate, 1, 3);
        leftSideInfo.add(cbUnknown, 1, 4);
        leftSideInfo.add(new Label("Country"), 0, 5);
        leftSideInfo.add(tfCountry, 0, 6);
        leftSideInfo.add(new Label("City"), 1, 5);
        leftSideInfo.add(tfCity, 1, 6);

        fpLikes = new FlowPane(5, 5);
        fpLikes.setPrefWrapLength(300);
        if (details != null) {
            for (String str : details.getLikes()) {
                fpLikes.getChildren().add(createTag(str));
            }
        }
        fpLikes.getChildren().add(createAddTagButton());
        ScrollPane scrollLikes = new ScrollPane(fpLikes);
        scrollLikes.setFitToWidth(true);
        scrollLikes.setMinHeight(50);
        scrollLikes.setMaxHeight(50);

        fpAvoid = new FlowPane(5, 5);
        fpAvoid.setPrefWrapLength(300);
        if (details != null) {
            for (String str : details.getDislikes()) {
                fpAvoid.getChildren().add(createTag(str));
            }
        }
        fpAvoid.getChildren().add(createAddTagButton());
        ScrollPane scrollAvoid = new ScrollPane(fpAvoid);
        scrollAvoid.setFitToWidth(true);
        scrollAvoid.setMinHeight(50);
        scrollAvoid.setMaxHeight(50);

        VBox likesBox = new VBox(new Label("Likes"), scrollLikes);
        VBox avoidBox = new VBox(new Label("Avoid"), scrollAvoid);

        taNotes = new TextArea();
        taNotes.setPromptText("Add notes about your enjoyer...");
        taNotes.setPrefRowCount(3);
        taNotes.setMinHeight(75);
        taNotes.setMaxHeight(75);
        taNotes.setWrapText(true);

        lbNotesCtr = new Label(taNotes.getText().length() + "/200");
        lbNotesCtr.setStyle("-fx-text-fill:" + AppColors.LIGHT_GRAY.getHexValue() + "; -fx-font-size: 12px;");

        VBox notesBox = new VBox(new Label("Notes"), taNotes, lbNotesCtr);
        VBox.setVgrow(notesBox, Priority.ALWAYS);

        VBox rightSideInfo = new VBox(likesBox, avoidBox, notesBox);
        rightSideInfo.setSpacing(20);
        VBox.setVgrow(rightSideInfo, Priority.ALWAYS);

        btnSave = new StyledButton(AppColors.GREEN, "Save preferences", AppColors.WHITE, "floppy-disk-solid-full.svg", true);
        btnReset = new StyledButton(AppColors.BLUE, "Reset", AppColors.WHITE, "rotate-left-solid-full.svg", true);
        btnGiftHistory = new StyledButton(AppColors.BLUE, "Gift history", AppColors.WHITE, "gift-solid-full.svg", true);
        btnClose = new StyledButton(AppColors.RED, "Close", AppColors.WHITE, "circle-xmark-regular-full.svg", true);
        btnSave.setDisable(true);
        HBox buttonBox = new HBox(10);
        if (details != null) {
            buttonBox.getChildren().addAll(btnSave, btnGiftHistory, btnClose);
        } else {
            buttonBox.getChildren().addAll(btnSave, btnReset, btnClose);
        }
        buttonBox.setAlignment(Pos.CENTER);

        HBox info = new HBox(leftSideInfo, rightSideInfo);
        info.setSpacing(20);
        VBox content = new VBox(20, info, buttonBox);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-background-color:" + AppColors.WHITE.getHexValue() + ";");

        VBox root = new VBox(header, content);
        root.setPadding(new Insets(0,0,15,0));
        root.setStyle("-fx-background-color: " + AppColors.WHITE.getHexValue()
                + "; -fx-border-color: " + AppColors.LIGHT_GRAY.getHexValue()
                + "; -fx-border-width: 1.5; -fx-border-radius: 10; -fx-background-radius: 10;"
        );

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
        if (details != null && details.getBirthDate() == null) {
            cbUnknown.setSelected(true);
        }
        dpBirthdate.setDisable(cbUnknown.isSelected());

        cbUnknown.selectedProperty().addListener((obs, oldVal, newVal) -> {
            dpBirthdate.setDisable(newVal);
            if (Boolean.TRUE.equals(newVal)) {
                dpBirthdate.setValue(null);
            }
            updateSaveButtonState();
        });

        lbClose.setOnMouseEntered(e -> lbClose.setStyle(
                "-fx-text-fill: " + AppColors.LIGHT_GRAY.getHexValue()
                        + "; -fx-font-size: 24; -fx-font-weight: bold; -fx-cursor: hand;"
        ));

        lbClose.setOnMouseExited(e -> lbClose.setStyle(
                "-fx-text-fill: " + AppColors.WHITE.getHexValue()
                        + "; -fx-font-size: 24; -fx-font-weight: bold; -fx-cursor: hand;"
        ));

        taNotes.textProperty().addListener((observable, oldValue, newValue) -> {
            String text = newValue == null ? "" : newValue;
            if (text.length() > 200) {
                taNotes.setText(text.substring(0, 200));
            }
            lbNotesCtr.setText(taNotes.getText().length() + "/200");
        });

        btnSave.setOnAction(e -> {
            if (!isFormValid()) {
                highlightInvalidFields();
                return;
            }

            if (details == null) {
                details = new EnjoyerDetails();
            }
            details.setName(tfName.getText());
            details.setRelationship(tfRelationship.getText());

            if (!cbUnknown.isSelected() && dpBirthdate.getValue() != null) {
                details.setBirthDate(dpBirthdate.getValue());
            } else {
                details.setBirthDate(null);
            }

            details.setNotes(taNotes.getText());
            details.setLikes(getLabelTexts(fpLikes));
            details.setDislikes(getLabelTexts(fpAvoid));
            details.setCity(tfCity.getText());
            details.setCountry(tfCountry.getText());
            this.close();
        });

        btnReset.setOnAction(e -> {
            if (OkCancelModal.show(this, "Reset data", "Are you sure you want to reset? You'll loose all data!")) {
                tfName.clear();
                tfName.setStyle(baseStyle);
                tfRelationship.clear();
                tfRelationship.setStyle(baseStyle);
                dpBirthdate.setValue(null);
                cbUnknown.setSelected(false);
                dpBirthdate.setDisable(false);
                tfCountry.clear();
                tfCity.clear();
                fpLikes.getChildren().clear();
                fpLikes.getChildren().add(createAddTagButton());
                fpAvoid.getChildren().clear();
                fpAvoid.getChildren().add(createAddTagButton());
                taNotes.clear();
                updateSaveButtonState();
            }
        });

        btnGiftHistory.setOnAction(e -> {
            UIPropertyChangeManager.getInstance().firePropertyChange(UIPropertyChangeManager.PROP_SHOW_GIFTS, null, enjoyerId);
            this.details = null;
            this.close();
        });

        btnClose.setOnAction(e -> {
            if (OkCancelModal.show(this, "Close enjoyer", "Do you want to exit? You'll loose all unsaved data!")) {
                this.details = null;
                this.close();
            }
        });

        lbClose.setOnMouseClicked(e -> {
            if (OkCancelModal.show(this, "Close enjoyer", "Do you want to exit? You'll loose all unsaved data.")) {
                this.details = null;
                this.close();
            }
        });

        tfName.textProperty().addListener((obs, oldText, newText) -> {
            tfName.setStyle(baseStyle);
            updateSaveButtonState();
        });

        tfRelationship.textProperty().addListener((obs, oldText, newText) -> {
            tfRelationship.setStyle(baseStyle);
            updateSaveButtonState();
        });

        dpBirthdate.valueProperty().addListener((obs, o, n) -> updateSaveButtonState());
    }

    private void update() {
        updateSaveButtonState();
    }

    private boolean isFormValid() {
        String name = tfName.getText() == null ? "" : tfName.getText().trim();
        String relationship = tfRelationship.getText() == null ? "" : tfRelationship.getText().trim();
        boolean birthOk = cbUnknown.isSelected() || dpBirthdate.getValue() != null;
        return !name.isEmpty() && !relationship.isEmpty() && birthOk;
    }

    private void updateSaveButtonState() {
        btnSave.setDisable(!isFormValid());
    }

    private void highlightInvalidFields() {
        if (tfName.getText().trim().isEmpty()) {
            tfName.clear();
            tfName.setStyle("-fx-background-color: " + AppColors.RED.getHexValue()
                    + "; -fx-prompt-text-fill: " + AppColors.WHITE.getHexValue() + ";");
        }
        if (tfRelationship.getText().trim().isEmpty()) {
            tfRelationship.clear();
            tfRelationship.setStyle("-fx-background-color: " + AppColors.RED.getHexValue()
                    + "; -fx-prompt-text-fill: " + AppColors.WHITE.getHexValue() + ";");
        }
        if (!cbUnknown.isSelected() && dpBirthdate.getValue() == null) {
            cbUnknown.setSelected(true);
        }
    }

    public EnjoyerDetails getDetails() {
        return details;
    }

    private Label createTag(String text) {
        Label tag = new Label(text);

        tag.setStyle("-fx-background-color:" + AppColors.PURPLE.getHexValue() + "; -fx-text-fill:"
                + AppColors.WHITE.getHexValue() + "; -fx-padding: 5 10 5 10; -fx-background-radius: 15;");

        tag.setOnMouseClicked(event -> {
            if (OkCancelModal.show(this, "Delete tag", "Are you sure you want to delete this?")) {
                ((FlowPane) tag.getParent()).getChildren().remove(tag);
            }
        });

        return tag;
    }

    private Button createAddTagButton() {
        Button btn = new Button("+");
        btn.setStyle("-fx-background-radius: 16; -fx-border-radius: 16; -fx-min-width: 32px; -fx-min-height: 32px; -fx-max-width: 32px; -fx-max-height: 32px; "
                + "-fx-border-color: " + AppColors.PURPLE.getHexValue() + "; -fx-text-fill:" + AppColors.PURPLE.getHexValue() + ";"
        );

        btn.setOnAction(event -> {
            AskInputModal inputModal = new AskInputModal();
            if (inputModal.show(this, "Add tag", "Tag:")) {
                String tagInput = inputModal.getInput();
                FlowPane parent = (FlowPane) btn.getParent();
                int idx = parent.getChildren().indexOf(btn);
                parent.getChildren().add(idx, createTag(tagInput));
            }
        });

        return btn;
    }

    private ArrayList<String> getLabelTexts(FlowPane fp) {
        ArrayList<String> labelTexts = new ArrayList<>();

        for (var child : fp.getChildren()) {
            if (child instanceof Label label) {
                labelTexts.add(label.getText());
            }
        }

        return labelTexts;
    }
}
