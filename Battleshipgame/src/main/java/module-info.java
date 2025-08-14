module com.example.battleshipgame {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.battleshipgame to javafx.fxml;
    exports com.example.battleshipgame;
}