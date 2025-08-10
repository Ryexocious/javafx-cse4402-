package application;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.awt.Toolkit;  // <-- import Toolkit for beep

public class PomodoroController {
    @FXML private VBox rootVBox;
    @FXML private Label sessionLabel, timerLabel;
    @FXML private ProgressBar progressBar;
    @FXML private TextField workDurationInput, breakDurationInput, taskInput;
    @FXML private ListView<String> taskList;
    @FXML private ToggleButton modeToggle;

    private PomodoroTimer timer;

    @FXML
    public void initialize() {
        workDurationInput.setText("25");
        breakDurationInput.setText("5");

        timer = new PomodoroTimer(this);

        rootVBox.getStyleClass().add("light-mode");  // start light mode
    }

    public void updateTimerDisplay(String time, double progress, String label) {
        Platform.runLater(() -> {
            timerLabel.setText(time);
            progressBar.setProgress(progress);
            sessionLabel.setText(label);
        });
    }

    @FXML
    public void startTimer() {
        try {
            int workMin = Integer.parseInt(workDurationInput.getText().trim());
            int breakMin = Integer.parseInt(breakDurationInput.getText().trim());

            if (workMin <= 0 || breakMin <= 0) {
                showAlert("Please enter positive numbers.");
                return;
            }

            timer.setDurations(workMin, breakMin);
            timer.start();

        } catch (NumberFormatException e) {
            showAlert("Please enter valid numeric values.");
        }
    }

    @FXML
    public void pauseTimer() {
        timer.pause();
    }

    @FXML
    public void resetTimer() {
        timer.reset();
    }

    @FXML
    public void addTask() {
        String task = taskInput.getText();
        if (task != null && !task.trim().isEmpty()) {
            taskList.getItems().add(task.trim());
            taskInput.clear();
        }
    }

    @FXML
    public void toggleMode() {
        if (modeToggle.isSelected()) {
            rootVBox.getStyleClass().removeAll("light-mode");
            rootVBox.getStyleClass().add("dark-mode");
        } else {
            rootVBox.getStyleClass().removeAll("dark-mode");
            rootVBox.getStyleClass().add("light-mode");
        }
    }

    // Beep repeatedly while alert open
    public void showAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Pomodoro Alert");
            alert.setHeaderText(null);
            alert.setContentText(message);

            Thread beepThread = new Thread(() -> {
                try {
                    while (alert.isShowing()) {
                        Toolkit.getDefaultToolkit().beep();
                        Thread.sleep(600);
                    }
                } catch (InterruptedException ignored) {}
            });
            beepThread.setDaemon(true);
            beepThread.start();

            alert.showAndWait();

            beepThread.interrupt();
        });
    }
}
