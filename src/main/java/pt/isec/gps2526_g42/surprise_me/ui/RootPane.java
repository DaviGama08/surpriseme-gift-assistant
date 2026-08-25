package pt.isec.gps2526_g42.surprise_me.ui;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import pt.isec.gps2526_g42.surprise_me.application.SurpriseMeManager;
import pt.isec.gps2526_g42.surprise_me.ui.events.EventsPane;
import pt.isec.gps2526_g42.surprise_me.ui.gifts.GiftsPane;
import pt.isec.gps2526_g42.surprise_me.ui.enjoyers.EnjoyersPane;
import pt.isec.gps2526_g42.surprise_me.ui.suggestions.components.GenerateGiftsPane;

import static pt.isec.gps2526_g42.surprise_me.ui.UIPropertyChangeManager.PROP_SHOW_ENJOYERS;
import static pt.isec.gps2526_g42.surprise_me.ui.UIPropertyChangeManager.PROP_SHOW_GENERATE_GIFTS;
import static pt.isec.gps2526_g42.surprise_me.ui.UIPropertyChangeManager.PROP_SHOW_GIFTS;
import static pt.isec.gps2526_g42.surprise_me.ui.UIPropertyChangeManager.PROP_SHOW_EVENTS;
import static pt.isec.gps2526_g42.surprise_me.ui.UIPropertyChangeManager.PROP_SHOW_DASHBOARD;
import static pt.isec.gps2526_g42.surprise_me.ui.UIPropertyChangeManager.PROP_SHOW_PROFILE;
import static pt.isec.gps2526_g42.surprise_me.ui.UIPropertyChangeManager.PROP_SHOW_LOGIN;
import static pt.isec.gps2526_g42.surprise_me.ui.UIPropertyChangeManager.PROP_SHOW_REGISTER;

public class RootPane extends BorderPane {
    private final SurpriseMeManager manager;
    private VBox navigation;

    // views that will appear on the right side of the screen
    private EnjoyersPane enjoyersPane;
    private GenerateGiftsPane generateGiftsPane;
    private GiftsPane giftsPane;
    private EventsPane eventsPane;
    private DashboardPane dashboardPane;
    private ProfilePane profilePane;
    private LoginPane loginPane;
    private RegisterPane registerPane;

    public RootPane(SurpriseMeManager manager) {
        this.manager = manager;
        createViews();
        registerHandlers();
    }

    private void createViews() {
        loginPane = new LoginPane(manager);
        navigation = new NavigationMenu(manager);
        setCenter(loginPane);
    }

    private void registerHandlers() {
        UIPropertyChangeManager.getInstance().addPropertyChangeListener(
                PROP_SHOW_ENJOYERS, event -> {
                    setLeft(navigation);
                    enjoyersPane = new EnjoyersPane(manager);
                    setCenter(enjoyersPane);
                }
        );

        UIPropertyChangeManager.getInstance().addPropertyChangeListener(
                PROP_SHOW_GENERATE_GIFTS, event -> {
                    setLeft(navigation);
                    generateGiftsPane = new GenerateGiftsPane(manager);
                    setCenter(generateGiftsPane);
                }
        );

        UIPropertyChangeManager.getInstance().addPropertyChangeListener(
                PROP_SHOW_GIFTS, event -> {
                    // Checks if fire was from enjoyer
                    if (event.getNewValue() instanceof Integer) {
                        int enjoyerId = (int) event.getNewValue();
                        giftsPane = new GiftsPane(manager, enjoyerId);
                    }
                    else {
                        giftsPane = new GiftsPane(manager);
                    }
                    setLeft(navigation);
                    setCenter(giftsPane);
                }
        );

        UIPropertyChangeManager.getInstance().addPropertyChangeListener(
                PROP_SHOW_EVENTS, event -> {
                    setLeft(navigation);
                    eventsPane = new EventsPane(manager);
                    setCenter(eventsPane);
                }
        );

        UIPropertyChangeManager.getInstance().addPropertyChangeListener(
                PROP_SHOW_DASHBOARD, event -> {
                    setLeft(navigation);
                    dashboardPane = new DashboardPane(manager);
                    setCenter(dashboardPane);
                }
        );

        UIPropertyChangeManager.getInstance().addPropertyChangeListener(
                PROP_SHOW_PROFILE, event -> {
                    setLeft(navigation);
                    profilePane = new ProfilePane(manager);
                    setCenter(profilePane);
                }
        );

        UIPropertyChangeManager.getInstance().addPropertyChangeListener(
                PROP_SHOW_LOGIN, event -> {
                    loginPane = new LoginPane(manager);
                    setLeft(null);
                    setCenter(loginPane);
                }
        );

        UIPropertyChangeManager.getInstance().addPropertyChangeListener(
                PROP_SHOW_REGISTER, event -> {
                    registerPane = new RegisterPane(manager);
                    setLeft(null);
                    setCenter(registerPane);
                }
        );
    }
}
