package com.weatherapp.android.data.remote.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Raw response model for OpenWeatherMap's /data/2.5/weather endpoint.
 * Written in Java (the rest of the app is Kotlin) to demonstrate Java/Kotlin
 * interop within the same module: Kotlin code (WeatherUiMapper) consumes
 * this class directly, and its getters read as ordinary Kotlin properties
 * (response.main.temp) without any wrapper.
 */
public class CurrentWeatherResponse {

    @SerializedName("coord")
    private Coord coord;

    @SerializedName("weather")
    private List<WeatherCondition> weather;

    @SerializedName("main")
    private MainMetrics main;

    @SerializedName("wind")
    private Wind wind;

    @SerializedName("name")
    private String name;

    @SerializedName("sys")
    private Sys sys;

    @SerializedName("dt")
    private long dt;

    public Coord getCoord() {
        return coord;
    }

    public List<WeatherCondition> getWeather() {
        return weather;
    }

    public MainMetrics getMain() {
        return main;
    }

    public Wind getWind() {
        return wind;
    }

    public String getName() {
        return name;
    }

    public Sys getSys() {
        return sys;
    }

    public long getDt() {
        return dt;
    }

    public static class Coord {
        @SerializedName("lon") private double lon;
        @SerializedName("lat") private double lat;

        public double getLon() { return lon; }
        public double getLat() { return lat; }
    }

    public static class WeatherCondition {
        @SerializedName("id") private int id;
        @SerializedName("main") private String main;
        @SerializedName("description") private String description;
        @SerializedName("icon") private String icon;

        public int getId() { return id; }
        public String getMain() { return main; }
        public String getDescription() { return description; }
        public String getIcon() { return icon; }
    }

    public static class MainMetrics {
        @SerializedName("temp") private double temp;
        @SerializedName("feels_like") private double feelsLike;
        @SerializedName("temp_min") private double tempMin;
        @SerializedName("temp_max") private double tempMax;
        @SerializedName("pressure") private int pressure;
        @SerializedName("humidity") private int humidity;

        public double getTemp() { return temp; }
        public double getFeelsLike() { return feelsLike; }
        public double getTempMin() { return tempMin; }
        public double getTempMax() { return tempMax; }
        public int getPressure() { return pressure; }
        public int getHumidity() { return humidity; }
    }

    public static class Wind {
        @SerializedName("speed") private double speed;
        @SerializedName("deg") private double deg;

        public double getSpeed() { return speed; }
        public double getDeg() { return deg; }
    }

    public static class Sys {
        @SerializedName("country") private String country;
        @SerializedName("sunrise") private long sunrise;
        @SerializedName("sunset") private long sunset;

        public String getCountry() { return country; }
        public long getSunrise() { return sunrise; }
        public long getSunset() { return sunset; }
    }
}
