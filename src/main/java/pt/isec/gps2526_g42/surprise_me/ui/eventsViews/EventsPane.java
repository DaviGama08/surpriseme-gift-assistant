package pt.isec.gps2526_g42.surprise_me.ui.eventsViews;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pt.isec.gps2526_g42.surprise_me.model.Occasion;
import pt.isec.gps2526_g42.surprise_me.model.data.SurpriseMeManager;
import pt.isec.gps2526_g42.surprise_me.model.data.EnjoyerDetails;
import pt.isec.gps2526_g42.surprise_me.ui.AppColors;
import pt.isec.gps2526_g42.surprise_me.ui.StyledButton;
import pt.isec.gps2526_g42.surprise_me.ui.dialogsViews.MessageModal;
import pt.isec.gps2526_g42.surprise_me.ui.res.FontManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class EventsPane extends VBox {
    private final SurpriseMeManager manager;

    private List<EventRow> allEvents;
    private final ObservableList<EventRow> observableEvents;
    private LocalDate currentMonth;

    private TableView<EventRow> tableEvents;
    private TextField searchField;
    private ComboBox<String> filterDropdown;
    private Text lbCurrentMonth;
    private Button btnPrevMonth;
    private Button btnNextMonth;
    private StyledButton btnAddEvent;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);

    private static class EventRow {
        private final int idEvent;
        private final String name;
        private final LocalDate date;
        private final Occasion occasion;
        private final String enjoyerName;

        public EventRow(int idEvent, String name, LocalDate date, Occasion occasion, String enjoyerName) {
            this.idEvent = idEvent;
            this.name = name;
            this.date = date;
            this.occasion = occasion;
            this.enjoyerName = enjoyerName;
        }

        public int getIdEvent() {
            return idEvent;
        }

        public String getName() {
            return name;
        }

        public LocalDate getDate() {
            return date;
        }

        public Occasion getOccasion() {
            return occasion;
        }

        public String getEnjoyerName() {
            return enjoyerName;
        }
    }

    public EventsPane(SurpriseMeManager manager) {
        this.manager = manager;
        this.currentMonth = LocalDate.now().withDayOfMonth(1);
        this.observableEvents = FXCollections.observableArrayList();
        this.allEvents = new ArrayList<>();

        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        VBox topBox = new VBox();
        VBox bottomBox = new VBox();

        // Create title
        Text initialTitle = new Text("Events");
        initialTitle.setFill(AppColors.BLUE.getColor());
        initialTitle.setFont(FontManager.loadFont("ADLaMDisplay-Regular.ttf", 64));

        Text finalTitle = new Text("List");
        finalTitle.setFill(AppColors.PURPLE.getColor());
        finalTitle.setFont(FontManager.loadFont("ADLaMDisplay-Regular.ttf", 64));

        HBox titleBox = new HBox(initialTitle, finalTitle);
        titleBox.setAlignment(Pos.TOP_LEFT);
        titleBox.setSpacing(0);
        topBox.getChildren().add(titleBox);
        topBox.setAlignment(Pos.TOP_CENTER);
        topBox.setPadding(new Insets(20, 15, 0, 15));

        // Month search bar
        btnPrevMonth = new Button("‹");
        btnPrevMonth.setStyle("-fx-background-color: transparent; -fx-font-size: 18; -fx-cursor: hand;");
        btnNextMonth = new Button("›");
        btnNextMonth.setStyle("-fx-background-color: transparent; -fx-font-size: 18; -fx-cursor:hand;");

        lbCurrentMonth = new Text();
        lbCurrentMonth.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        HBox currentMonthBox = new HBox(lbCurrentMonth);
        currentMonthBox.setMinWidth(160);
        currentMonthBox.setAlignment(Pos.CENTER);

        HBox monthAndActionBox = new HBox(10, btnPrevMonth, currentMonthBox, btnNextMonth);
        monthAndActionBox.setAlignment(Pos.CENTER_LEFT);

        // Search bar
        searchField = new TextField();
        searchField.setPromptText("Search");
        searchField.setPrefWidth(320);

        // Filter
        filterDropdown = new ComboBox<>();
        filterDropdown.getItems().add("All Occasions");
        filterDropdown.getSelectionModel().selectFirst();
        filterDropdown.setMinWidth(200);

        // Add event button
        btnAddEvent = new StyledButton(AppColors.GREEN, "Add event", AppColors.WHITE, "plus-solid-full.svg", true);

        HBox bar = new HBox(16, searchField, filterDropdown, btnAddEvent);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(10, 0, 10, 0));

        // Table
        tableEvents = new TableView<>();
        tableEvents.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<EventRow, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getDate().format(dateFormatter))
        );

        TableColumn<EventRow, String> eventCol = new TableColumn<>("Event");
        eventCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getName())
        );

        TableColumn<EventRow, String> enjoyerCol = new TableColumn<>("Enjoyer");
        enjoyerCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getEnjoyerName())
        );

        TableColumn<EventRow, String> occasionCol = new TableColumn<>("Occasion");
        occasionCol.setCellValueFactory(c -> {
            Occasion occ = c.getValue().getOccasion();
            String text = occ != null ? occ.getOccasion() : "";
            return new SimpleStringProperty(text);
        });

        tableEvents.getColumns().addAll(dateCol, eventCol, enjoyerCol, occasionCol);
        tableEvents.setItems(observableEvents);

        tableEvents.getSortOrder().add(dateCol);
        dateCol.setSortType(TableColumn.SortType.ASCENDING);
        tableEvents.sort();

        tableEvents.setFixedCellSize(40);
        tableEvents.prefHeightProperty().bind(
                tableEvents.fixedCellSizeProperty()
                        .multiply(Bindings.size(observableEvents).add(1.01))
        );

        tableEvents.setStyle("-fx-selection-bar: " + AppColors.PURPLE.getHexValue() +
                "; -fx-selection-bar-non-focused: " + AppColors.WHITE.getHexValue() + ";");

        bottomBox.getChildren().addAll(monthAndActionBox, bar, tableEvents);
        bottomBox.setPadding(new Insets(20));
        VBox.setVgrow(tableEvents, Priority.ALWAYS);

        getChildren().addAll(topBox, bottomBox);
        setVgrow(bottomBox, Priority.ALWAYS);
    }

    private void registerHandlers() {
        btnPrevMonth.setOnAction(e -> {
            currentMonth = currentMonth.minusMonths(1);
            update();
        });

        btnNextMonth.setOnAction(e -> {
            currentMonth = currentMonth.plusMonths(1);
            update();
        });

        tableEvents.setRowFactory(tv -> {
            TableRow<EventRow> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    EventRow selected = row.getItem();
                    EventDetailsModal edm =
                            new EventDetailsModal(manager, selected.getIdEvent());
                    edm.initModality(Modality.WINDOW_MODAL);
                    edm.initOwner(getScene().getWindow());
                    edm.showAndWait();
                    update();
                }
            });
            return row;
        });

        searchField.textProperty().addListener((obs, o, n) -> applyFilters());

        filterDropdown.valueProperty().addListener((obs, o, n) -> applyFilters());

        btnAddEvent.setOnAction(e -> {
            HashMap<Integer, EnjoyerDetails> enjoyers = manager.getEnjoyers();
            if (!enjoyers.isEmpty()) {
                EventDetailsModal edm = new EventDetailsModal(manager, null);
                edm.initModality(Modality.WINDOW_MODAL);
                edm.initOwner(getScene().getWindow());
                edm.showAndWait();
                update();
            } else {
                new MessageModal((Stage) this.getScene().getWindow(), "Can't add events!", "You don't have enjoyers. Please add an enjoyer before continuing.");
            }
        });
    }

    public void update() {
        lbCurrentMonth.setText(monthFormatter.format(currentMonth));

        List<Integer> ids = manager.getMonthlyEventsIds(currentMonth);

        allEvents = ids.stream()
                .map(id -> new EventRow(
                        id,
                        manager.getEventNameById(id),
                        manager.getEventDateById(id),
                        manager.getEventOccasionById(id),
                        manager.getEventEnjoyerNameById(id)
                ))
                .collect(Collectors.toCollection(ArrayList::new));

        updateFiltersData();
        applyFilters();
    }

    private void updateFiltersData() {
        Set<String> occasionTypes = allEvents.stream()
                .map(EventRow::getOccasion)
                .filter(o -> o != null)
                .map(Occasion::getOccasion)
                .collect(Collectors.toSet());

        List<String> sortedOccasions = occasionTypes.stream().sorted(String::compareToIgnoreCase).toList();

        filterDropdown.getItems().clear();
        filterDropdown.getItems().add("All occasions");
        filterDropdown.getItems().addAll(sortedOccasions);
        filterDropdown.getSelectionModel().selectFirst();
    }

    private void applyFilters() {
        String searchInput = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();

        String selectedOccasion = filterDropdown.getValue();
        final String occasionFilter = (selectedOccasion == null) ? "All occasions" : selectedOccasion;

        List<EventRow> filtered = allEvents.stream()
                .filter(ev -> {
                    boolean matchesSearch = true;
                    if (!searchInput.isEmpty()) {
                        String n = ev.getName() != null ? ev.getName().toLowerCase() : "";
                        String enjoyer = ev.getEnjoyerName() != null ? ev.getEnjoyerName().toLowerCase() : "";
                        String occ = ev.getOccasion() != null ? ev.getOccasion().getOccasion().toLowerCase() : "";
                        matchesSearch = n.contains(searchInput) || enjoyer.contains(searchInput) || occ.contains(searchInput);
                    }

                    boolean matchesOccasion = true;
                    if (!"All occasions".equals(occasionFilter)) {
                        String evOccasion = ev.getOccasion() != null ? ev.getOccasion().getOccasion() : "";
                        matchesOccasion = occasionFilter.equals(evOccasion);
                    }

                    return matchesSearch && matchesOccasion;
                })
                .collect(Collectors.toCollection(ArrayList::new));

        observableEvents.setAll(filtered);
        tableEvents.sort();
    }
}
