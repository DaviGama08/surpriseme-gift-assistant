package pt.isec.gps2526_g42.surprise_me.ui;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import pt.isec.gps2526_g42.surprise_me.model.data.SurpriseMeManager;
import pt.isec.gps2526_g42.surprise_me.model.data.SurpriseMeSerialization;
import pt.isec.gps2526_g42.surprise_me.model.data.UserDetails;
import pt.isec.gps2526_g42.surprise_me.ui.dialogsViews.ChoiceModal;
import pt.isec.gps2526_g42.surprise_me.ui.dialogsViews.MessageModal;
import pt.isec.gps2526_g42.surprise_me.ui.res.FontManager;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ProfilePane extends VBox {
    private final SurpriseMeManager manager;
    private UserDetails userDetails;
    private UserDetails originalDetails;    // To compare changes
    private boolean isUpdating;             // Flag to avoid listeners during update

    // UI components
    private StackPane avatarStack;
    private Circle avatarCircle;
    private Label avatarInitials;
    private TextField tfName;
    private TextField tfCountry;
    private TextField tfCity;
    private TextField tfEmail;
    private DatePicker dpBirth;
    private Label lbBirthError; // Error message for invalid date format
    private StyledButton btnSave;
    private File selectedAvatarFile;
    private boolean removeAvatarRequested;

    public ProfilePane(SurpriseMeManager manager) {
        this.manager = manager;
        selectedAvatarFile = null;
        removeAvatarRequested = false;
        isUpdating = false;
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        // Create title
        Text initialTitle = new Text("My");
        initialTitle.setFill(AppColors.BLUE.getColor());
        initialTitle.setFont(FontManager.loadFont("ADLaMDisplay-Regular.ttf", 64));

        Text finalTitle = new Text("Profile");
        finalTitle.setFill(AppColors.PURPLE.getColor());
        finalTitle.setFont(FontManager.loadFont("ADLaMDisplay-Regular.ttf", 64));

        HBox titleBox = new HBox(initialTitle, finalTitle);
        titleBox.setAlignment(Pos.TOP_LEFT);
        titleBox.setSpacing(0);

        VBox topBox = new VBox();
        topBox.getChildren().addAll(titleBox);
        topBox.setAlignment(Pos.TOP_CENTER);
        topBox.setPadding(new Insets(20, 40, 10, 40));

        // Avatar: circle + initials or image pattern
        avatarCircle = new Circle(100);
        avatarCircle.setFill(Paint.valueOf(AppColors.PURPLE.getHexValue()));
        avatarCircle.setStyle("-fx-cursor: hand");

        avatarInitials = new Label("?");
        avatarInitials.setTextFill(AppColors.WHITE.getColor());
        avatarInitials.setFont(Font.font(36));

        avatarStack = new StackPane(avatarCircle, avatarInitials);
        avatarStack.setPrefSize(180, 180);

        VBox avatarBox = new VBox(8, avatarStack);
        avatarBox.setAlignment(Pos.CENTER);
        avatarBox.setPadding(new Insets(10));

        // Form fields
        tfName = new TextField();
        tfName.setPromptText("Full name (required)");

        tfCountry = new TextField();
        tfCountry.setPromptText("Country");

        tfCity = new TextField();
        tfCity.setPromptText("City");

        tfEmail = new TextField();
        tfEmail.setPromptText("Email (required)");

        dpBirth = new DatePicker();
        dpBirth.setEditable(true);

        // Configure date format to dd/MM/yyyy
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        dpBirth.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                return (date != null) ? dateFormatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.trim().isEmpty()) {
                    try {
                        return LocalDate.parse(string, dateFormatter);
                    } catch (DateTimeParseException e) {
                        // Invalid format, return null
                        return null;
                    }
                }
                return null;
            }
        });

        // Error label for date validation
        lbBirthError = new Label("");
        lbBirthError.setStyle("-fx-text-fill: " + AppColors.RED.getHexValue() + "; -fx-font-size: 11px;");
        lbBirthError.setVisible(false);
        lbBirthError.setManaged(false); // Don't take space when hidden

        // Two-column GridPane com margens laterais
        GridPane form = new GridPane();
        form.setVgap(20);
        form.setHgap(20);
        form.setPadding(new Insets(20, 80, 20, 80));
        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(50);
        form.getColumnConstraints().addAll(col, col);

        // Row 0: Name | Country
        form.add(new Label("Name (required)"), 0, 0);
        form.add(tfName, 0, 1);
        form.add(new Label("Country"), 1, 0);
        form.add(tfCountry, 1, 1);
        GridPane.setHgrow(tfName, Priority.ALWAYS);
        GridPane.setHgrow(tfCountry, Priority.ALWAYS);

        // Row 1: Email | City
        form.add(new Label("Email (required)"), 0, 2);
        form.add(tfEmail, 0, 3);
        form.add(new Label("City"), 1, 2);
        form.add(tfCity, 1, 3);
        GridPane.setHgrow(tfCity, Priority.ALWAYS);
        GridPane.setHgrow(tfEmail, Priority.ALWAYS);

        // Row 2: Date of birth spans both columns
        VBox birthBox = new VBox(4, dpBirth, lbBirthError);
        form.add(new Label("Date of birth"), 0, 4);
        form.add(birthBox, 0, 5, 2, 1);

        // Save button with disk icon on the right
        btnSave = new StyledButton(AppColors.GREEN, "Save preferences", AppColors.WHITE, "floppy-disk-solid-full.svg", true);
        btnSave.setDisable(true); // Disabled until valid changes
        HBox saveBox = new HBox(btnSave);
        saveBox.setAlignment(Pos.CENTER_RIGHT);
        saveBox.setPadding(new Insets(20, 80, 20, 80));

        VBox bottomBox = new VBox(10, avatarBox, form, saveBox);
        bottomBox.setAlignment(Pos.TOP_CENTER);

        this.getChildren().addAll(topBox, bottomBox);
    }

    //when user avatar is clicked
    private void registerHandlers() {
        avatarStack.setOnMouseClicked(e -> {
            Stage owner = (Stage) this.getScene().getWindow();
            // Use styled ChoiceModal (app style) with three options
            int result = ChoiceModal.show(owner, "Avatar", "Choose an action for the profile image", "Load new image", "Remove image", "Cancel");
            if (result == 0) {
                return;
            }
            if (result == 1) {
                FileChooser fc = new FileChooser();
                fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
                File file = fc.showOpenDialog(owner);
                if (file != null) {
                    selectedAvatarFile = file;
                    removeAvatarRequested = false;
                    Image img = new Image(file.toURI().toString());
                    avatarCircle.setFill(new ImagePattern(img));
                    avatarInitials.setVisible(false);
                    // small scale pulse to indicate new image loaded
                    ScaleTransition st = new ScaleTransition(Duration.millis(220), avatarStack);
                    st.setFromX(1.0);
                    st.setFromY(1.0);
                    st.setToX(1.06);
                    st.setToY(1.06);
                    st.setAutoReverse(true);
                    st.setCycleCount(2);
                    st.play();
                    checkForChanges();
                }
            } else if (result == 2) {
                // remove avatar
                selectedAvatarFile = null;
                removeAvatarRequested = true;
                // fade avatar out then show initials
                FadeTransition ft = new FadeTransition(Duration.millis(180), avatarStack);
                ft.setFromValue(1.0);
                ft.setToValue(0.2);
                ft.setOnFinished(ev -> {
                    avatarCircle.setFill(Paint.valueOf(AppColors.PURPLE.getHexValue()));
                    avatarInitials.setVisible(true);
                    // fade back in
                    FadeTransition fin = new FadeTransition(Duration.millis(180), avatarStack);
                    fin.setFromValue(0.2);
                    fin.setToValue(1.0);
                    fin.play();
                });
                ft.play();
                checkForChanges();
            }
        });

        tfName.textProperty().addListener((obs, o, n) -> {
            if (!isUpdating) checkForChanges();
        });

        tfEmail.textProperty().addListener((obs, o, n) -> {
            if (!isUpdating) checkForChanges();
        });

        tfCountry.textProperty().addListener((obs, o, n) -> {
            if (!isUpdating) checkForChanges();
        });

        tfCity.textProperty().addListener((obs, o, n) -> {
            if (!isUpdating) checkForChanges();
        });

        dpBirth.valueProperty().addListener((obs, o, n) -> {
            if (!isUpdating) {
                lbBirthError.setVisible(false);
                lbBirthError.setManaged(false);
                checkForChanges();
            }
        });

        // Add text field listener to validate manual input
        dpBirth.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (isUpdating) return;

            if (newText == null || newText.trim().isEmpty()) {
                // Empty is valid (optional field)
                lbBirthError.setVisible(false);
                lbBirthError.setManaged(false);
                dpBirth.setStyle("");
                checkForChanges();
                return;
            }

            // Check if input matches date format dd/MM/yyyy
            boolean validFormat = newText.matches("^\\d{2}/\\d{2}/\\d{4}$");

            if (!validFormat && !newText.isEmpty()) {
                // Show error message
                lbBirthError.setText("Please use the date picker or format dd/MM/yyyy");
                lbBirthError.setVisible(true);
                lbBirthError.setManaged(true);
                dpBirth.setStyle("-fx-border-color: " + AppColors.RED.getHexValue() + "; -fx-border-width: 2;");
            } else {
                // Valid format or being typed
                lbBirthError.setVisible(false);
                lbBirthError.setManaged(false);
                dpBirth.setStyle("");
            }

            checkForChanges();
        });

        btnSave.setOnAction(e -> {
            if (!isFormValid()) {
                String errorMsg = buildValidationErrorMessage();
                new MessageModal((Stage) this.getScene().getWindow(), "Invalid data", errorMsg);
                highlightInvalidFields();
                return;
            }

            // Usar valores seguros evitando null
            String name = tfName.getText() != null ? tfName.getText().trim() : "";
            String email = tfEmail.getText() != null ? tfEmail.getText().trim() : "";
            String country = tfCountry.getText() != null ? tfCountry.getText().trim() : "";
            String city = tfCity.getText() != null ? tfCity.getText().trim() : "";

            userDetails.setName(name);
            userDetails.setEmail(email);
            userDetails.setCountry(country.isEmpty() ? null : country);
            userDetails.setCity(city.isEmpty() ? null : city);
            userDetails.setBirthDate(dpBirth.getValue());

            // Handle avatar changes
            if (removeAvatarRequested) {
                userDetails.setAvatarPath(null);
            } else if (selectedAvatarFile != null) {
                try {
                    // Copy avatar file to .surprise_me directory
                    Path surpriseMeDir = SurpriseMeSerialization.dataDirectory().resolve("avatars");

                    // Create avatars directory if it doesn't exist
                    if (!Files.exists(surpriseMeDir)) {
                        Files.createDirectories(surpriseMeDir);
                    }

                    // Generate unique filename to avoid conflicts
                    String originalName = selectedAvatarFile.getName();
                    String extension = "";
                    int dotIndex = originalName.lastIndexOf('.');
                    if (dotIndex > 0) {
                        extension = originalName.substring(dotIndex);
                    }
                    String uniqueFileName = "avatar_" + System.currentTimeMillis() + extension;

                    // Copy file to avatars directory
                    Path targetPath = surpriseMeDir.resolve(uniqueFileName);
                    Files.copy(selectedAvatarFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                    // Save relative path (avatars/filename)
                    userDetails.setAvatarPath("avatars/" + uniqueFileName);
                } catch (Exception ex) {
                    new MessageModal((Stage) this.getScene().getWindow(), "Error", "Failed to save avatar image: " + ex.getMessage());
                    return;
                }
            }

            if (manager.setUserDetails(userDetails)) {
                new MessageModal((Stage) this.getScene().getWindow(), "Saved", "Your preferences were saved.");
                // Update original state after successful save
                isUpdating = true;
                loadProfile();
                isUpdating = false;
                btnSave.setDisable(true);
            } else {
                new MessageModal((Stage) this.getScene().getWindow(), "Error", "Failed to save your preferences.");
            }
        });
    }

    private void update() {
        loadProfile();
    }

    private void loadProfile() {
        isUpdating = true;
        userDetails = manager != null ? manager.getUserDetails() : null;
        if (userDetails == null) {
            isUpdating = false;
            return;
        }

        // Saves the original state to compare using copy constructor
        originalDetails = new UserDetails(userDetails);

        tfName.setText(userDetails.getName() != null ? userDetails.getName() : "");
        tfCountry.setText(userDetails.getCountry() != null ? userDetails.getCountry() : "");
        tfCity.setText(userDetails.getCity() != null ? userDetails.getCity() : "");
        tfEmail.setText(userDetails.getEmail() != null ? userDetails.getEmail() : "");
        if (userDetails.getBirthDate() != null) {
            dpBirth.setValue(userDetails.getBirthDate());
        } else {
            dpBirth.setValue(null);
        }

        if (userDetails.getAvatarPath() != null && !userDetails.getAvatarPath().isEmpty()) {
            try {
                Path path = SurpriseMeSerialization.resolveAvatarPath(userDetails.getAvatarPath());
                if (path == null) {
                    throw new IllegalArgumentException("Invalid avatar path");
                }
                Image img = new Image(path.toUri().toString());
                // Fill circle with image pattern so it crops and centers
                avatarCircle.setFill(new ImagePattern(img));
                avatarInitials.setVisible(false);
                selectedAvatarFile = null; // existing avatar loaded from disk
                removeAvatarRequested = false;
            } catch (Exception ex) {
                // In fail, shows name Initials
                avatarInitials.setText(getInitials(userDetails.getName()));
                avatarInitials.setVisible(true);
                avatarCircle.setFill(Paint.valueOf(AppColors.PURPLE.getHexValue()));
            }
        } else {
            avatarInitials.setText(getInitials(userDetails.getName()));
            avatarInitials.setVisible(true);
            avatarCircle.setFill(Paint.valueOf(AppColors.PURPLE.getHexValue()));
            selectedAvatarFile = null;
            removeAvatarRequested = false;
        }
        isUpdating = false;
    }

    private boolean isFormValid() {
        String name = tfName.getText();
        String email = tfEmail.getText();

        // Name is mandatory
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        // Email is mandatory and must have a valid format
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        // Validates email format
        if (!email.trim().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            return false;
        }

        // Validates name: must have at least 2 letters and not only space
        if (name.trim().length() < 2) {
            return false;
        }

        // Validate date format if text is entered manually
        String dateText = dpBirth.getEditor().getText();
        if (dateText != null && !dateText.trim().isEmpty()) {
            // Check if format is correct dd/MM/yyyy
            if (!dateText.matches("^\\d{2}/\\d{2}/\\d{4}$")) {
                return false;
            }

            // Check if date can be parsed
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate date = LocalDate.parse(dateText, formatter);

                // Date of birth: if filled, must be in the past
                if (date.isAfter(LocalDate.now())) {
                    return false;
                }
            } catch (DateTimeParseException e) {
                return false;
            }
        }

        // Also check dpBirth value for dates selected from picker
        if (dpBirth.getValue() != null && dpBirth.getValue().isAfter(LocalDate.now())) {
            return false;
        }

        return true;
    }

    private boolean hasChanges() {
        if (originalDetails == null) {
            return false;
        }

        String currentName = tfName.getText() != null ? tfName.getText().trim() : "";
        String currentEmail = tfEmail.getText() != null ? tfEmail.getText().trim() : "";
        String currentCountry = tfCountry.getText() != null ? tfCountry.getText().trim() : "";
        String currentCity = tfCity.getText() != null ? tfCity.getText().trim() : "";

        String originalName = originalDetails.getName() != null ? originalDetails.getName() : "";
        String originalEmail = originalDetails.getEmail() != null ? originalDetails.getEmail() : "";
        String originalCountry = originalDetails.getCountry() != null ? originalDetails.getCountry() : "";
        String originalCity = originalDetails.getCity() != null ? originalDetails.getCity() : "";

        boolean nameChanged = !currentName.equals(originalName);
        boolean emailChanged = !currentEmail.equals(originalEmail);
        boolean countryChanged = !currentCountry.equals(originalCountry);
        boolean cityChanged = !currentCity.equals(originalCity);

        boolean birthChanged = false;
        if (dpBirth.getValue() != null && originalDetails.getBirthDate() != null) {
            birthChanged = !dpBirth.getValue().equals(originalDetails.getBirthDate());
        } else if (dpBirth.getValue() != null || originalDetails.getBirthDate() != null) {
            birthChanged = true;
        }

        boolean avatarChanged = selectedAvatarFile != null || removeAvatarRequested;

        return nameChanged || emailChanged || countryChanged || cityChanged || birthChanged || avatarChanged;
    }

    private void checkForChanges() {
        // clear error messages and red contours after field edited
        clearErrorStyles();

        boolean valid = isFormValid();
        boolean changed = hasChanges();
        btnSave.setDisable(!valid || !changed);

        // show visual feedback if not validated
        if (!valid && changed) {
            highlightInvalidFields();
        }
    }

    private void clearErrorStyles() {
        tfName.setStyle("");
        tfEmail.setStyle("");
        dpBirth.setStyle("");
        lbBirthError.setVisible(false);
        lbBirthError.setManaged(false);
    }

    private void highlightInvalidFields() {
        String errorStyle = "-fx-border-color: " + AppColors.RED.getHexValue() + "; -fx-border-width: 2;";

        String name = tfName.getText();
        if (name == null || name.trim().length() < 2) {
            tfName.setStyle(errorStyle);
        }

        String email = tfEmail.getText();
        if (email == null || email.trim().isEmpty() ||
                !email.trim().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            tfEmail.setStyle(errorStyle);
        }

        if (dpBirth.getValue() != null && dpBirth.getValue().isAfter(java.time.LocalDate.now())) {
            dpBirth.setStyle(errorStyle);
        }
    }

    private String buildValidationErrorMessage() {
        StringBuilder errors = new StringBuilder();

        String name = tfName.getText();
        if (name == null || name.trim().isEmpty()) {
            errors.append("• Name is required\n");
        } else if (name.trim().length() < 2) {
            errors.append("• Name must have at least 2 characters\n");
        }

        String email = tfEmail.getText();
        if (email == null || email.trim().isEmpty()) {
            errors.append("• Email is required\n");
        } else if (!email.trim().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            errors.append("• Email format is invalid\n");
        }

        // Validate date format
        String dateText = dpBirth.getEditor().getText();
        if (dateText != null && !dateText.trim().isEmpty()) {
            if (!dateText.matches("^\\d{2}/\\d{2}/\\d{4}$")) {
                errors.append("• Date must be in format dd/MM/yyyy\n");
            } else {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate date = LocalDate.parse(dateText, formatter);
                    if (date.isAfter(LocalDate.now())) {
                        errors.append("• Birth date cannot be in the future\n");
                    }
                } catch (DateTimeParseException e) {
                    errors.append("• Invalid date\n");
                }
            }
        }

        if (dpBirth.getValue() != null && dpBirth.getValue().isAfter(LocalDate.now())) {
            errors.append("• Birth date cannot be in the future\n");
        }

        return !errors.isEmpty() ? errors.toString() : "Please check the form fields.";
    }

    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "?";
        }
        String[] p = name.trim().split("\\s+");
        String a = p[0].substring(0, 1);
        String b = p.length > 1 ? p[p.length - 1].substring(0, 1) : "";
        return (a + b).toUpperCase();
    }
}
