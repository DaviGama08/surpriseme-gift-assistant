package pt.isec.gps2526_g42.surprise_me.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;
import pt.isec.gps2526_g42.surprise_me.application.SurpriseMeManager;
import pt.isec.gps2526_g42.surprise_me.ui.dialogs.OkCancelModal;
import pt.isec.gps2526_g42.surprise_me.ui.res.ImageManager;

public class SurpriseMeMainJFX extends Application {
    private SurpriseMeManager manager;

    public SurpriseMeMainJFX() {
        manager = new SurpriseMeManager();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        manager = new SurpriseMeManager();
        createAppStage(primaryStage);
        primaryStage.show();
    }

    private void createAppStage(Stage stage) {
        RootPane root = new RootPane(manager);
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("SurpriseMe");
        stage.getIcons().add(ImageManager.getImage("logo.png"));

        // screen dimensions
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double appWidth = screenBounds.getWidth();
        double appHeight = screenBounds.getHeight();
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        stage.setWidth(appWidth);
        stage.setHeight(appHeight);
        stage.setResizable(false);

        stage.setOnCloseRequest(event -> {
            event.consume();
            if (OkCancelModal.show(stage, "Close application", "Are you sure you want to quit?")) {
                // ensure persistence
                manager.logout();
                Platform.exit();
            }
        });
        stage.show();
    }

}
