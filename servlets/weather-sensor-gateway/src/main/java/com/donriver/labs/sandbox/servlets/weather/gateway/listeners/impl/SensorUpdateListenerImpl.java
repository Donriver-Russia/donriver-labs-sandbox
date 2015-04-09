/**
 * Copyright DonRiver Inc. All Rights Reserved.
 * Created on: 09.04.15
 * Created by: Alexey Lugovoy
 */
package com.donriver.labs.sandbox.servlets.weather.gateway.listeners.impl;

import com.donriver.labs.sandbox.servlets.weather.data.SensorLocation;
import com.donriver.labs.sandbox.servlets.weather.data.UpdateRequest;
import com.donriver.labs.sandbox.servlets.weather.data.WeatherData;
import com.donriver.labs.sandbox.servlets.weather.gateway.listeners.SensorUpdateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import java.io.IOException;

public class SensorUpdateListenerImpl implements SensorUpdateListener {

    private static final Logger logger = LoggerFactory.getLogger(SensorUpdateListenerImpl.class);

    private final AsyncContext listeningContext;
    private boolean listening = true;


    public SensorUpdateListenerImpl(AsyncContext listeningContext) {
        this.listeningContext = listeningContext;
    }

    @Override
    public synchronized void handleUpdate(UpdateRequest updateRequest) {
        try {
            ServletOutputStream os = listeningContext.getResponse().getOutputStream();
            os.print("data: " + convertToJson(updateRequest) + "\n\n");
            os.flush();
        } catch (IllegalStateException | IOException ise) {
            logger.error("Failed to notify listener", ise);
            //TODO listening should be handled another way!
            listening = false;
        }
    }

    private String convertToJson(UpdateRequest updateRequest) {
        SensorLocation location = updateRequest.getLocation();
        WeatherData weatherData = updateRequest.getWeatherData();
        return "{ \"locationName\":\"" + location.getLocationName() + "\"," +
                "\"latitude\": " + location.getLatitude() + "," +
                "\"longitude\": " + location.getLongitude() + "," +
                "\"weatherData\":{" +
                "\"temperature\": " + weatherData.getTemperature() + "," +
                "\"humidity\": " + weatherData.getHumidity() + "," +
                "\"windDirection\": \"" + weatherData.getWindDirection() + "\"," +
                "\"windSpeed\": " + weatherData.getWindSpeed() + "}}";
    }

    @Override
    public boolean isListening() {
        return listening;
    }
}
