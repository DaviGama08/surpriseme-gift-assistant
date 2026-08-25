package pt.isec.gps2526_g42.surprise_me.ui.suggestions.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import pt.isec.gps2526_g42.surprise_me.ui.AppColors;
import pt.isec.gps2526_g42.surprise_me.ui.StyledButton;
import pt.isec.gps2526_g42.surprise_me.ui.suggestions.utils.GiftSuggestion;

public final class GiftCardPane extends VBox {
    private final GiftSuggestion suggestion;
    private final int row;
    private final int col;

    private final Label lbTitle;
    private final Label lbPrice;
    private final StyledButton btnDetails;
    private final StyledButton btnDislike;
    private final Label lbShortDescription;

    private Runnable onDetails;
    private Runnable onDislike;

    private static final int CARD_WIDTH = 350;
    private static final int CARD_PADDING = 14;
    private static final int ACTIONS_GAP = 10;
    private static final int CARD_VERTICAL_GAP = 8;
    private static final int DESC_TO_ACTIONS_SPACER = 18;
    private static final int SHORT_DESC_MAX_LINES = 3;

    public GiftCardPane(GiftSuggestion suggestion, int col, int row) {
        this.suggestion = suggestion;
        this.col = col;
        this.row = row;

        lbTitle = new Label(suggestion.title());
        lbTitle.setStyle("-fx-font-size:16px; -fx-font-weight:bold; -fx-text-fill:" + AppColors.DARK_GREY.getHexValue() + ";");
        setFillWidth(true);
        lbTitle.setWrapText(true);
        lbTitle.setMaxWidth(Double.MAX_VALUE);

        lbPrice = new Label(suggestion.price() == null ? "" : suggestion.price());
        lbPrice.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:" + AppColors.PURPLE.getHexValue() + ";");

        lbShortDescription = new Label(suggestion.shortDescription());
        lbShortDescription.setStyle("-fx-font-size:13px; -fx-text-fill:" + AppColors.LIGHT_GRAY.getHexValue() + ";");
        lbShortDescription.setWrapText(true);
        lbShortDescription.setTextOverrun(OverrunStyle.ELLIPSIS);
        lbShortDescription.setMaxWidth(Double.MAX_VALUE);

        double lineH = computeLineHeight(lbShortDescription);
        double clamp = lineH * SHORT_DESC_MAX_LINES;
        lbShortDescription.setMinHeight(Region.USE_PREF_SIZE);
        lbShortDescription.setPrefHeight(clamp);
        lbShortDescription.setMaxHeight(clamp);

        btnDetails = new StyledButton(AppColors.GREEN, "Gift details", AppColors.WHITE, "gift-solid-full.svg", true);
        btnDislike = new StyledButton(AppColors.RED, "Dislike", AppColors.WHITE, "thumbs-down-solid-full.svg", true);

        btnDetails.setOnAction(e -> {
            if (onDetails != null) onDetails.run();
        });
        btnDislike.setOnAction(e -> {
            if (onDislike != null) onDislike.run();
        });

        HBox actions = new HBox(ACTIONS_GAP, btnDetails, btnDislike);
        actions.setAlignment(Pos.CENTER);

        Region descActionsSpacer = new Region();
        descActionsSpacer.setMinHeight(DESC_TO_ACTIONS_SPACER);
        descActionsSpacer.setPrefHeight(DESC_TO_ACTIONS_SPACER);
        descActionsSpacer.setMaxHeight(DESC_TO_ACTIONS_SPACER);

        setSpacing(CARD_VERTICAL_GAP);
        getChildren().addAll(lbTitle, lbPrice, lbShortDescription, descActionsSpacer, actions);
        setPadding(new Insets(CARD_PADDING));
        setStyle("-fx-background-color:" + AppColors.WHITE.getHexValue()
                + "; -fx-background-radius:12; -fx-border-color: rgba(0,0,0,0.12); -fx-border-radius:12;");
        setMinWidth(0);
        setPrefWidth(CARD_WIDTH);
        setMaxWidth(CARD_WIDTH);

        setOnMouseEntered(ev -> setStyle("-fx-background-color: " + AppColors.CRISTAL_BLUE.getHexValue()
                + "; -fx-background-radius: 12; -fx-border-color:" + AppColors.BLUE.getHexValue()
                + "; -fx-border-radius: 12; -fx-border-width:1px;"));
        setOnMouseExited(ev -> setStyle("-fx-background-color: " + AppColors.WHITE.getHexValue()
                + "; -fx-background-radius: 12; -fx-border-color:" + AppColors.LIGHT_GRAY.getHexValue()
                + "; -fx-border-radius: 12;"));
    }

    private static double computeLineHeight(Label sample) {
        Text t = new Text("Ag");
        t.setFont(sample.getFont());
        return Math.ceil(t.getLayoutBounds().getHeight() * 1.15);
    }

    public void setOnDetails(Runnable r) {
        this.onDetails = r;
    }

    public void setOnDislike(Runnable r) {
        this.onDislike = r;
    }

    public void setLoading(boolean loading) {
        btnDislike.setDisable(loading);
        btnDetails.setDisable(loading);
        if (loading) {
            lbTitle.setText("Generating new suggestion...");
            lbPrice.setText("Calculating...");
            lbShortDescription.setText("Preparing details...");
        } else {
            lbTitle.setText(suggestion.title());
            lbPrice.setText(suggestion.price() == null ? "" : suggestion.price());
            lbShortDescription.setText(suggestion.shortDescription());
        }
    }

    public GiftSuggestion getSuggestion() {
        return suggestion;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }
}
