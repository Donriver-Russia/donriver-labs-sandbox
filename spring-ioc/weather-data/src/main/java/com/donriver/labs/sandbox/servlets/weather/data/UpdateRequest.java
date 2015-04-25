/**
 * Copyright DonRiver Inc. All Rights Reserved.
 * Created on: 08.04.15
 * Created by: Alexey Lugovoy
 */
package com.donriver.labs.sandbox.servlets.weather.data;

import java.io.Serializable;

public class UpdateRequest implements Serializable {
    private SensorLocation location;
    private WeatherData weatherData;

    public SensorLocation getLocation() {
        return location;
    }

    public void setLocation(SensorLocation location) {
        this.location = location;
    }

    public WeatherData getWeatherData() {
        return weatherData;
    }

    public void setWeatherData(WeatherData weatherData) {
        this.weatherData = weatherData;
    }
}
