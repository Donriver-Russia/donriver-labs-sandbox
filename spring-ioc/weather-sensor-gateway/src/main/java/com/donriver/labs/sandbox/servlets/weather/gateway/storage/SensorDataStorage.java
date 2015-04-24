/**
 * Copyright DonRiver Inc. All Rights Reserved.
 * Created on: 08.04.15
 * Created by: Alexey Lugovoy
 */
package com.donriver.labs.sandbox.servlets.weather.gateway.storage;

import com.donriver.labs.sandbox.servlets.weather.data.SensorLocation;
import com.donriver.labs.sandbox.servlets.weather.data.WeatherData;

import java.util.Queue;

public interface SensorDataStorage {

    Queue<WeatherData> getDataForLocation(String locationName);

    void addData(SensorLocation sensorLocation, WeatherData weatherData);
}
