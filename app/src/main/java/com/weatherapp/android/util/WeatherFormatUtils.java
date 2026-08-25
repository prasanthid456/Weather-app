package com.weatherapp.android.util;

import java.util.Locale;

/**
 * Formatting helpers shared by the Kotlin UI-mapping layer (WeatherUiMapper).
 * Deliberately plain static Java methods with no Android framework
 * dependency, so they're trivial to unit test directly and to call from
 * Kotlin as ordinary static functions.
 */
public final class WeatherFormatUtils {

    private static final String ICON_BASE_URL = "https://openweathermap.org/img/wn/";

    private WeatherFormatUtils() {
        // Static utility class — not instantiable.
    }

    /**
     * OpenWeatherMap returns whichever unit was requested; the app always
     * requests "imperial" (see OpenWeatherApiService), so this only ever
     * needs to format whole-degree Fahrenheit.
     */
    public static String formatTemperature(double degreesFahrenheit) {
        return Math.round(degreesFahrenheit) + "°F";
    }

    public static String formatWindSpeed(double milesPerHour) {
        return String.format(Locale.US, "%.0f mph", milesPerHour);
    }

    public static String capitalize(String description) {
        if (description == null || description.isEmpty()) {
            return description;
        }
        String[] words = description.split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            if (result.length() > 0) {
                result.append(' ');
            }
            result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return result.toString();
    }

    public static String buildIconUrl(String iconCode) {
        if (iconCode == null || iconCode.isEmpty()) {
            return null;
        }
        return ICON_BASE_URL + iconCode + "@2x.png";
    }
}
