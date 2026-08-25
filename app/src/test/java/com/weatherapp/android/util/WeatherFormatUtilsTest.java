package com.weatherapp.android.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class WeatherFormatUtilsTest {

    @Test
    public void formatTemperature_roundsToWholeDegree() {
        assertEquals("90°F", WeatherFormatUtils.formatTemperature(89.6));
    }

    @Test
    public void formatWindSpeed_formatsToWholeMph() {
        assertEquals("6 mph", WeatherFormatUtils.formatWindSpeed(5.75));
    }

    @Test
    public void capitalize_capitalizesEachWord() {
        assertEquals("Clear Sky", WeatherFormatUtils.capitalize("clear sky"));
    }

    @Test
    public void capitalize_handlesEmptyString() {
        assertEquals("", WeatherFormatUtils.capitalize(""));
    }

    @Test
    public void buildIconUrl_returnsFullUrl() {
        assertEquals("https://openweathermap.org/img/wn/01d@2x.png", WeatherFormatUtils.buildIconUrl("01d"));
    }

    @Test
    public void buildIconUrl_returnsNullForMissingCode() {
        assertNull(WeatherFormatUtils.buildIconUrl(null));
    }
}
