module com.example.moviebrowserproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    opens com.example.moviebrowserproject to javafx.fxml;
    exports com.example.moviebrowserproject;
}