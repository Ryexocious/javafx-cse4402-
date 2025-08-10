package com.example.moviebrowserproject;

import javafx.beans.property.*;
import java.util.List;

public class Movie {
    private final IntegerProperty id;
    private final StringProperty title;
    private final StringProperty genre;
    private final StringProperty cast;
    private final IntegerProperty duration; // in minutes
    private final DoubleProperty imdbRating;
    private final StringProperty plotSummary;
    private final StringProperty posterImagePath;
    private final BooleanProperty isInWatchLater;

    public Movie(int id, String title, String genre, String cast, int duration,
                 double imdbRating, String plotSummary, String posterImagePath) {
        this.id = new SimpleIntegerProperty(id);
        this.title = new SimpleStringProperty(title);
        this.genre = new SimpleStringProperty(genre);
        this.cast = new SimpleStringProperty(cast);
        this.duration = new SimpleIntegerProperty(duration);
        this.imdbRating = new SimpleDoubleProperty(imdbRating);
        this.plotSummary = new SimpleStringProperty(plotSummary);
        this.posterImagePath = new SimpleStringProperty(posterImagePath);
        this.isInWatchLater = new SimpleBooleanProperty(false);
    }

    // Getters and Setters
    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public String getTitle() { return title.get(); }
    public StringProperty titleProperty() { return title; }
    public void setTitle(String title) { this.title.set(title); }

    public String getGenre() { return genre.get(); }
    public StringProperty genreProperty() { return genre; }
    public void setGenre(String genre) { this.genre.set(genre); }

    public String getCast() { return cast.get(); }
    public StringProperty castProperty() { return cast; }
    public void setCast(String cast) { this.cast.set(cast); }

    public int getDuration() { return duration.get(); }
    public IntegerProperty durationProperty() { return duration; }
    public void setDuration(int duration) { this.duration.set(duration); }

    public double getImdbRating() { return imdbRating.get(); }
    public DoubleProperty imdbRatingProperty() { return imdbRating; }
    public void setImdbRating(double imdbRating) { this.imdbRating.set(imdbRating); }

    public String getPlotSummary() { return plotSummary.get(); }
    public StringProperty plotSummaryProperty() { return plotSummary; }
    public void setPlotSummary(String plotSummary) { this.plotSummary.set(plotSummary); }

    public String getPosterImagePath() { return posterImagePath.get(); }
    public StringProperty posterImagePathProperty() { return posterImagePath; }
    public void setPosterImagePath(String posterImagePath) { this.posterImagePath.set(posterImagePath); }

    public boolean isInWatchLater() { return isInWatchLater.get(); }
    public BooleanProperty isInWatchLaterProperty() { return isInWatchLater; }
    public void setInWatchLater(boolean inWatchLater) { this.isInWatchLater.set(inWatchLater); }

    public String getFormattedDuration() {
        int hours = duration.get() / 60;
        int minutes = duration.get() % 60;
        if (hours > 0) {
            return String.format("%dh %dm", hours, minutes);
        } else {
            return String.format("%dm", minutes);
        }
    }

    @Override
    public String toString() {
        return title.get();
    }
}
