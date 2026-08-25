package pt.isec.gps2526_g42.surprise_me.ui.suggestions.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pt.isec.gps2526_g42.surprise_me.model.data.EnjoyerDetails;
import pt.isec.gps2526_g42.surprise_me.application.SurpriseMeManager;
import pt.isec.gps2526_g42.surprise_me.ui.StyledButton;
import pt.isec.gps2526_g42.surprise_me.ui.dialogs.MessageModal;
import pt.isec.gps2526_g42.surprise_me.ui.enjoyers.EnjoyerModal;
import pt.isec.gps2526_g42.surprise_me.ui.suggestions.modals.DefineGiftModal;
import pt.isec.gps2526_g42.surprise_me.ui.res.FontManager;
import pt.isec.gps2526_g42.surprise_me.ui.AppColors;

import java.util.HashMap;

public class GenerateGiftsPane extends VBox {
    private static final double BUTTON_WIDTH = 220;
    private static final double BUTTON_HEIGHT = 46;

    private final SurpriseMeManager manager;
    private HashMap<String, Integer> enjoyerNameToIdMap;

    // UI components
    private StyledButton btnFromList;
    private StyledButton btnNewEnjoyer;
    private StyledButton btnSpontaneous;
    private StyledButton btnNext;
    private VBox contentBox;
    private ComboBox<String> cbEnjoyers;
    private VBox spontaneousBox;
    private TextArea taDescription;
    private Label lbDescriptionCtr;

    private enum Mode {NONE, FROM_LIST, NEW_ENJOYER, SPONTANEOUS}

    private Mode mode;

    public GenerateGiftsPane(SurpriseMeManager manager) {
        this.manager = manager;
        this.enjoyerNameToIdMap = new HashMap<>();
        this.mode = Mode.NONE;
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        VBox topBox = new VBox(); // box that includes the window title

        // Create title
        Text initialTitle = new Text("Generate");
        initialTitle.setFill(AppColors.BLUE.getColor());
        initialTitle.setFont(FontManager.loadFont("ADLaMDisplay-Regular.ttf", 64));

        Text finalTitle = new Text("Gifts");
        finalTitle.setFill(AppColors.PURPLE.getColor());
        finalTitle.setFont(FontManager.loadFont("ADLaMDisplay-Regular.ttf", 64));

        HBox titleBox = new HBox(initialTitle, finalTitle);
        titleBox.setAlignment(Pos.TOP_LEFT);
        titleBox.setSpacing(0);
        topBox.getChildren().addAll(titleBox);
        topBox.setAlignment(Pos.TOP_CENTER);
        topBox.setPadding(new Insets(20, 15, 0, 15));

        Label choose = new Label("Choose source");
        choose.setFont(FontManager.loadFont("ADLaMDisplay-Regular.ttf", 32));

        btnFromList = new StyledButton(AppColors.BLUE, "Enjoyer from list", AppColors.WHITE, null, true);
        btnFromList.setPrefWidth(BUTTON_WIDTH);
        btnFromList.setPrefHeight(BUTTON_HEIGHT);

        btnNewEnjoyer = new StyledButton(AppColors.BLUE, "New enjoyer", AppColors.WHITE, null, true);
        btnNewEnjoyer.setPrefWidth(BUTTON_WIDTH);
        btnNewEnjoyer.setPrefHeight(BUTTON_HEIGHT);

        btnSpontaneous = new StyledButton(AppColors.BLUE, "Spontaneous suggestion", AppColors.WHITE, null, true);
        btnSpontaneous.setPrefWidth(BUTTON_WIDTH);
        btnSpontaneous.setPrefHeight(BUTTON_HEIGHT);

        HBox buttonsBox = new HBox(18, btnFromList, btnNewEnjoyer, btnSpontaneous);
        buttonsBox.setAlignment(Pos.CENTER_LEFT);

        VBox top = new VBox(8, choose, buttonsBox);
        top.setPadding(new Insets(10, 20, 10, 20));

        contentBox = new VBox(10);
        contentBox.setPadding(new Insets(10, 20, 10, 20));

        cbEnjoyers = new ComboBox<>();
        cbEnjoyers.setPromptText("Pick an enjoyer");
        cbEnjoyers.setPrefWidth(220);

        // Spontaneous suggestion
        taDescription = new TextArea();
        taDescription.setPromptText("Describe the target enjoyer the best you can");
        taDescription.setWrapText(true);
        taDescription.setPrefRowCount(5);

        lbDescriptionCtr = new Label("0/500");
        lbDescriptionCtr.setStyle("-fx-text-fill:" + AppColors.LIGHT_GRAY.getHexValue() + "; -fx-font-size: 12px;");

        spontaneousBox = new VBox(6, new Label("Describe the target enjoyer (required)"), taDescription, lbDescriptionCtr);

        btnNext = new StyledButton(AppColors.GREEN, "Next", AppColors.WHITE, "chevron-right-solid-full.svg", true);
        btnNext.setDisable(true);
        btnNext.setPrefWidth(BUTTON_WIDTH);
        btnNext.setPrefHeight(BUTTON_HEIGHT);

        HBox nextBox = new HBox(btnNext);
        nextBox.setAlignment(Pos.BOTTOM_RIGHT);
        nextBox.setPadding(new Insets(10, 20, 10, 20));

        VBox bottomBox = new VBox(16, top, contentBox, nextBox);
        setVgrow(contentBox, Priority.ALWAYS);
        bottomBox.setPadding(new Insets(10, 20, 20, 20));

        this.getChildren().addAll(topBox, bottomBox);
        setVgrow(bottomBox, Priority.ALWAYS);
    }

    private void registerHandlers() {
        // listener for description word counter
        taDescription.textProperty().addListener((o, old, val) -> {
            String s = val == null ? "" : val;
            if (s.length() > 500) {
                taDescription.setText(s.substring(0, 500));
            }
            lbDescriptionCtr.setText(taDescription.getText().length() + "/500");
            btnNext.setDisable(mode != Mode.SPONTANEOUS || taDescription.getText().trim().isEmpty());
        });

        // listener for combo box
        cbEnjoyers.valueProperty().addListener((o, old, val) ->
                btnNext.setDisable(mode != Mode.FROM_LIST || val == null || val.trim().isEmpty())
        );

        btnFromList.setOnAction(e -> setMode(Mode.FROM_LIST));

        btnNewEnjoyer.setOnAction(e -> {
            setMode(Mode.NEW_ENJOYER);
            EnjoyerModal enjoyerModal = new EnjoyerModal();
            enjoyerModal.initModality(Modality.WINDOW_MODAL);
            enjoyerModal.initOwner(this.getScene().getWindow());
            enjoyerModal.showAndWait();

            if (enjoyerModal.getDetails() != null) {
                EnjoyerDetails details = enjoyerModal.getDetails();
                manager.addEnjoyer(details);
                loadEnjoyers();
                Integer newId = enjoyerNameToIdMap.get(details.getName());
                if (newId != null) {
                    new MessageModal((Stage) this.getScene().getWindow(),
                            "Enjoyer added", "A new enjoyer was just added to your list! You will now proceed with your gift suggestion.");
                    DefineGiftModal define = new DefineGiftModal(manager, newId, details, null);
                    define.initOwner(getScene() != null ? getScene().getWindow() : null);
                    define.showAndWait();
                    setMode(Mode.NONE);
                }
            } else {
                new MessageModal((Stage) this.getScene().getWindow(),
                        "Operation cancelled", "Please choose a new source to generate your gift suggestion.");
                setMode(Mode.NONE);
            }
        });

        btnSpontaneous.setOnAction(e -> setMode(Mode.SPONTANEOUS));

        btnNext.setOnAction(e -> {
            if (mode == Mode.FROM_LIST) {
                String selectedName = cbEnjoyers.getValue();
                if (selectedName != null && !selectedName.trim().isEmpty()) {
                    Integer enjoyerId = enjoyerNameToIdMap.get(selectedName);
                    if (enjoyerId != null) {
                        EnjoyerDetails selectedEnjoyer = manager.getEnjoyers().get(enjoyerId);
                        if (selectedEnjoyer != null) {
                            DefineGiftModal define = new DefineGiftModal(manager, enjoyerId, selectedEnjoyer, null);
                            define.initOwner(getScene() != null ? getScene().getWindow() : null);
                            define.showAndWait();
                            setMode(Mode.NONE);
                        }
                    }
                }
            } else if (mode == Mode.SPONTANEOUS) {
                String description = taDescription.getText().trim();
                taDescription.clear();
                DefineGiftModal define = new DefineGiftModal(manager, null, null, description);
                define.initOwner(getScene() != null ? getScene().getWindow() : null);
                define.showAndWait();
                setMode(Mode.NONE);
            }
        });
    }

    private void update() {
        loadEnjoyers();
    }

    private void loadEnjoyers() {
        cbEnjoyers.getItems().clear();
        enjoyerNameToIdMap.clear();

        HashMap<Integer, EnjoyerDetails> enjoyers = manager.getEnjoyers();
        if (enjoyers != null) {
            for (var entry : enjoyers.entrySet()) {
                String name = entry.getValue().getName();
                enjoyerNameToIdMap.put(name, entry.getKey());
                cbEnjoyers.getItems().add(name);
            }
        }
    }

    private void setMode(Mode m) {
        mode = m;
        contentBox.getChildren().clear();

        switch (mode) {
            case FROM_LIST -> {
                VBox fromListBox = new VBox(new Label("Pick an enjoyer"), cbEnjoyers);
                fromListBox.setSpacing(8);
                contentBox.getChildren().add(fromListBox);

                String val = cbEnjoyers.getValue();
                boolean disable = (val == null || val.trim().isEmpty());
                btnNext.setDisable(disable);
            }
            case NEW_ENJOYER -> {
                // no usage
            }
            case SPONTANEOUS -> {
                contentBox.getChildren().add(spontaneousBox);
                btnNext.setVisible(true);
                btnNext.setDisable(taDescription.getText().trim().isEmpty());
            }
            case NONE -> {
                Label hint = new Label("Choose one of the options above...");
                hint.setStyle("-fx-text-fill:" + AppColors.LIGHT_GRAY.getHexValue() + "; -fx-font-size: 12px;");
                contentBox.getChildren().add(hint); 
                btnNext.setVisible(true);
                btnNext.setDisable(true);
            }
        }
    }
}
