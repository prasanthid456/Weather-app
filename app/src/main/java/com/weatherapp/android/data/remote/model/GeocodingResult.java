package com.weatherapp.android.data.remote.model;

import com.google.gson.annotations.SerializedName;

// one match from OpenWeatherMap's geocoding endpoint (geo/1.0/direct),
// used instead of the deprecated city-name lookup on /weather itself
public class GeocodingResult {

    @SerializedName("name") private String name;
    @SerializedName("lat") private double lat;
    @SerializedName("lon") private double lon;
    @SerializedName("country") private String country;
    @SerializedName("state") private String state;

    public GeocodingResult() {
        // Required by Gson.
    }

    /** Used by tests to build fixtures without going through Gson. */
    public GeocodingResult(String name, double lat, double lon, String country, String state) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.country = country;
        this.state = state;
    }

    public String getName() { return name; }
    public double getLat() { return lat; }
    public double getLon() { return lon; }
    public String getCountry() { return country; }
    public String getState() { return state; }
}
