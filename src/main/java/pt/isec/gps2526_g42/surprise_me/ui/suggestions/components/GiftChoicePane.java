package pt.isec.gps2526_g42.surprise_me.ui.suggestions.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import pt.isec.gps2526_g42.surprise_me.ui.AppColors;
import pt.isec.gps2526_g42.surprise_me.ui.StyledButton;

public final class GiftChoicePane extends VBox {
    private Runnable onSave;
    private Runnable onBack;
    private Runnable onClose;

    public GiftChoicePane(String giftName, String description) {
        Label lbtitle = new Label("You have selected");
        lbtitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + AppColors.DARK_GREY.getHexValue() + ";");

        Label lbGiftName = new Label(giftName == null ? "" : giftName);
        lbGiftName.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + AppColors.PURPLE.getHexValue() + ";");
        lbGiftName.setWrapText(true);
        lbGiftName.setMaxWidth(520);
        lbGiftName.setAlignment(Pos.CENTER);

        Label lbDescription = new Label(description == null ? "" : description);
        lbDescription.setStyle("-fx-font-size: 14px; -fx-text-fill: " + AppColors.LIGHT_GRAY.getHexValue() + ";");
        lbDescription.setWrapText(true);
        lbDescription.setMaxWidth(520);
        lbDescription.setAlignment(Pos.CENTER);

        VBox giftInfo = new VBox(10, lbtitle, lbGiftName, lbDescription);
        giftInfo.setPadding(new Insets(15, 20, 15, 20));
        giftInfo.setStyle("-fx-background-color: " + AppColors.CRISTAL_BLUE.getHexValue()
                + "; -fx-background-radius: 8; -fx-border-radius: 8;");
        giftInfo.setAlignment(Pos.CENTER);

        StyledButton btnSave = new StyledButton(AppColors.GREEN, "Save", AppColors.WHITE, "floppy-disk-solid-full.svg", true);
        btnSave.setOnAction(e -> {
            if (onSave != null) onSave.run();
        });

        StyledButton btnBack = new StyledButton(AppColors.BLUE, "Back to suggestions", AppColors.WHITE, "rotate-left-solid-full.svg", true);
        btnBack.setOnAction(e -> {
            if (onBack != null) onBack.run();
        });

        StyledButton btnClose = new StyledButton(AppColors.RED, "Close", AppColors.WHITE, "circle-xmark-regular-full.svg", true);
        btnClose.setOnAction(e -> {
            if (onClose != null) onClose.run();
        });

        HBox buttons = new HBox(12, btnSave, btnBack, btnClose);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(15, 0, 0, 0));

        Region topSpacer = new Region();
        Region bottomSpacer = new Region();
        VBox.setVgrow(topSpacer, Priority.ALWAYS);
        VBox.setVgrow(bottomSpacer, Priority.ALWAYS);

        getChildren().addAll(topSpacer, giftInfo, buttons, bottomSpacer);
        setPadding(new Insets(16));
        setStyle("-fx-background-color: " + AppColors.WHITE.getHexValue() + "; -fx-background-radius: 0 0 10 10;");
        setAlignment(Pos.CENTER);
        setFillWidth(false);
    }

    public void setOnSave(Runnable r) {
        this.onSave = r;
    }

    public void setOnBack(Runnable r) {
        this.onBack = r;
    }

    public void setOnClose(Runnable r) {
        this.onClose = r;
    }
}
