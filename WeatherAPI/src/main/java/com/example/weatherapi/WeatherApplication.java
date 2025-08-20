package com.example.weatherapi;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main Weather Application class
 * Initializes and launches the JavaFX application
 */
public class WeatherApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WeatherApplication.class.getResource("weather-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);

        // Set application icon
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/example/weatherapi/icons/weather-icon.png")));

        stage.setTitle("Weather Forecast Application");
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}