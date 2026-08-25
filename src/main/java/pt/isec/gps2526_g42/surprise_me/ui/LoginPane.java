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
import static pt.isec.gps2526_g42.surprise_me.ui.UIPropertyChangeManager.PROP_SHOW_REGISTER;

public class LoginPane extends HBox {
    private final SurpriseMeManager manager;

    // UI elements which are used to click or validate info
    private TextField emailField;
    private PasswordField passwordField;
    private CheckBox agreeCheck;
    private StyledButton logInButton;
    private Text signUpText;
    private Text loginErrorText;

    public LoginPane(SurpriseMeManager manager) {
        this.manager = manager;
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        Text initialTitleText = new Text("Welcome to");
        initialTitleText.setFill(AppColors.BLACK.getColor());
        initialTitleText.setFont(FontManager.loadFont("ADLaMDisplay-Regular.ttf", 64));

        Text surpriseText = new Text("Surprise");
        surpriseText.setFill(AppColors.BLUE.getColor());
        surpriseText.setFont(FontManager.loadFont("ADLaMDisplay-Regular.ttf", 64));

        Text meText = new Text("Me");
        meText.setFill(AppColors.PURPLE.getColor());
        meText.setFont(FontManager.loadFont("ADLaMDisplay-Regular.ttf", 64));

        Text exclamationText = new Text("!");
        exclamationText.setFill(AppColors.BLACK.getColor());
        exclamationText.setFont(FontManager.loadFont("ADLaMDisplay-Regular.ttf", 64));

        HBox brandBox = new HBox(surpriseText, meText, exclamationText);
        brandBox.setAlignment(Pos.CENTER);

        // Box with whole title
        VBox titleBox = new VBox(initialTitleText, brandBox);
        titleBox.setAlignment(Pos.CENTER);

        // Informative text
        Text signInText = new Text("Sign in to continue");
        signInText.setStyle("-fx-font-size: 18px; -fx-font-weight: 500;");

        // Inputs from user
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

        // Login button
        logInButton = new StyledButton(AppColors.BLUE, "LOG IN", AppColors.WHITE, null, true);
        logInButton.setPrefHeight(45);
        logInButton.setMaxWidth(Double.MAX_VALUE);

        // Terms box
        HBox termsBox = new HBox();
        termsBox.setAlignment(Pos.CENTER_LEFT);
        termsBox.setSpacing(10);

        agreeCheck = new CheckBox();
        agreeCheck.setSelected(true);
        Text termsText = new Text("I agree with terms and conditions");
        termsText.setStyle("-fx-font-size: 14px; -fx-font-weight: 500;");

        termsBox.getChildren().addAll(agreeCheck, termsText);

        // Error message
        loginErrorText = new Text();
        loginErrorText.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        loginErrorText.setFill(AppColors.RED.getColor());

        VBox middleBox = new VBox(signInText, emailField, passwordField, logInButton, termsBox, loginErrorText);
        middleBox.setSpacing(10);
        middleBox.setPadding(new Insets(50, 10, 10, 10));
        VBox.setVgrow(middleBox, Priority.ALWAYS);

        // Sign up
        Text bottomText = new Text("Don’t have an account? ");
        bottomText.setStyle("-fx-font-size: 16px; -fx-font-weight: 500;");
        signUpText = new Text("Sign up");
        signUpText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        signUpText.setOnMouseEntered(e -> signUpText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-underline: true; -fx-cursor: hand;"));
        signUpText.setOnMouseExited(e -> signUpText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-underline: false;"));
        HBox signUpBox = new HBox(bottomText, signUpText);
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
        emailField.textProperty().addListener((obs, oldV, newV) -> updateLoginButtonState());

        passwordField.textProperty().addListener((obs, oldV, newV) -> updateLoginButtonState());

        agreeCheck.selectedProperty().addListener((obs, oldV, newV) -> updateLoginButtonState());

        signUpText.setOnMouseClicked(e -> UIPropertyChangeManager.getInstance().firePropertyChange(PROP_SHOW_REGISTER, null, null));

        logInButton.setOnAction(e -> {
            if(manager.login(emailField.getText().trim(), passwordField.getText())) {
                UIPropertyChangeManager.getInstance().firePropertyChange(PROP_SHOW_DASHBOARD, null, null);
            } else {
                loginErrorText.setText("Wrong email or password. Try again.");
            }
        });
    }

    private void update() {
        updateLoginButtonState();
    }

    private void updateLoginButtonState() {
        boolean emailOK = !emailField.getText().trim().isEmpty();
        boolean passOK = !passwordField.getText().trim().isEmpty();
        boolean termsOK = agreeCheck.isSelected();

        logInButton.setDisable(!(emailOK && passOK && termsOK));
    }

}
