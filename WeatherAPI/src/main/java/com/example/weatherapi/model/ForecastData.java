package com.example.weatherapi.model;

/**
 * Model class representing forecast weather data
 */
public class ForecastData {
    private String date;
    private double maxTemp;
    private double minTemp;
    private String condition;
    private String iconUrl;
    private double precipitation;

    public ForecastData() {}

    public ForecastData(String date, double maxTemp, double minTemp, String condition,
                        String iconUrl, double precipitation) {
        this.date = date;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.condition = condition;
        this.iconUrl = iconUrl;
        this.precipitation = precipitation;
    }

    // Getters and Setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(double maxTemp) {
        this.maxTemp = maxTemp;
    }

    public double getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(double minTemp) {
        this.minTemp = minTemp;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public double getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(double precipitation) {
        this.precipitation = precipitation;
    }

    @Override
    public String toString() {
        return "ForecastData{" +
                "date='" + date + '\'' +
                ", maxTemp=" + maxTemp +
                ", minTemp=" + minTemp +
                ", condition='" + condition + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", precipitation=" + precipitation +
                '}';
    }
}