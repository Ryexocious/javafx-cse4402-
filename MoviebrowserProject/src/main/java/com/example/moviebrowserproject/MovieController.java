package com.example.moviebrowserproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class MovieController implements Initializable {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> genreComboBox;
    @FXML private ScrollPane movieScrollPane;
    @FXML private VBox movieContainer;
    @FXML private Button watchLaterButton;
    @FXML private ToggleButton darkModeToggle;
    @FXML private BorderPane rootPane;

    private DatabaseManager dbManager;
    private ObservableList<Movie> currentMovies;
    private boolean isDarkMode = false;
    private boolean showingWatchLater = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbManager = DatabaseManager.getInstance();
        currentMovies = FXCollections.observableArrayList();

        setupGenreComboBox();
        setupSearchField();
        setupDarkModeToggle();
        loadAllMovies();
    }

    private void setupGenreComboBox() {
        genreComboBox.getItems().add("All Genres");
        genreComboBox.getItems().addAll(dbManager.getAllGenres());
        genreComboBox.setValue("All Genres");

        genreComboBox.setOnAction(e -> {
            String selectedGenre = genreComboBox.getValue();
            if ("All Genres".equals(selectedGenre)) {
                loadAllMovies();
            } else {
                loadMoviesByGenre(selectedGenre);
            }
        });
    }

    private void setupSearchField() {
        searchField.setOnKeyReleased(e -> {
            String searchTerm = searchField.getText().trim();
            if (searchTerm.isEmpty()) {
                loadAllMovies();
            } else {
                searchMovies(searchTerm);
            }
        });
    }

    private void setupDarkModeToggle() {
        darkModeToggle.setOnAction(e -> toggleDarkMode());
    }

    @FXML
    private void handleWatchLaterButton() {
        if (showingWatchLater) {
            loadAllMovies();
            watchLaterButton.setText("Watch Later");
            showingWatchLater = false;
        } else {
            loadWatchLaterMovies();
            watchLaterButton.setText("All Movies");
            showingWatchLater = true;
        }
    }

    @FXML
    private void handleRefreshDatabase() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Refresh Database");
        confirmation.setHeaderText("Refresh Movie Database");
        confirmation.setContentText("This will update the database with the latest movie list. Your Watch Later list will be cleared. Continue?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                dbManager.refreshMovieDatabase();
                loadAllMovies();
                setupGenreComboBox(); // Refresh genres
            }
        });
    }

    private void loadAllMovies() {
        currentMovies = dbManager.getAllMovies();
        displayMovies();
    }

    private void searchMovies(String searchTerm) {
        currentMovies = dbManager.searchMovies(searchTerm);
        displayMovies();
    }

    private void loadMoviesByGenre(String genre) {
        currentMovies = dbManager.getMoviesByGenre(genre);
        displayMovies();
    }

    private void loadWatchLaterMovies() {
        currentMovies = dbManager.getWatchLaterMovies();
        displayMovies();
    }

    private void displayMovies() {
        movieContainer.getChildren().clear();

        if (currentMovies.isEmpty()) {
            Label noMoviesLabel = new Label("No movies found");
            noMoviesLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: gray;");
            VBox.setMargin(noMoviesLabel, new Insets(20));
            movieContainer.getChildren().add(noMoviesLabel);
            return;
        }

        // Create a grid of movie cards
        GridPane gridPane = new GridPane();
        gridPane.setHgap(20);
        gridPane.setVgap(20);
        gridPane.setPadding(new Insets(20));

        int columns = 4;
        int row = 0;
        int col = 0;

        for (Movie movie : currentMovies) {
            VBox movieCard = createMovieCard(movie);
            gridPane.add(movieCard, col, row);

            col++;
            if (col >= columns) {
                col = 0;
                row++;
            }
        }

        movieContainer.getChildren().add(gridPane);
    }

    private VBox createMovieCard(Movie movie) {
        VBox card = new VBox();
        card.setSpacing(10);
        card.setPrefWidth(200);
        card.setMaxWidth(200);
        card.setPadding(new Insets(15));
        card.setStyle(getCardStyle());
        card.getStyleClass().add("movie-card");

        // Movie poster
        ImageView posterView = new ImageView();
        posterView.setFitWidth(170);
        posterView.setFitHeight(250);
        posterView.setPreserveRatio(true);

        // Try to load poster image, use placeholder if not found
        try {
            InputStream imageStream = getClass().getResourceAsStream(movie.getPosterImagePath());
            if (imageStream != null) {
                Image posterImage = new Image(imageStream);
                posterView.setImage(posterImage);
            } else {
                // Try alternative path
                imageStream = getClass().getResourceAsStream("/" + movie.getPosterImagePath());
                if (imageStream != null) {
                    Image posterImage = new Image(imageStream);
                    posterView.setImage(posterImage);
                } else {
                    // Create a placeholder
                    posterView.setStyle("-fx-background-color: #cccccc;");
                }
            }
        } catch (Exception e) {
            posterView.setStyle("-fx-background-color: #cccccc;");
        }

        // Movie title
        Label titleLabel = new Label(movie.getTitle());
        titleLabel.setWrapText(true);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        titleLabel.setMaxWidth(170);

        // Genre and rating
        Label genreRatingLabel = new Label(movie.getGenre() + " | ★ " + movie.getImdbRating());
        genreRatingLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 12px;");

        // Duration
        Label durationLabel = new Label(movie.getFormattedDuration());
        durationLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 11px;");

        // Buttons
        HBox buttonBox = new HBox(5);
        buttonBox.setAlignment(Pos.CENTER);

        Button detailsButton = new Button("Details");
        detailsButton.setStyle(getButtonStyle());
        detailsButton.setOnAction(e -> showMovieDetails(movie));

        Button watchLaterBtn = new Button(movie.isInWatchLater() ? "Remove" : "Add");
        watchLaterBtn.setStyle(getSecondaryButtonStyle());
        watchLaterBtn.setOnAction(e -> toggleWatchLater(movie, watchLaterBtn));

        buttonBox.getChildren().addAll(detailsButton, watchLaterBtn);

        card.getChildren().addAll(posterView, titleLabel, genreRatingLabel, durationLabel, buttonBox);

        // Add hover effect
        card.setOnMouseEntered(e -> card.setStyle(getCardHoverStyle()));
        card.setOnMouseExited(e -> card.setStyle(getCardStyle()));

        return card;
    }

    private void showMovieDetails(Movie movie) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Movie Details");
        alert.setHeaderText(movie.getTitle());

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        content.getChildren().addAll(
                new Label("Genre: " + movie.getGenre()),
                new Label("Cast: " + movie.getCast()),
                new Label("Duration: " + movie.getFormattedDuration()),
                new Label("IMDB Rating: ★ " + movie.getImdbRating()),
                new Label("Plot Summary:"),
                createPlotLabel(movie.getPlotSummary())
        );

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setPrefHeight(300);
        scrollPane.setPrefWidth(400);
        scrollPane.setFitToWidth(true);

        alert.getDialogPane().setContent(scrollPane);
        alert.showAndWait();
    }

    private Label createPlotLabel(String plot) {
        Label plotLabel = new Label(plot);
        plotLabel.setWrapText(true);
        plotLabel.setMaxWidth(360);
        plotLabel.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 10; -fx-background-radius: 5;");
        return plotLabel;
    }

    private void toggleWatchLater(Movie movie, Button button) {
        if (movie.isInWatchLater()) {
            dbManager.removeFromWatchLater(movie.getId());
            movie.setInWatchLater(false);
            button.setText("Add");
        } else {
            dbManager.addToWatchLater(movie.getId());
            movie.setInWatchLater(true);
            button.setText("Remove");
        }

        // If currently showing watch later list, refresh it
        if (showingWatchLater) {
            loadWatchLaterMovies();
        }
    }

    private void toggleDarkMode() {
        isDarkMode = !isDarkMode;
        applyTheme();
    }

    private void applyTheme() {
        if (isDarkMode) {
            rootPane.setStyle("-fx-background-color: #2b2b2b;");
            searchField.setStyle("-fx-background-color: #404040; -fx-text-fill: white; -fx-border-color: #555;");
            genreComboBox.setStyle("-fx-background-color: #404040; -fx-text-fill: white;");
            watchLaterButton.setStyle(getDarkButtonStyle());
            darkModeToggle.setText("Light Mode");
        } else {
            rootPane.setStyle("-fx-background-color: white;");
            searchField.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-border-color: #ccc;");
            genreComboBox.setStyle("-fx-background-color: white; -fx-text-fill: black;");
            watchLaterButton.setStyle(getButtonStyle());
            darkModeToggle.setText("Dark Mode");
        }
        displayMovies(); // Refresh movie display with new theme
    }

    private String getCardStyle() {
        if (isDarkMode) {
            return "-fx-background-color: #404040; -fx-background-radius: 10; -fx-border-color: #555; -fx-border-radius: 10; -fx-border-width: 1; -fx-text-fill: white;";
        } else {
            return "-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e0e0e0; -fx-border-radius: 10; -fx-border-width: 1;";
        }
    }

    private String getCardHoverStyle() {
        if (isDarkMode) {
            return "-fx-background-color: #505050; -fx-background-radius: 10; -fx-border-color: #777; -fx-border-radius: 10; -fx-border-width: 1; -fx-text-fill: white;";
        } else {
            return "-fx-background-color: #f8f8f8; -fx-background-radius: 10; -fx-border-color: #d0d0d0; -fx-border-radius: 10; -fx-border-width: 1;";
        }
    }

    private String getButtonStyle() {
        if (isDarkMode) {
            return "-fx-background-color: #0084ff; -fx-text-fill: white; -fx-background-radius: 5; -fx-border-radius: 5; -fx-font-size: 11px;";
        } else {
            return "-fx-background-color: #007bff; -fx-text-fill: white; -fx-background-radius: 5; -fx-border-radius: 5; -fx-font-size: 11px;";
        }
    }

    private String getSecondaryButtonStyle() {
        if (isDarkMode) {
            return "-fx-background-color: #6c757d; -fx-text-fill: white; -fx-background-radius: 5; -fx-border-radius: 5; -fx-font-size: 11px;";
        } else {
            return "-fx-background-color: #6c757d; -fx-text-fill: white; -fx-background-radius: 5; -fx-border-radius: 5; -fx-font-size: 11px;";
        }
    }

    private String getDarkButtonStyle() {
        return "-fx-background-color: #28a745; -fx-text-fill: white; -fx-background-radius: 5; -fx-border-radius: 5;";
    }
}