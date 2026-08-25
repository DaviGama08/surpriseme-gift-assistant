package pt.isec.gps2526_g42.surprise_me.ui.enjoyers;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pt.isec.gps2526_g42.surprise_me.application.SurpriseMeManager;
import pt.isec.gps2526_g42.surprise_me.model.data.EnjoyerDetails;
import pt.isec.gps2526_g42.surprise_me.ui.AppColors;
import pt.isec.gps2526_g42.surprise_me.ui.StyledButton;
import pt.isec.gps2526_g42.surprise_me.ui.dialogs.MessageModal;
import pt.isec.gps2526_g42.surprise_me.ui.dialogs.OkCancelModal;
import pt.isec.gps2526_g42.surprise_me.ui.suggestions.modals.DefineGiftModal;
import pt.isec.gps2526_g42.surprise_me.ui.res.FontManager;

import java.util.*;
import java.util.stream.Collectors;

public class EnjoyersPane extends VBox {
    private SurpriseMeManager manager;
    private TextField searchField;
    private ComboBox<String> filterDropdown;
    private StyledButton btnAddEnjoyer;
    private Pagination pagination;
    private HashMap<Integer, EnjoyerDetails> allEnjoyers;
    private List<Map.Entry<Integer, EnjoyerDetails>> filteredEnjoyers;
    private static final int CARDS_PER_PAGE = 6; // 2 x 3

    public EnjoyersPane(SurpriseMeManager manager) {
        this.manager = manager;
        filteredEnjoyers = new ArrayList<>();
        allEnjoyers = new HashMap<>();
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        VBox topBox = new VBox(); // box that includes the window title
        VBox bottomBox = new VBox(); // box that includes the window

        // Create title
        Text initialTitle = new Text("My");
        initialTitle.setFill(AppColors.BLUE.getColor());
        initialTitle.setFont(FontManager.loadFont("ADLaMDisplay-Regular.ttf", 64));

        Text finalTitle = new Text("Enjoyers");
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

        // Filter
        filterDropdown = new ComboBox<>();
        filterDropdown.getItems().addAll("Name", "Relationship");
        filterDropdown.getSelectionModel().selectFirst();
        filterDropdown.setMinWidth(200);

        // Add enjoyer button
        btnAddEnjoyer = new StyledButton(AppColors.GREEN, "Add enjoyer", AppColors.WHITE, "plus-solid-full.svg", true);

        HBox bar = new HBox(16, searchField, filterDropdown, btnAddEnjoyer);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(10, 0, 10, 0));

        pagination = new Pagination();

        bottomBox.getChildren().addAll(bar, pagination);
        VBox.setVgrow(pagination, Priority.ALWAYS);
        bottomBox.setSpacing(16);
        bottomBox.setPadding(new Insets(20));

        this.getChildren().addAll(topBox, bottomBox);
        setVgrow(bottomBox, Priority.ALWAYS);
    }

    private void registerHandlers() {
        // Add new enjoyer
        btnAddEnjoyer.setOnAction(e -> {
            EnjoyerModal enjoyerModal = new EnjoyerModal();
            enjoyerModal.initModality(Modality.WINDOW_MODAL);
            enjoyerModal.initOwner(this.getScene().getWindow());
            enjoyerModal.showAndWait();

            if (enjoyerModal.getDetails() != null) {
                manager.addEnjoyer(enjoyerModal.getDetails());
                new MessageModal((Stage) this.getScene().getWindow(), "Enjoyer added", "A new enjoyer was just added to your list!");
                update();
            }
        });

        // Filters
        searchField.textProperty().addListener((obs, o, n) -> applyFiltersAndRefresh());
        filterDropdown.valueProperty().addListener((obs, o, n) -> applyFiltersAndRefresh());
    }

    public void update() {
        // Gets a list of enjoyers (their ID and details)
        allEnjoyers = manager.getEnjoyers();

        // Apply filters and refresh
        applyFiltersAndRefresh();
    }

    private void applyFiltersAndRefresh() {
        filteredEnjoyers.clear();
        String searchInput = searchField.getText().trim().toLowerCase();
        String filter = filterDropdown.getValue();

        if (filter.equals("Name")) {
            filteredEnjoyers = allEnjoyers.entrySet().stream()
                    .filter(entry -> entry.getValue().getName().toLowerCase().contains(searchInput))
                    .collect(Collectors.toList());
        } else { // filter by relationship
            filteredEnjoyers = allEnjoyers.entrySet().stream()
                    .filter(entry -> entry.getValue().getRelationship().toLowerCase().contains(searchInput))
                    .collect(Collectors.toList());
        }

        // Enjoyers are always sorted by name
        filteredEnjoyers.sort(new Comparator<Map.Entry<Integer, EnjoyerDetails>>() {
            @Override
            public int compare(Map.Entry<Integer, EnjoyerDetails> o1, Map.Entry<Integer, EnjoyerDetails> o2) {
                if (o1.getValue().getName().compareToIgnoreCase(o2.getValue().getName()) < 0)
                    return -1;
                else
                    return 1;
            }
        });

        int pages = Math.max(1, (int) Math.ceil(filteredEnjoyers.size() / (double) CARDS_PER_PAGE));
        pagination.setPageCount(pages);
        pagination.setPageFactory(this::buildPage);
        pagination.setCurrentPageIndex(0);
    }

    private FlowPane buildPage(int pageIndex) {
        FlowPane pageGrid = new FlowPane();
        pageGrid.setHgap(20);
        pageGrid.setVgap(20);
        pageGrid.setPrefWrapLength(860);
        pageGrid.setAlignment(Pos.TOP_LEFT);
        pageGrid.setPadding(new Insets(10, 0, 10, 0));

        if (filteredEnjoyers == null || filteredEnjoyers.isEmpty()) {
            Label empty = new Label("No results found...");
            empty.setStyle("-fx-text-fill: " + AppColors.LIGHT_GRAY.getHexValue() + "; -fx-font-size: 14px;");
            pageGrid.getChildren().add(empty);
            return pageGrid;
        }

        int start = pageIndex * CARDS_PER_PAGE;
        int end = Math.min(start + CARDS_PER_PAGE, filteredEnjoyers.size());

        for (int i = start; i < end; i++) {
            pageGrid.getChildren().add(buildCard(filteredEnjoyers.get(i)));
        }

        return pageGrid;
    }

    private Region buildCard(Map.Entry<Integer, EnjoyerDetails> enjoyerCard) {
        HBox card = new HBox(16);
        card.setPadding(new Insets(14));
        card.setPrefWidth(420);
        card.setMinHeight(120);
        card.setStyle("-fx-background-color:" + AppColors.WHITE.getHexValue() + "; -fx-background-radius: 12; -fx-border-color: "
                + AppColors.LIGHT_GRAY.getHexValue() + "; -fx-border-radius: 12;");

        // avatar with initials
        StackPane avatar = new StackPane();
        Circle circleName = new Circle(28, AppColors.PURPLE.getColor());
        Label initials = new Label(getInitials(enjoyerCard.getValue().getName()));
        initials.setTextFill(AppColors.WHITE.getColor());
        initials.setFont(Font.font("System", FontWeight.BOLD, 14));
        avatar.getChildren().addAll(circleName, initials);

        Label name = new Label(enjoyerCard.getValue().getName());
        name.setFont(Font.font("System", FontWeight.BOLD, 16));
        Label relationship = new Label(enjoyerCard.getValue().getRelationship());
        relationship.setStyle("-fx-text-fill: " + AppColors.LIGHT_GRAY.getHexValue() + "; -fx-font-size: 12 px;");

        // likes
        FlowPane likesBox = new FlowPane(6, 6);
        if (enjoyerCard.getValue().getLikes() != null) {
            int numberLikes = enjoyerCard.getValue().getLikes().size();
            int counter = 0;
            for (String like : enjoyerCard.getValue().getLikes()) {
                Label chip = new Label(like);
                chip.setStyle("-fx-background-color: " + AppColors.VIVID_BLUE.getHexValue()
                        + "; -fx-text-fill: " + AppColors.WHITE.getHexValue()
                        + "; -fx-background-radius: 999; -fx-padding: 3 8 3 8; -fx-font-size: 12 px;");
                likesBox.getChildren().add(chip);
                counter++;
                if (counter == 2 && enjoyerCard.getValue().getLikes().size() > 2) {
                    Label extraChip = new Label("+" + (numberLikes - 2));
                    extraChip.setStyle("-fx-background-color: " + AppColors.VIVID_BLUE.getHexValue()
                            + "; -fx-text-fill: " + AppColors.WHITE.getHexValue()
                            + "; -fx-background-radius: 999; -fx-padding: 3 8 3 8; -fx-font-size: 12 px;");
                    likesBox.getChildren().add(extraChip);
                    break;
                }
            }
        }

        VBox text = new VBox(2, name, relationship, likesBox);

        // buttons
        StyledButton btnView = new StyledButton(AppColors.BLUE, "View/Edit", AppColors.WHITE, "pen-to-square-solid-full.svg", true);
        StyledButton btnDelete = new StyledButton(AppColors.RED, "Delete", AppColors.WHITE, "trash-can-solid-full.svg", true);
        StyledButton btnGift = new StyledButton(AppColors.GREEN, "Generate gift", AppColors.WHITE, "gift-solid-full.svg", true);

        //View/edit button
        btnView.setOnAction(e -> {
            EnjoyerModal em = new EnjoyerModal(enjoyerCard.getKey(), enjoyerCard.getValue());
            em.initModality(Modality.WINDOW_MODAL);
            em.initOwner(getScene().getWindow());
            em.showAndWait();

            if (em.getDetails() != null) {
                manager.editEnjoyer(enjoyerCard.getKey(), em.getDetails());
                new MessageModal((Stage) this.getScene().getWindow(), "Enjoyer edited", "Your enjoyer data was successfully updated!");
                applyFiltersAndRefresh();
            }
        });

        // Delete handler
        btnDelete.setOnAction(e -> {
            String nome = enjoyerCard.getValue().getName();
            Stage owner = (Stage) this.getScene().getWindow();
            boolean confirm = OkCancelModal.show(owner, "Confirm deletion", "Are you sure you want to delete \"" + nome + "\"? This action is irreversible.");
            if (!confirm) return;

            boolean ok = manager.removeEnjoyer(enjoyerCard.getKey());
            if (ok) {
                new MessageModal(owner, "Enjoyer deleted", "The enjoyer was successfully deleted.");
                update();
            } else {
                new MessageModal(owner, "Error", "Failed to delete enjoyer.");
            }
        });

        //Generate gifts from enjoyer's pane
        btnGift.setOnAction(e -> {
            DefineGiftModal define = new DefineGiftModal(manager, enjoyerCard.getKey(), enjoyerCard.getValue(), null);
            define.initModality(Modality.WINDOW_MODAL);
            define.initOwner(getScene().getWindow());
            define.showAndWait();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox actions = new VBox(8, btnView, btnDelete, btnGift);
        actions.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(avatar, text, spacer, actions);
        return card;
    }

    private String getInitials(String name) {
        String[] p = name.trim().split("\\s+");
        String a = p[0].substring(0, 1);
        String b = p.length > 1 ? p[p.length - 1].substring(0, 1) : "";
        return (a + b).toUpperCase();
    }
}
