package pt.isec.gps2526_g42.surprise_me.ui.suggestions.modals;

import javafx.animation.RotateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import pt.isec.gps2526_g42.surprise_me.ui.AppColors;

public class LoadingModal extends Stage {
    private RotateTransition rt;

    public LoadingModal() {
        createViews();
        registerHandlers();
    }

    private void createViews() {
        HBox header = new HBox();
        header.setStyle(
                "-fx-background-color: " + AppColors.BLUE.getHexValue() +
                        "; -fx-padding: 10 15; -fx-alignment: CENTER_LEFT; -fx-background-radius: 10 10 0 0;"
        );

        Label title = new Label("Generating gift ideas");
        title.setStyle(
                "-fx-text-fill: " + AppColors.WHITE.getHexValue() +
                        "; -fx-font-size: 16; -fx-font-weight: bold;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(title, spacer);

        Arc arc = new Arc();
        arc.setCenterX(40);
        arc.setCenterY(40);
        arc.setRadiusX(30);
        arc.setRadiusY(30);
        arc.setStartAngle(45);
        arc.setLength(270);
        arc.setType(ArcType.OPEN);
        arc.setFill(null);
        arc.setStroke(AppColors.PURPLE.getColor());
        arc.setStrokeWidth(4);

        rt = new RotateTransition(Duration.seconds(1.2), arc);
        rt.setByAngle(360);
        rt.setCycleCount(RotateTransition.INDEFINITE);

        VBox spinnerBox = new VBox(arc);
        spinnerBox.setAlignment(Pos.CENTER);
        spinnerBox.setPrefHeight(100);

        Label msg = new Label("Please wait while we find the perfect gifts...");
        msg.setStyle("-fx-font-size: 14px; -fx-text-fill: " + AppColors.LIGHT_GRAY.getHexValue() + ";");
        msg.setWrapText(true);
        msg.setMaxWidth(320);
        msg.setAlignment(Pos.CENTER);

        VBox content = new VBox(16, spinnerBox, msg);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(24, 20, 24, 20));
        content.setStyle("-fx-background-color: white; -fx-background-radius: 0 0 10 10;");

        VBox frame = new VBox(header, content);
        frame.setStyle(
                "-fx-background-color: white;" +
                        " -fx-border-color: " + AppColors.LIGHT_GRAY.getHexValue() + ";" +
                        " -fx-border-width: 1.5;" +
                        " -fx-border-radius: 10;" +
                        " -fx-background-radius: 10;"
        );

        Rectangle clip = new Rectangle();
        clip.setArcWidth(10);
        clip.setArcHeight(10);
        clip.widthProperty().bind(frame.widthProperty());
        clip.heightProperty().bind(frame.heightProperty());
        frame.setClip(clip);

        Scene scene = new Scene(frame, 420, 260);
        scene.setFill(Color.TRANSPARENT);
        setScene(scene);

        initStyle(StageStyle.TRANSPARENT);
        initModality(Modality.WINDOW_MODAL);
        setResizable(false);
        sizeToScene();
        centerOnScreen();
    }

    private void registerHandlers() {
        final double[] offset = new double[2];
        var root = getScene().getRoot();

        root.setOnMousePressed(e -> {
            offset[0] = e.getSceneX();
            offset[1] = e.getSceneY();
        });
        root.setOnMouseDragged(e -> {
            this.setX(e.getScreenX() - offset[0]);
            this.setY(e.getScreenY() - offset[1]);
        });
    }

    public void showLoading() {
        if (rt != null) {
            rt.play();
        }
        show();
    }

    public void hideLoading() {
        if (rt != null) {
            rt.stop();
        }
        close();
    }
}
