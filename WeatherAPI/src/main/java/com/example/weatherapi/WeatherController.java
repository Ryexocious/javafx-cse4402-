package com.example.weatherapi;

import com.example.weatherapi.model.WeatherData;
import com.example.weatherapi.model.ForecastData;
import com.example.weatherapi.model.HistoricalData;
import com.example.weatherapi.service.WeatherService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Main controller for the Weather Application
 * Handles user interactions and updates the UI with weather data
 */
public class WeatherController implements Initializable {

    @FXML private TextField citySearchField;
    @FXML private Button searchButton;
    @FXML private Button themeToggleButton;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label errorLabel;

    // Current weather components
    @FXML private Label cityNameLabel;
    @FXML private Label currentTempLabel;
    @FXML private Label feelsLikeLabel;
    @FXML private Label conditionLabel;
    @FXML private Label humidityLabel;
    @FXML private Label cloudCoverageLabel;
    @FXML private Label precipitationLabel;
    @FXML private Label timeLabel;
    @FXML private Label aqiLabel;
    @FXML private ImageView weatherIcon;

    // Forecast components
    @FXML private HBox forecastContainer;

    // Historical data components
    @FXML private VBox historicalContainer;

    // Theme components
    @FXML private Pane rootPane;

    private WeatherService weatherService;
    private boolean isDarkMode = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        weatherService = new WeatherService();
        setupUI();
        loadDefaultWeather();
    }

    private void setupUI() {
        // Hide loading indicator initially
        loadingIndicator.setVisible(false);
        errorLabel.setVisible(false);

        // Setup search functionality
        searchButton.setOnAction(e -> searchWeather());
        citySearchField.setOnAction(e -> searchWeather());

        // Setup theme toggle
        themeToggleButton.setOnAction(e -> toggleTheme());
        themeToggleButton.setText("🌙");

        // Apply initial theme
        applyTheme();
    }

    private void loadDefaultWeather() {
        // Load weather for a default city (e.g., London)
        loadWeatherData("London");
    }

    @FXML
    private void searchWeather() {
        String city = citySearchField.getText().trim();
        if (!city.isEmpty()) {
            loadWeatherData(city);
        } else {
            showError("Please enter a city name");
        }
    }

    private void loadWeatherData(String city) {
        showLoading(true);
        hideError();

        Task<Void> weatherTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // Load current weather
                    WeatherData currentWeather = weatherService.getCurrentWeather(city);

                    // Load forecast
                    List<ForecastData> forecast = weatherService.getForecast(city, 3);

                    // Load historical data (last 7 days)
                    List<HistoricalData> historical = weatherService.getHistoricalWeather(city, 7);

                    Platform.runLater(() -> {
                        updateCurrentWeather(currentWeather);
                        updateForecast(forecast);
                        updateHistoricalData(historical);
                        showLoading(false);
                    });

                } catch (Exception e) {
                    Platform.runLater(() -> {
                        showError("Failed to load weather data: " + e.getMessage());
                        showLoading(false);
                    });
                }
                return null;
            }
        };

        Thread weatherThread = new Thread(weatherTask);
        weatherThread.setDaemon(true);
        weatherThread.start();
    }

    private void updateCurrentWeather(WeatherData weather) {
        cityNameLabel.setText(weather.getLocation());
        currentTempLabel.setText(weather.getTemperature() + "°C");
        feelsLikeLabel.setText("Feels like " + weather.getFeelsLike() + "°C");
        conditionLabel.setText(weather.getCondition());
        humidityLabel.setText("Humidity: " + weather.getHumidity() + "%");
        cloudCoverageLabel.setText("Cloud Coverage: " + weather.getCloudCoverage() + "%");
        precipitationLabel.setText("Precipitation: " + weather.getPrecipitation() + " mm");
        timeLabel.setText("Last Updated: " + weather.getTime());
        aqiLabel.setText("AQI: " + weather.getAqi());

        // Load weather icon
        try {
            String iconUrl = "https:" + weather.getIconUrl();
            Image icon = new Image(iconUrl);
            weatherIcon.setImage(icon);
        } catch (Exception e) {
            System.err.println("Failed to load weather icon: " + e.getMessage());
        }
    }

    private void updateForecast(List<ForecastData> forecast) {
        forecastContainer.getChildren().clear();

        for (ForecastData day : forecast) {
            VBox dayBox = createForecastDayBox(day);
            forecastContainer.getChildren().add(dayBox);
        }
    }

    private VBox createForecastDayBox(ForecastData forecast) {
        VBox dayBox = new VBox(5);
        dayBox.getStyleClass().add("forecast-day-box");

        Label dateLabel = new Label(forecast.getDate());
        dateLabel.getStyleClass().add("forecast-date");

        ImageView iconView = new ImageView();
        iconView.setFitWidth(50);
        iconView.setFitHeight(50);
        try {
            String iconUrl = "https:" + forecast.getIconUrl();
            iconView.setImage(new Image(iconUrl));
        } catch (Exception e) {
            // Use default icon or leave empty
        }

        Label tempLabel = new Label(forecast.getMaxTemp() + "°/" + forecast.getMinTemp() + "°");
        tempLabel.getStyleClass().add("forecast-temp");

        Label conditionLabel = new Label(forecast.getCondition());
        conditionLabel.getStyleClass().add("forecast-condition");

        dayBox.getChildren().addAll(dateLabel, iconView, tempLabel, conditionLabel);
        return dayBox;
    }

    private void updateHistoricalData(List<HistoricalData> historical) {
        historicalContainer.getChildren().clear();

        Label historyTitle = new Label("Past 7 Days");
        historyTitle.getStyleClass().add("section-title");
        historicalContainer.getChildren().add(historyTitle);

        for (HistoricalData day : historical) {
            HBox dayBox = createHistoricalDayBox(day);
            historicalContainer.getChildren().add(dayBox);
        }
    }

    private HBox createHistoricalDayBox(HistoricalData historical) {
        HBox dayBox = new HBox(15);
        dayBox.getStyleClass().add("historical-day-box");

        Label dateLabel = new Label(historical.getDate());
        dateLabel.setPrefWidth(100);

        Label tempLabel = new Label(historical.getMaxTemp() + "°/" + historical.getMinTemp() + "°");
        tempLabel.setPrefWidth(80);

        Label conditionLabel = new Label(historical.getCondition());
        conditionLabel.setPrefWidth(150);

        Label precipLabel = new Label(historical.getPrecipitation() + " mm");
        precipLabel.setPrefWidth(60);

        dayBox.getChildren().addAll(dateLabel, tempLabel, conditionLabel, precipLabel);
        return dayBox;
    }

    private void showLoading(boolean show) {
        loadingIndicator.setVisible(show);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void hideError() {
        errorLabel.setVisible(false);
    }

    @FXML
    private void toggleTheme() {
        isDarkMode = !isDarkMode;
        applyTheme();
        themeToggleButton.setText(isDarkMode ? "☀️" : "🌙");
    }

    private void applyTheme() {
        if (isDarkMode) {
            rootPane.getStyleClass().remove("light-theme");
            rootPane.getStyleClass().add("dark-theme");
        } else {
            rootPane.getStyleClass().remove("dark-theme");
            rootPane.getStyleClass().add("light-theme");
        }
    }
}