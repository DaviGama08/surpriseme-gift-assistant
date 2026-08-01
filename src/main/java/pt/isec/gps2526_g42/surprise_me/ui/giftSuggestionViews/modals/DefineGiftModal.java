package pt.isec.gps2526_g42.surprise_me.ui.giftSuggestionViews.modals;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pt.isec.gps2526_g42.surprise_me.model.Occasion;
import pt.isec.gps2526_g42.surprise_me.model.data.SurpriseMeManager;
import pt.isec.gps2526_g42.surprise_me.model.Type;
import pt.isec.gps2526_g42.surprise_me.model.data.EnjoyerDetails;
import pt.isec.gps2526_g42.surprise_me.model.llm.GiftCriteria;
import pt.isec.gps2526_g42.surprise_me.ui.AppColors;
import pt.isec.gps2526_g42.surprise_me.ui.StyledButton;
import pt.isec.gps2526_g42.surprise_me.ui.dialogsViews.MessageModal;
import pt.isec.gps2526_g42.surprise_me.ui.dialogsViews.OkCancelModal;
import pt.isec.gps2526_g42.surprise_me.ui.giftSuggestionViews.utils.GiftSuggestion;
import pt.isec.gps2526_g42.surprise_me.ui.giftSuggestionViews.utils.GiftSuggestionParser;

import java.util.List;

public class DefineGiftModal extends Stage {
    private final SurpriseMeManager manager;
    private final Integer enjoyerId;
    private final EnjoyerDetails enjoyer;
    private final String enjoyerDescription;
    private GiftCriteria criteria;
    private static final int REQUIRED_ITEMS = 4;

    // UI components
    private Label lbClose;
    private Label lbAvoidCtr;
    private Label lbIdeaCtr;
    private final VBox contentPane;
    private TextArea taIdea;
    private CheckBox cbNoClue;
    private ComboBox<String> cbOccasion;
    private Spinner<Integer> spMin;
    private Spinner<Integer> spMax;
    private ComboBox<String> cbType;
    private TextArea taAvoid;
    private CheckBox cbSustain;
    private CheckBox cbUseful;
    private StyledButton btnGenerate;
    private StyledButton btnReset;
    private StyledButton btnClose;

    public DefineGiftModal(SurpriseMeManager manager, Integer enjoyerId, EnjoyerDetails enjoyer, String enjoyerDescription) {
        this.manager = manager;
        this.enjoyerId = enjoyerId;
        this.enjoyer = enjoyer;
        this.enjoyerDescription = enjoyerDescription;
        contentPane = new VBox();
        initStyle(StageStyle.TRANSPARENT);
        initModality(Modality.WINDOW_MODAL);
        createViews();
        registerHandlers();
    }

    private void createViews() {
        // Title with the enjoyer name
        String title;
        if (enjoyer != null) {
            String fullName = enjoyer.getName();
            String formattedName;
            String[] parts = fullName.trim().split("\\s+");
            if (parts.length > 1) {
                formattedName = parts[0] + " " + parts[parts.length - 1];
            } else {
                formattedName = fullName;
            }
            title = "Define gift for " + formattedName;
        } else {
            title = "Define gift";
        }

        HBox header = new HBox();
        header.setStyle("-fx-background-color: " + AppColors.BLUE.getHexValue()
                + "; -fx-padding: 10 15; -fx-alignment: CENTER_LEFT; -fx-background-radius: 10 10 0 0;");

        Label headerLabel = new Label(title);
        headerLabel.setStyle("-fx-text-fill: " + AppColors.WHITE.getHexValue()
                + "; -fx-font-size: 24; -fx-font-weight: bold;");

        lbClose = new Label("X");
        lbClose.setStyle("-fx-text-fill: " + AppColors.WHITE.getHexValue()
                + "; -fx-font-size: 24; -fx-font-weight: bold; -fx-cursor: hand;");

        Region spacerHeader = new Region();
        HBox.setHgrow(spacerHeader, Priority.ALWAYS);
        header.getChildren().addAll(headerLabel, spacerHeader, lbClose);

        Label ideasLabel = new Label("Drop your idea here (if you want)");
        cbNoClue = new CheckBox("I have no clue");

        Region spacerIdeas = new Region();
        HBox.setHgrow(spacerIdeas, Priority.ALWAYS);
        HBox ideasHeader = new HBox(ideasLabel, spacerIdeas, cbNoClue);

        taIdea = new TextArea();
        taIdea.setPromptText("Describe your gift idea for this enjoyer...");
        taIdea.setPrefRowCount(5);
        taIdea.setWrapText(true);
        lbIdeaCtr = new Label(taIdea.getText().length() + "/500");
        lbIdeaCtr.setStyle("-fx-text-fill:" + AppColors.LIGHT_GRAY.getHexValue() + "; -fx-font-size: 12px;");

        VBox ideasBox = new VBox(8, ideasHeader, taIdea, lbIdeaCtr);

        cbOccasion = new ComboBox<>();
        cbOccasion.getItems().addAll(Occasion.getAllOccasions());
        cbOccasion.getSelectionModel().selectFirst();

        spMin = new Spinner<>(0, 1000, 20, 5);
        spMax = new Spinner<>(0, 2000, 60, 5);
        spMin.setEditable(true);
        spMax.setEditable(true);

        cbType = new ComboBox<>();
        cbType.getItems().addAll(Type.getAllTypes());
        cbType.getSelectionModel().selectFirst();

        taAvoid = new TextArea();
        taAvoid.setPromptText("Better leave these things out...");
        taAvoid.setPrefRowCount(4);
        taAvoid.setWrapText(true);
        lbAvoidCtr = new Label(taAvoid.getText().length() + "/250");
        lbAvoidCtr.setStyle("-fx-text-fill:" + AppColors.LIGHT_GRAY.getHexValue() + "; -fx-font-size: 12px;");

        cbSustain = new CheckBox("Concerned about sustainability?");
        cbUseful = new CheckBox("Are you looking for a useful gift?");
        cbUseful.setSelected(true);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Pick occasion"), 0, 0);
        grid.add(cbOccasion, 0, 1);
        grid.add(new Label("Min. budget (€)"), 1, 0);
        grid.add(spMin, 1, 1);
        grid.add(new Label("Max. budget (€)"), 2, 0);
        grid.add(spMax, 2, 1);
        grid.add(new Label("Pick type"), 3, 0);
        grid.add(cbType, 3, 1);
        grid.add(new Label("Better leave these out"), 0, 3, 3, 1);
        grid.add(taAvoid, 0, 4, 3, 2);
        grid.add(lbAvoidCtr, 0, 6, 3, 1);
        grid.add(cbSustain, 3, 4);
        grid.add(cbUseful, 3, 5);

        btnGenerate = new StyledButton(AppColors.GREEN, "Generate ideas", AppColors.WHITE, "lightbulb-solid-full.svg", true);
        btnReset = new StyledButton(AppColors.BLUE, "Reset", AppColors.WHITE, "rotate-left-solid-full.svg", true);
        btnClose = new StyledButton(AppColors.RED, "Close", AppColors.WHITE, "circle-xmark-regular-full.svg", true);

        HBox buttons = new HBox(10, btnGenerate, btnReset, btnClose);
        buttons.setAlignment(Pos.CENTER);

        contentPane.getChildren().addAll(ideasBox, grid, buttons);
        contentPane.setPadding(new Insets(16));
        contentPane.setStyle("-fx-background-color: " + AppColors.WHITE.getHexValue() + "; -fx-background-radius: 0 0 10 10;");

        VBox root = new VBox(header, contentPane);
        root.setStyle("-fx-background-color: " + AppColors.WHITE.getHexValue()
                + "; -fx-border-color: " + AppColors.LIGHT_GRAY.getHexValue()
                + "; -fx-border-width: 1.5; -fx-border-radius: 10; -fx-background-radius: 10;");

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        setScene(scene);
        setTitle("Define gift");
        setResizable(true);
        sizeToScene();
        centerOnScreen();

        final double[] offset = new double[2];
        root.setOnMousePressed(e -> {
            offset[0] = e.getSceneX();
            offset[1] = e.getSceneY();
        });
        root.setOnMouseDragged(e -> {
            setX(e.getScreenX() - offset[0]);
            setY(e.getScreenY() - offset[1]);
        });
    }

    private GiftCriteria buildCriteria() {
        GiftCriteria c = new GiftCriteria();
        try {
            if (spMin.getValue() != null) {
                c.setMinBudget(spMin.getValue());
            }
            if (spMax.getValue() != null) {
                c.setMaxBudget(spMax.getValue());
            }
        } catch (NumberFormatException ex) {
            // ignored
        }
        c.setOccasion(cbOccasion.getValue());
        c.setGiftType(cbType.getValue());
        c.setThingsToAvoid(taAvoid.getText());
        c.setSustainable(cbSustain.isSelected());
        c.setUseful(cbUseful.isSelected());
        c.setAdditionalIdeas(cbNoClue.isSelected() ? "I don't have any ideas" : taIdea.getText().trim());
        return c;
    }

    private void registerHandlers() {
        taIdea.textProperty().addListener((observable, oldValue, newValue) -> {
            String text = newValue == null ? "" : newValue;
            if (text.length() > 500) {
                taIdea.setText(text.substring(0, 500));
            }
            lbIdeaCtr.setText(taIdea.getText().length() + "/500");
        });

        taAvoid.textProperty().addListener((observable, oldValue, newValue) -> {
            String text = newValue == null ? "" : newValue;
            if (text.length() > 250) {
                taAvoid.setText(text.substring(0, 250));
            }
            lbAvoidCtr.setText(taAvoid.getText().length() + "/250");
        });

        cbNoClue.selectedProperty().addListener((o, oldValue, newValue) -> {
            taIdea.setDisable(newValue);
            if (Boolean.TRUE.equals(newValue)) {
                taIdea.clear();
            }
        });

        lbClose.setOnMouseEntered(e -> lbClose.setStyle(
                "-fx-text-fill: " + AppColors.LIGHT_GRAY.getHexValue()
                        + "; -fx-font-size: 24; -fx-font-weight: bold; -fx-cursor: hand;"));

        lbClose.setOnMouseExited(e -> lbClose.setStyle(
                "-fx-text-fill: " + AppColors.WHITE.getHexValue()
                        + "; -fx-font-size: 24; -fx-font-weight: bold; -fx-cursor: hand;"));

        lbClose.setOnMouseClicked(e -> {
            if (OkCancelModal.show(this, "Close suggestion", "Are you sure you want to quit? You'll have to start over!")) {
                this.close();
            }
        });

        btnReset.setOnAction(e -> {
            if (OkCancelModal.show(this, "Reset gift criteria", "Are you sure you want to reset? You'll loose all data!")) {
                taIdea.clear();
                cbNoClue.setSelected(false);
                cbOccasion.getSelectionModel().selectFirst();
                spMin.getValueFactory().setValue(20);
                spMax.getValueFactory().setValue(60);
                cbType.getSelectionModel().selectFirst();
                taAvoid.clear();
                cbSustain.setSelected(false);
                cbUseful.setSelected(true);
            }
        });

        btnClose.setOnAction(e -> {
            if (OkCancelModal.show(this, "Close suggestion", "Are you sure you want to quit? You'll have to start over!")) {
                this.close();
            }
        });

        btnGenerate.setOnAction(e -> {
            if (!validateBudget()) {
                return;
            }

            if (taIdea.getText().isEmpty()) {
                cbNoClue.setSelected(true);
            }

            criteria = buildCriteria();
            final double targetWidth = getScene().getWidth();

            boolean consentGranted = OkCancelModal.show(
                    this,
                    "External AI service",
                    "SurpriseMe will send the recipient details and gift criteria shown here to the configured external LLM provider. Continue?"
            );
            if (!consentGranted) {
                return;
            }

            LoadingModal loading = new LoadingModal();
            loading.initOwner(getScene().getWindow());
            loading.showLoading();
            btnGenerate.setDisable(true);

            new Thread(() -> {
                try {
                    String suggestions = (enjoyer != null)
                            ? manager.generateGiftSuggestions(enjoyer, criteria, true)
                            : manager.generateSpontaneousGifts(enjoyerDescription, criteria, true);

                    List<GiftSuggestion> parsed = GiftSuggestionParser.parseAll(suggestions);
                    if (parsed.size() != REQUIRED_ITEMS) {
                        throw new RuntimeException("Incomplete response");
                    }

                    Platform.runLater(() -> {
                        loading.hideLoading();
                        btnGenerate.setDisable(false);

                        GiftSuggestionsModal modal = new GiftSuggestionsModal(
                                manager, enjoyerId, enjoyer, enjoyerDescription,
                                cbType.getValue(), cbOccasion.getValue(), suggestions, criteria, true
                        );
                        modal.initOwner(getScene().getWindow());
                        modal.showAndWait();

                        sizeToScene();
                        setWidth(targetWidth);
                        centerOnScreen();
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        loading.hideLoading();
                        btnGenerate.setDisable(false);
                        new MessageModal((Stage) this.getScene().getWindow(), "Error", "Could not load suggestions. Please try again.");
                    });
                }
            }, "llm-gift-generator").start();
        });
    }

    private boolean validateBudget() {
        try {
            int min = Integer.parseInt(spMin.getEditor().getText().trim());
            int max = Integer.parseInt(spMax.getEditor().getText().trim());
            if (min > max) {
                new MessageModal((Stage) this.getScene().getWindow(), "Budget error", "Minimum budget can not be greater than maximum budget!");
                return false;
            }
            spMin.getValueFactory().setValue(min);
            spMax.getValueFactory().setValue(max);
            return true;
        } catch (Exception error) {
            new MessageModal((Stage) this.getScene().getWindow(), "Budget error", "Please make sure that both the minimum and maximum budget are integers!");
            return false;
        }
    }
}
