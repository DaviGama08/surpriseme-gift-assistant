package pt.isec.gps2526_g42.surprise_me.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import pt.isec.gps2526_g42.surprise_me.model.data.Event;
import pt.isec.gps2526_g42.surprise_me.model.data.SurpriseMeManager;
import pt.isec.gps2526_g42.surprise_me.ui.res.FontManager;
import pt.isec.gps2526_g42.surprise_me.ui.res.ImageManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class DashboardPane extends VBox {
    private final SurpriseMeManager manager;

    private HBox eventsCardsBox;

    private final DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("d");
    private final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH);

    public DashboardPane(SurpriseMeManager manager) {
        this.manager = manager;
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        VBox topBox = new VBox();
        // UI components
        VBox bottomBox = new VBox();

        // title
        Text initialTitle = new Text("Welcome to ");
        initialTitle.setFill(AppColors.BLACK.getColor());
        initialTitle.setFont(FontManager.loadFont("ADLaMDisplay-Regular.ttf", 64));

        Text middleTitle = new Text("Surprise");
        middleTitle.setFill(AppColors.BLUE.getColor());
        middleTitle.setFont(FontManager.loadFont("ADLaMDisplay-Regular.ttf", 64));

        Text finalTitle = new Text("Me");
        finalTitle.setFill(AppColors.PURPLE.getColor());
        finalTitle.setFont(FontManager.loadFont("ADLaMDisplay-Regular.ttf", 64));

        HBox titleBox = new HBox(initialTitle, middleTitle, finalTitle);
        titleBox.setAlignment(Pos.TOP_LEFT);
        titleBox.setSpacing(0);

        // metrics cards
        HBox cardsBox = new HBox(20);
        cardsBox.setAlignment(Pos.TOP_LEFT);
        cardsBox.setPadding(new Insets(10, 0, 0, 0));

        // surprised enjoyers card
        VBox enjoyersCard = new VBox(8);
        enjoyersCard.setMinWidth(260);
        enjoyersCard.setMaxWidth(260);
        enjoyersCard.setAlignment(Pos.CENTER_LEFT);
        enjoyersCard.setPadding(new Insets(20));
        enjoyersCard.setStyle("-fx-background-color: " + AppColors.BLUE.getHexValue() + "; -fx-background-radius: 25;");

        Text enjoyersCardValue = new Text(String.valueOf(manager.getSurprisedEnjoyers()));
        enjoyersCardValue.setFill(AppColors.WHITE.getColor());
        enjoyersCardValue.setFont(new Font(32));

        Text enjoyersCardTitle = new Text("enjoyers surprised");
        enjoyersCardTitle.setFill(AppColors.WHITE.getColor());
        enjoyersCardTitle.setFont(new Font(18));

        enjoyersCard.getChildren().addAll(enjoyersCardValue, enjoyersCardTitle);

        // accepted suggestions card
        VBox giftsCard = new VBox(8);
        giftsCard.setMinWidth(260);
        giftsCard.setMaxWidth(260);
        giftsCard.setAlignment(Pos.CENTER_LEFT);
        giftsCard.setPadding(new Insets(20));
        giftsCard.setStyle("-fx-background-color: " + AppColors.PURPLE.getHexValue() + "; -fx-background-radius: 25;");

        Text giftsCardValue = new Text(String.valueOf(manager.getGifts().size()));
        giftsCardValue.setFill(AppColors.WHITE.getColor());
        giftsCardValue.setFont(new Font(32));

        Text giftsCardTitle = new Text("accepted suggestions");
        giftsCardTitle.setFill(AppColors.WHITE.getColor());
        giftsCardTitle.setFont(new Font(18));

        giftsCard.getChildren().addAll(giftsCardValue, giftsCardTitle);

        cardsBox.getChildren().addAll(enjoyersCard, giftsCard);

        VBox topRight = new VBox(15);
        topRight.getChildren().addAll(titleBox, cardsBox);

        // round logo
        Image image = ImageManager.getImage("logo.png");
        ImageView imageView = new ImageView(image);
        double size = 250;
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        Circle clip = new Circle(size / 2, size / 2, size / 2);
        imageView.setClip(clip);

        HBox imageTitleBox = new HBox(imageView, topRight);
        imageTitleBox.setAlignment(Pos.TOP_LEFT);
        imageTitleBox.setSpacing(40);

        topBox.getChildren().add(imageTitleBox);
        topBox.setAlignment(Pos.TOP_CENTER);
        topBox.setPadding(new Insets(20, 40, 10, 40));

        // upcoming events area
        Text eventsTitle = new Text("Upcoming events");
        eventsTitle.setStyle("-fx-font-size: 28; -fx-font-weight: bold;");

        eventsCardsBox = new HBox(20);
        eventsCardsBox.setAlignment(Pos.CENTER_LEFT);
        eventsCardsBox.setPadding(new Insets(10, 10, 35, 10));

        ScrollPane eventsScroll = new ScrollPane(eventsCardsBox);
        eventsScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        eventsScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        eventsScroll.setFitToHeight(false);
        eventsScroll.setPannable(true);
        eventsScroll.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-padding: 0;");

        bottomBox.setPadding(new Insets(10, 40, 20, 40));
        bottomBox.setSpacing(10);
        bottomBox.getChildren().addAll(eventsTitle, eventsScroll);

        this.getChildren().addAll(topBox, bottomBox);
        setVgrow(bottomBox, Priority.ALWAYS);
    }

    private void registerHandlers() {
    }

    public void update() {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);

        List<Event> monthlyEvents = manager.getMonthlyEvents(firstDayOfMonth);

        for (Event event : monthlyEvents) {
            LocalDate date = event.getDate();
            if (date == null || date.isBefore(today)) {
                continue;
            }

            String name = event.getName();
            String enjoyerFullName = event.getEnjoyerName();
            String[] parts = enjoyerFullName.trim().split("\\s+");
            String enjoyerName;
            if (parts.length > 1) {
                enjoyerName = parts[0] + " " + parts[parts.length - 1];
            } else {
                enjoyerName = parts[0];
            }

            String dayText = date.format(dayFormatter);
            String monthText = date.format(monthFormatter).toUpperCase(Locale.ENGLISH);

            VBox card = new VBox(8);
            card.setMinWidth(200);
            card.setMaxWidth(200);
            card.setAlignment(Pos.TOP_CENTER);
            card.setPadding(new Insets(20));
            card.setStyle("-fx-background-color: " + AppColors.WHITE.getHexValue() + "; -fx-border-color: "
                    + AppColors.LIGHT_GRAY.getHexValue() + "; -fx-border-radius: 20; -fx-background-radius: 20;");

            Text lbDay = new Text(dayText);
            lbDay.setStyle("-fx-font-size: 32; -fx-font-weight: bold;");

            Text lbMonth = new Text(monthText);
            lbMonth.setStyle("-fx-font-size: 32; -fx-font-weight: bold;");

            Text lbEnjoyerName = new Text(enjoyerName);
            lbEnjoyerName.setStyle("-fx-font-size: 16;");
            lbEnjoyerName.setWrappingWidth(160);
            lbEnjoyerName.setTextAlignment(TextAlignment.CENTER);

            Text lbEventName = new Text(name);
            lbEventName.setStyle("-fx-font-size: 16;");
            lbEventName.setWrappingWidth(160);
            lbEventName.setTextAlignment(TextAlignment.CENTER);

            VBox nameAndEventBox = new VBox(8, lbEnjoyerName, lbEventName);
            nameAndEventBox.setAlignment(Pos.CENTER);

            card.getChildren().addAll(lbDay, lbMonth, nameAndEventBox);
            eventsCardsBox.getChildren().add(card);
        }

        if (eventsCardsBox.getChildren().isEmpty()) {
            VBox emptyCard = new VBox(8);
            emptyCard.setMinWidth(200);
            emptyCard.setMaxWidth(200);
            emptyCard.setAlignment(Pos.CENTER);
            emptyCard.setPadding(new Insets(20));
            emptyCard.setStyle("-fx-background-color: " + AppColors.WHITE.getHexValue() + "; -fx-border-color: "
                    + AppColors.LIGHT_GRAY.getHexValue() + "; -fx-border-radius: 20; -fx-background-radius: 20;"
            );

            Text emptyTitle = new Text("No upcoming events");
            emptyTitle.setStyle("-fx-font-size: 16;");

            emptyCard.getChildren().add(emptyTitle);
            eventsCardsBox.getChildren().add(emptyCard);
        }
    }
}
