module com.example.weatherapi {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.google.gson;

    exports com.example.weatherapi;
    exports com.example.weatherapi.model;
    exports com.example.weatherapi.service;

    // Open packages for reflection (needed by JavaFX FXML and Gson)
    opens com.example.weatherapi to javafx.fxml;
    opens com.example.weatherapi.model to com.google.gson;
}