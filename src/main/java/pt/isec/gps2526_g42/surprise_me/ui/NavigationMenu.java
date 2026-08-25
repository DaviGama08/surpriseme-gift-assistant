package pt.isec.gps2526_g42.surprise_me.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import pt.isec.gps2526_g42.surprise_me.application.SurpriseMeManager;
import pt.isec.gps2526_g42.surprise_me.ui.dialogs.OkCancelModal;
import pt.isec.gps2526_g42.surprise_me.ui.res.FontManager;

import static pt.isec.gps2526_g42.surprise_me.ui.UIPropertyChangeManager.*;

// Class that creates and manages the navigation menu for the App
public class NavigationMenu extends VBox {
    private final SurpriseMeManager manager;
    private Button btnDashboard;
    private Button btnMyProfile;
    private Button btnGenerateGifts;
    private Button btnEnjoyers;
    private Button btnEvents;
    private Button btnGiftsHistory;
    private Button btnLogout;

    public NavigationMenu(SurpriseMeManager manager) {
        this.manager = manager;
        createViews();
        registerHandlers();
    }

    private void createViews() {
        // Styling
        VBox topBox = new VBox(); // box that includes the menu title
        VBox bottomBox = new VBox(); // box that includes the menu options

        // Styling of menu title
        topBox.setAlignment(Pos.TOP_CENTER);
        topBox.setPadding(new Insets(20, 15, 0, 15));

        // Styling of menu options
        bottomBox.setSpacing(15);
        bottomBox.setPadding(new Insets(20));
        bottomBox.setAlignment(Pos.CENTER);

        // Create title
        Text initialTitle = new Text("Surprise");
        initialTitle.setFill(AppColors.BLUE.getColor());
        initialTitle.setFont(FontManager.loadFont("ADLaMDisplay-Regular.ttf", 48));

        Text finalTitle = new Text("Me");
        finalTitle.setFill(AppColors.PURPLE.getColor());
        finalTitle.setFont(FontManager.loadFont("ADLaMDisplay-Regular.ttf", 48));

        HBox titleBox = new HBox(initialTitle, finalTitle);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setSpacing(0);
        topBox.getChildren().addAll(titleBox);

        // Create clickable icon buttons
        btnDashboard = new StyledButton(AppColors.DARK_GREY, "Dashboard", AppColors.LIGHT_GRAY, "house-solid-full.svg", false);
        btnMyProfile = new StyledButton(AppColors.DARK_GREY, "My profile", AppColors.LIGHT_GRAY, "user-solid-full.svg", false);
        btnGenerateGifts = new StyledButton(AppColors.DARK_GREY, "Generate gifts", AppColors.LIGHT_GRAY, "gift-solid-full.svg", false);
        btnEnjoyers = new StyledButton(AppColors.DARK_GREY, "Enjoyers", AppColors.LIGHT_GRAY, "user-group-solid-full.svg", false);
        btnEvents = new StyledButton(AppColors.DARK_GREY, "Events", AppColors.LIGHT_GRAY, "calendar-days-solid-full.svg", false);
        btnGiftsHistory = new StyledButton(AppColors.DARK_GREY, "Gifts history", AppColors.LIGHT_GRAY, "clock-rotate-left-solid-full.svg", false);
        btnLogout = new StyledButton(AppColors.DARK_GREY, "Logout", AppColors.LIGHT_GRAY, "right-from-bracket-solid-full.svg", false);

        // Add all buttons to the bottomBox
        bottomBox.getChildren().addAll(btnDashboard, btnMyProfile, btnGenerateGifts, btnEnjoyers, btnEvents, btnGiftsHistory, btnLogout);

        // Styling of menu
        setStyle("-fx-background-color:" + AppColors.DARK_GREY.getHexValue());
        getChildren().addAll(topBox, bottomBox);
        setVgrow(bottomBox, Priority.ALWAYS);
    }

    private void registerHandlers() {
        btnDashboard.setOnAction(e -> UIPropertyChangeManager.getInstance().firePropertyChange(PROP_SHOW_DASHBOARD, null, true));

        btnMyProfile.setOnAction(e -> UIPropertyChangeManager.getInstance().firePropertyChange(PROP_SHOW_PROFILE, null, true));

        btnGenerateGifts.setOnAction(e -> UIPropertyChangeManager.getInstance().firePropertyChange(PROP_SHOW_GENERATE_GIFTS, null, true));

        btnEnjoyers.setOnAction(e -> UIPropertyChangeManager.getInstance().firePropertyChange(PROP_SHOW_ENJOYERS, null, true));

        btnEvents.setOnAction(e -> UIPropertyChangeManager.getInstance().firePropertyChange(PROP_SHOW_EVENTS, null, null));

        btnGiftsHistory.setOnAction(e -> UIPropertyChangeManager.getInstance().firePropertyChange(PROP_SHOW_GIFTS, null, null));

        btnLogout.setOnAction(e -> {
            Stage stage = (Stage) this.getScene().getWindow();

            if (OkCancelModal.show(stage, "Logout", "Are you sure you want to logout?")) {
                manager.logout();
                UIPropertyChangeManager.getInstance().firePropertyChange(PROP_SHOW_LOGIN, null, null);
            }
        });
    }
}
