package com.example.weatherapi.model;

/**
 * Model class representing current weather data
 */
public class WeatherData {
    private String location;
    private double temperature;
    private double feelsLike;
    private String condition;
    private int humidity;
    private int cloudCoverage;
    private double precipitation;
    private String time;
    private int aqi;
    private String iconUrl;

    public WeatherData() {}

    public WeatherData(String location, double temperature, double feelsLike, String condition,
                       int humidity, int cloudCoverage, double precipitation, String time,
                       int aqi, String iconUrl) {
        this.location = location;
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.condition = condition;
        this.humidity = humidity;
        this.cloudCoverage = cloudCoverage;
        this.precipitation = precipitation;
        this.time = time;
        this.aqi = aqi;
        this.iconUrl = iconUrl;
    }

    // Getters and Setters
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getFeelsLike() {
        return feelsLike;
    }

    public void setFeelsLike(double feelsLike) {
        this.feelsLike = feelsLike;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getCloudCoverage() {
        return cloudCoverage;
    }

    public void setCloudCoverage(int cloudCoverage) {
        this.cloudCoverage = cloudCoverage;
    }

    public double getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(double precipitation) {
        this.precipitation = precipitation;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getAqi() {
        return aqi;
    }

    public void setAqi(int aqi) {
        this.aqi = aqi;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    @Override
    public String toString() {
        return "WeatherData{" +
                "location='" + location + '\'' +
                ", temperature=" + temperature +
                ", feelsLike=" + feelsLike +
                ", condition='" + condition + '\'' +
                ", humidity=" + humidity +
                ", cloudCoverage=" + cloudCoverage +
                ", precipitation=" + precipitation +
                ", time='" + time + '\'' +
                ", aqi=" + aqi +
                ", iconUrl='" + iconUrl + '\'' +
                '}';
    }
}