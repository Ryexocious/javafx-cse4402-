package application;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class PomodoroTimer {
    private final PomodoroController controller;
    private Timeline timeline;
    private int timeLeft; // seconds left
    private boolean isWork = true;
    private int sessionCount = 0;

    private int workSec = 1500, breakSec = 300; // default 25 min work, 5 min break
    private final int longBreakSec = 1200; // 20 minutes

    public PomodoroTimer(PomodoroController controller) {
        this.controller = controller;
    }

    public void setDurations(int workMin, int breakMin) {
        this.workSec = workMin;
        this.breakSec = breakMin;
    }

    public void start() {
        if (timeline != null) timeline.stop();
        timeLeft = isWork ? workSec : (sessionCount == 4 ? longBreakSec : breakSec);
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> tick()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void pause() {
        if (timeline != null) timeline.pause();
    }

    public void reset() {
        if (timeline != null) timeline.stop();
        timeLeft = 0;
        isWork = true;
        sessionCount = 0;
        controller.updateTimerDisplay("00:00", 0, "Session Reset");
    }

    private void tick() {
        if (--timeLeft < 0) {
            // Play alert beep + show popup
            controller.showAlert(isWork ? "Work session ended! Time for a break." : "Break session ended! Time to work.");

            if (isWork) sessionCount++;
            if (sessionCount > 4) sessionCount = 0; // reset after long break

            isWork = !isWork;
            start(); // start next session
            return;
        }

        int total = isWork ? workSec : (sessionCount == 4 ? longBreakSec : breakSec);
        double progress = 1 - (double) timeLeft / total;
        String label = isWork ? "Work" : (sessionCount == 4 ? "Long Break" : "Break");

        controller.updateTimerDisplay(format(timeLeft), progress, label);
    }

    private String format(int seconds) {
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }
}
