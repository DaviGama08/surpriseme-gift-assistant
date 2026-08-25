package pt.isec.gps2526_g42.surprise_me.ui.gifts;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import pt.isec.gps2526_g42.surprise_me.application.SurpriseMeManager;
import pt.isec.gps2526_g42.surprise_me.model.data.Gift;
import pt.isec.gps2526_g42.surprise_me.ui.AppColors;
import pt.isec.gps2526_g42.surprise_me.ui.res.FontManager;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class GiftsPane extends VBox {
    // data components
    private SurpriseMeManager manager;
    private ArrayList<Gift> allGifts;
    private ObservableList<Gift> observableGifts;
    private final int selectedEnjoyer;

    // UI components
    private TableView<Gift> tableGifts;
    private TextField searchField;

    public GiftsPane(SurpriseMeManager manager) {
        this.manager = manager;
        this.selectedEnjoyer = -1;
        this.allGifts = manager.getGifts();
        this.observableGifts = FXCollections.observableList(this.allGifts);
        createViews();
        registerHandlers();
        update();
    }

    public GiftsPane(SurpriseMeManager manager, int enjoyerId) {
        this.manager = manager;
        this.selectedEnjoyer = enjoyerId;
        this.allGifts = manager.getGiftsForEnjoyer(selectedEnjoyer);
        this.observableGifts = FXCollections.observableList(this.allGifts);
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        VBox topBox = new VBox(); // box that includes the window title
        VBox bottomBox = new VBox(); // box that includes the window

        // Create title
        Text initialTitle = new Text("Gifts");
        initialTitle.setFill(AppColors.BLUE.getColor());
        initialTitle.setFont(FontManager.loadFont("ADLaMDisplay-Regular.ttf", 64));

        Text finalTitle = new Text("History");
        finalTitle.setFill(AppColors.PURPLE.getColor());
        finalTitle.setFont(FontManager.loadFont("ADLaMDisplay-Regular.ttf", 64));

        HBox titleBox = new HBox(initialTitle, finalTitle);
        titleBox.setAlignment(Pos.TOP_LEFT);
        titleBox.setSpacing(0);
        topBox.getChildren().addAll(titleBox);
        topBox.setAlignment(Pos.TOP_CENTER);
        topBox.setPadding(new Insets(20, 15, 0, 15));

        // Search bar
        searchField = new TextField();
        searchField.setPromptText("Search");
        searchField.setPrefWidth(320);
        HBox searchBox = new HBox(16, searchField);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(10, 0, 10, 0));

        // Table view for gifts
        tableGifts = new TableView<>();
        tableGifts.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<Gift, String> giftCol = new TableColumn<>("Gift");
        giftCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));

        TableColumn<Gift, String> occasionCol = new TableColumn<>("Occasion");
        occasionCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getOccasion()));

        TableColumn<Gift, String> enjoyerCol = new TableColumn<>("Enjoyer");
        enjoyerCol.setCellValueFactory(c -> {
            if (c.getValue().getEnjoyer() == null)
                return new SimpleStringProperty("-");
            return new SimpleStringProperty(c.getValue().getEnjoyer().getDetails().getName());
        });

        TableColumn<Gift, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));

        TableColumn<Gift, String> feedbackCol = new TableColumn<>("Feedback");
        feedbackCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFeedback()));

        tableGifts.getColumns().addAll(giftCol, occasionCol, enjoyerCol, statusCol, feedbackCol);
        tableGifts.setItems(observableGifts);

        // by default, the table is sorted by gift name
        tableGifts.getSortOrder().add(giftCol);
        giftCol.setSortType(TableColumn.SortType.ASCENDING);
        tableGifts.sort();

        // height of each entry in the table
        tableGifts.setFixedCellSize(40);

        // only the necessary lines appear
        tableGifts.prefHeightProperty().bind(tableGifts.fixedCellSizeProperty()
                .multiply(Bindings.size(observableGifts).add(1.01)));

        // formatting of the selected line
        tableGifts.setStyle("-fx-selection-bar: " + AppColors.PURPLE.getHexValue()
                + "; -fx-selection-bar-non-focused: " + AppColors.WHITE.getHexValue() + ";");

        bottomBox.getChildren().addAll(searchBox, tableGifts);
        bottomBox.setPadding(new Insets(20));
        VBox.setVgrow(tableGifts, Priority.ALWAYS);

        getChildren().addAll(topBox, bottomBox);
        setVgrow(bottomBox, Priority.ALWAYS);
    }

    private void registerHandlers() {
        tableGifts.setRowFactory(tv -> {
            var row = new TableRow<Gift>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    Gift selectedGift = row.getItem();
                    GiftDetailsModal gdm = new GiftDetailsModal(manager, selectedGift);
                    gdm.initModality(Modality.WINDOW_MODAL);
                    gdm.initOwner(getScene().getWindow());
                    gdm.showAndWait();
                    update();
                }
            });
            return row;
        });

        // Filters
        searchField.textProperty().addListener((obs, o, n) -> applyFilters());
    }

    public void update() {
        // Clear the selected table row
        tableGifts.getSelectionModel().clearSelection();

        // Get the updated gifts list
        if (selectedEnjoyer != -1) {
            this.allGifts = manager.getGiftsForEnjoyer(selectedEnjoyer);
        } else {
            this.allGifts = manager.getGifts();
        }

        applyFilters();
    }

    private void applyFilters() {
        String searchInput = searchField.getText().trim().toLowerCase();

        // Shows all gifts if input is empty
        if (searchInput.isEmpty()) {
            observableGifts.setAll(allGifts);
        } else {
            // Filter the Gifts to a new array list and set it as observable
            ArrayList<Gift> filteredList = (ArrayList<Gift>) allGifts.stream()
                    .filter(gift -> gift.getName().toLowerCase().contains(searchInput))
                    .collect(Collectors.toList());

            observableGifts.setAll(filteredList);
        }
        tableGifts.sort();
    }
}
