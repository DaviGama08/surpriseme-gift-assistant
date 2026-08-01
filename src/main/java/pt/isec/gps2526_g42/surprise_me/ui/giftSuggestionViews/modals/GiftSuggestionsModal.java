package pt.isec.gps2526_g42.surprise_me.ui.giftSuggestionViews.modals;

import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import pt.isec.gps2526_g42.surprise_me.model.Occasion;
import pt.isec.gps2526_g42.surprise_me.model.data.SurpriseMeManager;
import pt.isec.gps2526_g42.surprise_me.model.Type;
import pt.isec.gps2526_g42.surprise_me.model.data.EnjoyerDetails;
import pt.isec.gps2526_g42.surprise_me.model.llm.GiftCriteria;
import pt.isec.gps2526_g42.surprise_me.ui.AppColors;
import pt.isec.gps2526_g42.surprise_me.ui.dialogsViews.GiftMessageModal;
import pt.isec.gps2526_g42.surprise_me.ui.dialogsViews.MessageModal;
import pt.isec.gps2526_g42.surprise_me.ui.dialogsViews.OkCancelModal;
import pt.isec.gps2526_g42.surprise_me.ui.giftSuggestionViews.components.GiftCardPane;
import pt.isec.gps2526_g42.surprise_me.ui.giftSuggestionViews.components.GiftChoicePane;
import pt.isec.gps2526_g42.surprise_me.ui.giftSuggestionViews.utils.GiftCriteriaUtil;
import pt.isec.gps2526_g42.surprise_me.ui.giftSuggestionViews.utils.GiftSuggestion;
import pt.isec.gps2526_g42.surprise_me.ui.giftSuggestionViews.utils.GiftSuggestionParser;

import java.util.*;

public class GiftSuggestionsModal extends Stage {
    private final SurpriseMeManager manager;
    private final Integer enjoyerId;
    private final EnjoyerDetails enjoyer;
    private final String enjoyerDescription;

    private final String selectedTypeLabel;
    private final String selectedOccasionLabel;
    private final GiftCriteria baseCriteria;
    private final boolean consentGranted;

    private final VBox contentPane = new VBox();
    private Label headerLabel;

    private GridPane cardsGrid;
    private Region suggestionsRoot;

    private final List<String> rejectedGiftIdeas = new ArrayList<>();
    private final Set<String> currentGiftTitles = new HashSet<>();

    private static final int PREF_VIEWPORT_HEIGHT = 560;
    private static final int SUGGESTIONS_VIEW_WIDTH = 750;
    private static final int DETAILS_VIEW_WIDTH = 720;

    public GiftSuggestionsModal(SurpriseMeManager manager, Integer enjoyerId, EnjoyerDetails enjoyer,
                                String enjoyerDescription, String selectedTypeLabel, String selectedOccasionLabel,
                                String rawSuggestions, GiftCriteria baseCriteria, boolean consentGranted) {
        this.manager = manager;
        this.enjoyerId = enjoyerId;
        this.enjoyer = enjoyer;
        this.enjoyerDescription = enjoyerDescription;
        this.selectedTypeLabel = selectedTypeLabel;
        this.selectedOccasionLabel = selectedOccasionLabel;
        this.baseCriteria = baseCriteria;
        this.consentGranted = consentGranted;
        String rs = rawSuggestions == null ? "" : rawSuggestions;
        initStyle(StageStyle.TRANSPARENT);
        initModality(Modality.WINDOW_MODAL);
        createViews();
        populateSuggestions(rs);
    }

    private void createViews() {
        HBox header = new HBox();
        header.setStyle("-fx-background-color: " + AppColors.BLUE.getHexValue()
                + "; -fx-padding: 10 15; -fx-alignment: CENTER_LEFT; -fx-background-radius: 10 10 0 0;");

        headerLabel = new Label("Gifts suggested");
        headerLabel.setStyle("-fx-text-fill: " + AppColors.WHITE.getHexValue()
                + "; -fx-font-size: 20; -fx-font-weight: bold;");

        Label closeLabel = new Label("X");
        closeLabel.setStyle("-fx-text-fill: " + AppColors.WHITE.getHexValue()
                + "; -fx-font-size: 24; -fx-font-weight: bold; -fx-cursor: hand;");
        closeLabel.setOnMouseEntered(e -> closeLabel.setStyle(
                "-fx-text-fill: " + AppColors.LIGHT_GRAY.getHexValue()
                        + "; -fx-font-size: 24; -fx-font-weight: bold; -fx-cursor: hand;"));
        closeLabel.setOnMouseExited(e -> closeLabel.setStyle(
                "-fx-text-fill: " + AppColors.WHITE.getHexValue()
                        + "; -fx-font-size: 24; -fx-font-weight: bold; -fx-cursor: hand;"));
        closeLabel.setOnMouseClicked(e -> {
            if (OkCancelModal.show(this, "Close suggestion", "Are you sure you want to quit? You'll have to start over!")) {
                this.close();
            }
        });

        Region spacerHeader = new Region();
        HBox.setHgrow(spacerHeader, Priority.ALWAYS);
        header.getChildren().addAll(headerLabel, spacerHeader, closeLabel);

        cardsGrid = new GridPane();
        cardsGrid.setAlignment(Pos.CENTER);
        cardsGrid.setHgap(16);
        cardsGrid.setVgap(16);
        cardsGrid.setPadding(new Insets(16, 16, 16, 16));
        cardsGrid.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        ColumnConstraints c1 = new ColumnConstraints();
        ColumnConstraints c2 = new ColumnConstraints();
        c1.setHgrow(Priority.NEVER);
        c2.setHgrow(Priority.NEVER);
        c1.setHalignment(HPos.CENTER);
        c2.setHalignment(HPos.CENTER);
        cardsGrid.getColumnConstraints().setAll(c1, c2);

        StackPane centered = new StackPane(cardsGrid);
        centered.setAlignment(Pos.CENTER);

        ScrollPane sp = new ScrollPane(centered);
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);
        sp.setPrefViewportHeight(PREF_VIEWPORT_HEIGHT);
        sp.setPrefHeight(PREF_VIEWPORT_HEIGHT);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setStyle("-fx-background-color: transparent; -fx-background: " + AppColors.WHITE.getHexValue() + ";");

        VBox content = new VBox(sp);
        content.setStyle("-fx-background-color: " + AppColors.WHITE.getHexValue()
                + "; -fx-background-radius: 0 0 10 10;");
        suggestionsRoot = content;

        contentPane.getChildren().setAll(content);

        VBox root = new VBox(header, contentPane);
        root.setStyle("-fx-background-color: " + AppColors.WHITE.getHexValue()
                + "; -fx-border-color: " + AppColors.LIGHT_GRAY.getHexValue()
                + "; -fx-border-width: 1.5; -fx-border-radius: 10; -fx-background-radius: 10;");

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        setScene(scene);
        setTitle("Gift suggestions");
        setResizable(true);

        setWidth(SUGGESTIONS_VIEW_WIDTH);
        setHeight(PREF_VIEWPORT_HEIGHT);
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

    private void populateSuggestions(String raw) {
        cardsGrid.getChildren().clear();
        currentGiftTitles.clear();

        var suggestions = GiftSuggestionParser.parseAll(raw);
        if (suggestions.size() != 4) {
            new MessageModal((Stage) this.getScene().getWindow(),
                    "Incomplete response", "Could not load all suggestions. Please try again.");
            close();
            return;
        }

        int row = 0;
        int col = 0;
        for (GiftSuggestion s : suggestions) {
            if (s.title().isBlank() || s.description().isBlank()) {
                new MessageModal((Stage) this.getScene().getWindow(),
                        "Incomplete response", "Could not load all suggestions. Please try again.");
                close();
                return;
            }
            addCard(s, col, row);
            col = (col + 1) % 2;
            if (col == 0) {
                row++;
            }
        }
    }

    private void addCard(GiftSuggestion s, int col, int row) {
        currentGiftTitles.add(s.title());
        GiftCardPane card = new GiftCardPane(s, col, row);

        card.setOnDetails(() -> showChoice(card.getSuggestion()));
        card.setOnDislike(() -> {
            rejectedGiftIdeas.add(card.getSuggestion().title());
            currentGiftTitles.remove(card.getSuggestion().title());
            card.setLoading(true);
            regenerate(card);
        });

        cardsGrid.add(card, col, row);
        GridPane.setHgrow(card, Priority.NEVER);
        GridPane.setFillWidth(card, false);
        GridPane.setHalignment(card, javafx.geometry.HPos.CENTER);
        GridPane.setValignment(card, javafx.geometry.VPos.TOP);
    }

    private void showChoice(GiftSuggestion s) {
        headerLabel.setText("Gift selected");
        setWidth(DETAILS_VIEW_WIDTH);

        GiftChoicePane choice = new GiftChoicePane(s.title(), s.description());

        choice.setOnSave(() -> {
            Type type = Type.convertFromString(selectedTypeLabel);
            Occasion occ = Occasion.convertFromString(selectedOccasionLabel);

            // Add the gift first
            int giftId = -1;
            if (enjoyerId != null) {
                manager.addGift(s.title(), type, occ, enjoyerId);
            } else {
                manager.addGift(s.title(), type, occ);
            }

            // Get the gift ID from the last added gift (after adding it)
            var gifts = manager.getGifts();
            if (!gifts.isEmpty()) {
                giftId = gifts.getLast().getIdGift();
            }
            // Show a success message

            new MessageModal((Stage) this.getScene().getWindow(),
                    "Gift saved", "Your gift suggestion was successfully saved!");
            // Ask if the user wants to create a gift message
            boolean wantsMessage = GiftMessageModal.askForGiftMessage((Stage) this.getScene().getWindow());

            if (wantsMessage) {
                // Create a gift message in background thread
                final int finalGiftId = giftId;
                new Thread(() -> {
                    try {
                        String generatedMessage;

                        if (enjoyer != null) {
                            // For enjoyer-based gifts, use a personalized message
                            generatedMessage = manager.generateGiftMessage(s.title(), s.description(), enjoyer.getName(), enjoyer.getRelationship(), selectedOccasionLabel, consentGranted);
                        } else {
                            // For spontaneous gifts, use a generic/formal message
                            generatedMessage = manager.generateSpontaneousGiftMessage(s.title(), s.description(), selectedOccasionLabel, consentGranted);
                        }

                        Platform.runLater(() -> {
                            // Show the generated message and wait until confirmation
                            while (true) {
                                boolean acceptMessage = GiftMessageModal.showGiftMessage(
                                        (Stage) this.getScene().getWindow(), generatedMessage);

                                if (acceptMessage && finalGiftId != -1) {
                                    // Save the message to the gift
                                    manager.setGiftMessage(finalGiftId, generatedMessage);

                                    // Show confirmation
                                    new MessageModal((Stage) this.getScene().getWindow(),
                                            "Gift message saved", "Your gift message has been saved successfully!");
                                    break;
                                } else {
                                    // Show closing confirmation
                                    if (OkCancelModal.show(this, "Reject gift message", "Are you sure you want to reject this message? It won't be saved!")) {
                                        break;
                                    }
                                }
                            }
                            // Close windows
                            Window owner = getOwner();
                            close();
                            if (owner instanceof Stage os) {
                                os.close();
                            }
                        });
                    } catch (Exception e) {
                        Platform.runLater(() -> {
                            new MessageModal((Stage) this.getScene().getWindow(),
                                    "Error", "Failed to generate gift message. Please try again later.");

                            // Close windows anyway
                            Window owner = getOwner();
                            close();
                            if (owner instanceof Stage os) {
                                os.close();
                            }
                        });
                    }
                }).start();
            } else {
                // User doesn't want a message, close
                Window owner = getOwner();
                close();
                if (owner instanceof Stage os) {
                    os.close();
                }
            }
        });

        choice.setOnBack(() -> {
            headerLabel.setText("Gifts suggested");
            contentPane.getChildren().setAll(suggestionsRoot);
            setWidth(SUGGESTIONS_VIEW_WIDTH);
        });

        choice.setOnClose(() -> {
            if (OkCancelModal.show(this, "Close suggestion", "Are you sure you want to quit? You'll have to start over!")) {
                close();
            }
        });

        contentPane.getChildren().setAll(choice);
        centerOnScreen();
    }

    private void regenerate(GiftCardPane oldCard) {
        new Thread(() -> {
            try {
                GiftCriteria criteria = GiftCriteriaUtil.withRejections(
                        baseCriteria, currentGiftTitles, rejectedGiftIdeas
                );

                String text = (enjoyer != null)
                        ? manager.generateGiftSuggestions(enjoyer, criteria, consentGranted)
                        : manager.generateSpontaneousGifts(enjoyerDescription, criteria, consentGranted);

                List<GiftSuggestion> list = GiftSuggestionParser.parseAll(text);
                if (list.isEmpty()) {
                    throw new RuntimeException("Empty response");
                }
                GiftSuggestion s = list.getFirst();

                if (!s.isValidTitle() || s.description().isBlank()
                        || currentGiftTitles.contains(s.title())
                        || rejectedGiftIdeas.contains(s.title())) {
                    Platform.runLater(() -> {
                        oldCard.setLoading(false);
                        new MessageModal((Stage) this.getScene().getWindow(),
                                "Error", "Failed to generate a valid unique suggestion. Please try again!");
                    });
                    return;
                }

                Platform.runLater(() -> replaceCard(oldCard, s));
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    oldCard.setLoading(false);
                    new MessageModal((Stage) this.getScene().getWindow(),
                            "Error", "Failed to generate a new suggestion. Please try again!");
                });
            }
        }, "single-card-regenerator").start();
    }

    private void replaceCard(GiftCardPane oldCard, GiftSuggestion s) {
        int col = oldCard.getCol();
        int row = oldCard.getRow();

        cardsGrid.getChildren().removeIf(node ->
                GridPane.getColumnIndex(node) != null && GridPane.getRowIndex(node) != null &&
                        GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row
        );

        currentGiftTitles.add(s.title());
        addCard(s, col, row);
    }
}
