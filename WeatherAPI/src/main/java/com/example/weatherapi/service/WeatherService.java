package com.example.weatherapi.service;

import com.example.weatherapi.model.WeatherData;
import com.example.weatherapi.model.ForecastData;
import com.example.weatherapi.model.HistoricalData;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for making API calls to WeatherAPI.com
 * Handles HTTP requests and JSON parsing
 */
public class WeatherService {

    // Replace with your actual API key from weatherapi.com
    private static final String API_KEY = "Your API KEY over here";
    private static final String BASE_URL = "http://api.weatherapi.com/v1";

    private final HttpClient httpClient;
    private final Gson gson;

    public WeatherService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    /**
     * Get current weather for a specific city
     */
    public WeatherData getCurrentWeather(String city) throws IOException, InterruptedException {
        String url = String.format("%s/current.json?key=%s&q=%s&aqi=yes", BASE_URL, API_KEY, city);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to fetch weather data. Status: " + response.statusCode());
        }

        return parseCurrentWeather(response.body());
    }

    /**
     * Get weather forecast for specified number of days
     */
    public List<ForecastData> getForecast(String city, int days) throws IOException, InterruptedException {
        String url = String.format("%s/forecast.json?key=%s&q=%s&days=%d&aqi=no&alerts=no",
                BASE_URL, API_KEY, city, days);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to fetch forecast data. Status: " + response.statusCode());
        }

        return parseForecast(response.body());
    }

    /**
     * Get historical weather data for the past specified number of days
     */
    public List<HistoricalData> getHistoricalWeather(String city, int days) throws IOException, InterruptedException {
        List<HistoricalData> historicalList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = 1; i <= days; i++) {
            LocalDate date = LocalDate.now().minusDays(i);
            String dateStr = date.format(formatter);

            String url = String.format("%s/history.json?key=%s&q=%s&dt=%s",
                    BASE_URL, API_KEY, city, dateStr);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                HistoricalData historical = parseHistoricalWeather(response.body());
                historicalList.add(historical);
            }
        }

        return historicalList;
    }

    private WeatherData parseCurrentWeather(String jsonResponse) {
        JsonObject json = gson.fromJson(jsonResponse, JsonObject.class);
        JsonObject location = json.getAsJsonObject("location");
        JsonObject current = json.getAsJsonObject("current");
        JsonObject condition = current.getAsJsonObject("condition");
        JsonObject airQuality = current.getAsJsonObject("air_quality");

        WeatherData weather = new WeatherData();
        weather.setLocation(location.get("name").getAsString() + ", " + location.get("country").getAsString());
        weather.setTemperature(current.get("temp_c").getAsDouble());
        weather.setFeelsLike(current.get("feelslike_c").getAsDouble());
        weather.setCondition(condition.get("text").getAsString());
        weather.setHumidity(current.get("humidity").getAsInt());
        weather.setCloudCoverage(current.get("cloud").getAsInt());
        weather.setPrecipitation(current.get("precip_mm").getAsDouble());
        weather.setTime(current.get("last_updated").getAsString());
        weather.setIconUrl(condition.get("icon").getAsString());

        // Air Quality Index (if available)
        if (airQuality != null && airQuality.has("us-epa-index")) {
            weather.setAqi(airQuality.get("us-epa-index").getAsInt());
        } else {
            weather.setAqi(0);
        }

        return weather;
    }

    private List<ForecastData> parseForecast(String jsonResponse) {
        List<ForecastData> forecastList = new ArrayList<>();
        JsonObject json = gson.fromJson(jsonResponse, JsonObject.class);
        JsonObject forecast = json.getAsJsonObject("forecast");
        JsonArray forecastdays = forecast.getAsJsonArray("forecastday");

        for (JsonElement dayElement : forecastdays) {
            JsonObject day = dayElement.getAsJsonObject();
            JsonObject dayData = day.getAsJsonObject("day");
            JsonObject condition = dayData.getAsJsonObject("condition");

            ForecastData forecastData = new ForecastData();
            forecastData.setDate(day.get("date").getAsString());
            forecastData.setMaxTemp(dayData.get("maxtemp_c").getAsDouble());
            forecastData.setMinTemp(dayData.get("mintemp_c").getAsDouble());
            forecastData.setCondition(condition.get("text").getAsString());
            forecastData.setIconUrl(condition.get("icon").getAsString());
            forecastData.setPrecipitation(dayData.get("totalprecip_mm").getAsDouble());

            forecastList.add(forecastData);
        }

        return forecastList;
    }

    private HistoricalData parseHistoricalWeather(String jsonResponse) {
        JsonObject json = gson.fromJson(jsonResponse, JsonObject.class);
        JsonObject forecast = json.getAsJsonObject("forecast");
        JsonArray forecastdays = forecast.getAsJsonArray("forecastday");
        JsonObject day = forecastdays.get(0).getAsJsonObject();
        JsonObject dayData = day.getAsJsonObject("day");
        JsonObject condition = dayData.getAsJsonObject("condition");

        HistoricalData historical = new HistoricalData();
        historical.setDate(day.get("date").getAsString());
        historical.setMaxTemp(dayData.get("maxtemp_c").getAsDouble());
        historical.setMinTemp(dayData.get("mintemp_c").getAsDouble());
        historical.setCondition(condition.get("text").getAsString());
        historical.setPrecipitation(dayData.get("totalprecip_mm").getAsDouble());

        return historical;
    }
}
