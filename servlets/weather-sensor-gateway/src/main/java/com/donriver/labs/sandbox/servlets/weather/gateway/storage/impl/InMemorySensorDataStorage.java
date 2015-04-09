/**
 * Copyright DonRiver Inc. All Rights Reserved.
 * Created on: 08.04.15
 * Created by: Alexey Lugovoy
 */
package com.donriver.labs.sandbox.servlets.weather.gateway.storage.impl;

import com.donriver.labs.sandbox.servlets.weather.data.SensorLocation;
import com.donriver.labs.sandbox.servlets.weather.data.WeatherData;
import com.donriver.labs.sandbox.servlets.weather.gateway.storage.SensorDataStorage;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

@WebListener("sensorDataContextInitializer")
public class InMemorySensorDataStorage implements ServletContextListener, SensorDataStorage {

    public static final String CONTEXT_ATTR_NAME = "InMemorySensorDataContext";
    private static final int DATA_CAPACITY = 10;

    private ConcurrentHashMap<String, Deque<WeatherData>> locationsData = new ConcurrentHashMap<>();

    public Queue<WeatherData> getDataForLocation(String locationName) {
        return locationsData.get(locationName);
    }

    public void addData(SensorLocation sensorLocation, WeatherData weatherData) {
        Deque<WeatherData> datas = locationsData.get(sensorLocation.getLocationName());
        if (datas == null) {
            datas = new LinkedList<>();
            locationsData.put(sensorLocation.getLocationName(), datas);
        }
        while (datas.size() >= DATA_CAPACITY) {
            datas.removeFirst();
        }
        datas.add(weatherData);
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        sce.getServletContext().setAttribute(CONTEXT_ATTR_NAME, this);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
