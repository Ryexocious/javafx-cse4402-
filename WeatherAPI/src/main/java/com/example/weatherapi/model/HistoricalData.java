package com.example.weatherapi.model;

/**
 * Model class representing historical weather data
 */
public class HistoricalData {
    private String date;
    private double maxTemp;
    private double minTemp;
    private String condition;
    private double precipitation;

    public HistoricalData() {}

    public HistoricalData(String date, double maxTemp, double minTemp,
                          String condition, double precipitation) {
        this.date = date;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.condition = condition;
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

    public double getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(double precipitation) {
        this.precipitation = precipitation;
    }

    @Override
    public String toString() {
        return "HistoricalData{" +
                "date='" + date + '\'' +
                ", maxTemp=" + maxTemp +
                ", minTemp=" + minTemp +
                ", condition='" + condition + '\'' +
                ", precipitation=" + precipitation +
                '}';
    }
}