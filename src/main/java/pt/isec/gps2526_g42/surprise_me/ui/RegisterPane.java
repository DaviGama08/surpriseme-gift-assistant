package pt.isec.gps2526_g42.surprise_me.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import pt.isec.gps2526_g42.surprise_me.application.SurpriseMeManager;
import pt.isec.gps2526_g42.surprise_me.ui.res.FontManager;
import pt.isec.gps2526_g42.surprise_me.ui.res.ImageManager;

import static pt.isec.gps2526_g42.surprise_me.ui.UIPropertyChangeManager.PROP_SHOW_DASHBOARD;
import static pt.isec.gps2526_g42.surprise_me.ui.UIPropertyChangeManager.PROP_SHOW_LOGIN;

public class RegisterPane extends HBox {
    private final SurpriseMeManager manager;

    // UI elements which are used to click or validate info
    private TextField nameField;
    private TextField emailField;
    private PasswordField passwordField;
    private CheckBox agreeCheck;
    private StyledButton createAccountButton;
    private Text signInText;
    private Text createAccountErrorText;

    public RegisterPane(SurpriseMeManager manager) {
        this.manager = manager;
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        Text TitleText = new Text("Create your account");
        TitleText.setFill(AppColors.BLACK.getColor());
        TitleText.setFont(FontManager.loadFont("ADLaMDisplay-Regular.ttf", 46));

        // Informative text
        Text infoText = new Text("It's just a few minutes and free!");
        infoText.setStyle("-fx-font-size: 24px; -fx-font-weight: 500;");

        // Box with title
        VBox titleBox = new VBox(TitleText, infoText);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setSpacing(20);
        titleBox.setPadding(new Insets(0, 0, 34, 0));

        // Inputs from user
        nameField = new TextField();
        nameField.setPromptText("Name");
        nameField.setPrefHeight(45);
        nameField.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: "
                + AppColors.LIGHT_GRAY.getHexValue() + "; -fx-font-size: 14px;");

        emailField = new TextField();
        emailField.setPromptText("webmail@gmail.com");
        emailField.setPrefHeight(45);
        emailField.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: "
                + AppColors.LIGHT_GRAY.getHexValue() + "; -fx-font-size: 14px;");

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefHeight(45);
        passwordField.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: "
                + AppColors.LIGHT_GRAY.getHexValue() + "; -fx-font-size: 14px;");

        // Create account button
        createAccountButton = new StyledButton(AppColors.BLUE, "CREATE ACCOUNT", AppColors.WHITE, null, true);
        createAccountButton.setPrefHeight(45);
        createAccountButton.setMaxWidth(Double.MAX_VALUE);

        HBox termsBox = new HBox();
        termsBox.setAlignment(Pos.CENTER_LEFT);
        termsBox.setSpacing(10);

        agreeCheck = new CheckBox();
        Text termsText = new Text("I confirm I want to create this local account");
        termsText.setStyle("-fx-font-size: 14px; -fx-font-weight: 500;");

        termsBox.getChildren().addAll(agreeCheck, termsText);

        // Error message
        createAccountErrorText = new Text();
        createAccountErrorText.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        createAccountErrorText.setFill(AppColors.RED.getColor());

        VBox middleBox = new VBox(nameField, emailField, passwordField, createAccountButton, termsBox, createAccountErrorText);
        middleBox.setSpacing(10);
        middleBox.setPadding(new Insets(50, 10, 10, 10));
        VBox.setVgrow(middleBox, Priority.ALWAYS);

        // Sign in
        Text bottomText = new Text("Already a member? ");
        bottomText.setStyle("-fx-font-size: 16px; -fx-font-weight: 500;");
        signInText = new Text("Sign in");
        signInText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        signInText.setOnMouseEntered(e -> signInText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-underline: true; -fx-cursor: hand;"));
        signInText.setOnMouseExited(e -> signInText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-underline: false;"));
        HBox signUpBox = new HBox(bottomText, signInText);
        signUpBox.setAlignment(Pos.CENTER);

        // Image on the right
        Image image = ImageManager.getImage("logo.png");
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(this.widthProperty().multiply(0.5));

        // Box on the left, with everything except the image
        VBox leftSide = new VBox(titleBox, middleBox, signUpBox);
        leftSide.setPadding(new Insets(60, 150, 60, 150));
        leftSide.setAlignment(Pos.CENTER_LEFT);
        leftSide.setSpacing(20);

        HBox.setHgrow(leftSide, Priority.ALWAYS);
        this.getChildren().addAll(leftSide, imageView);
    }

    private void registerHandlers() {
        nameField.textProperty().addListener((obs, oldV, newV) -> updateLoginButtonState());

        emailField.textProperty().addListener((obs, oldV, newV) -> updateLoginButtonState());

        passwordField.textProperty().addListener((obs, oldV, newV) -> updateLoginButtonState());

        agreeCheck.selectedProperty().addListener((obs, oldV, newV) -> updateLoginButtonState());

        signInText.setOnMouseClicked(e -> UIPropertyChangeManager.getInstance().firePropertyChange(PROP_SHOW_LOGIN, null, null));

        createAccountButton.setOnAction(e -> {
            if (!emailField.getText().trim().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
                    && passwordField.getText().trim().length() < 6) {
                createAccountErrorText.setText("Please enter a valid email. \nThe password must be at least 6 characters long.");
                return;
            }
            if (passwordField.getText().trim().length() < 6) {
                createAccountErrorText.setText("The password must be at least 6 characters long.");
                return;
            }
            if (!emailField.getText().trim().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                createAccountErrorText.setText("Please enter a valid email.");
                return;
            }
            if (manager.register(nameField.getText().trim(), emailField.getText().trim(), passwordField.getText())) {
                UIPropertyChangeManager.getInstance().firePropertyChange(PROP_SHOW_DASHBOARD, null, null);
            } else {
                createAccountErrorText.setText("Email already in use. Try again.");
            }
        });
    }

    private void update() {
        updateLoginButtonState();
    }

    private void updateLoginButtonState() {
        boolean nameOK = !nameField.getText().trim().isEmpty();
        boolean emailOK = !emailField.getText().trim().isEmpty();
        boolean passOK = !passwordField.getText().trim().isEmpty();
        boolean termsOK = agreeCheck.isSelected();

        createAccountButton.setDisable(!(nameOK && emailOK && passOK && termsOK));
    }

}
