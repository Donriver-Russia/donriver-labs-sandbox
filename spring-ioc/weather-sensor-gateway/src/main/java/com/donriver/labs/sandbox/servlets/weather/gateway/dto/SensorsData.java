/**
 * Copyright DonRiver Inc. All Rights Reserved.
 * Created on: 23.04.15
 * Created by: Alexey Lugovoy
 */
package com.donriver.labs.sandbox.servlets.weather.gateway.dto;

import com.donriver.labs.sandbox.servlets.weather.data.WeatherData;

import java.util.List;

public class SensorsData {

    private List<WeatherData> weatherDataList;

    public List<WeatherData> getWeatherDataList() {
        return weatherDataList;
    }

    public void setWeatherDataList(List<WeatherData> weatherDataList) {
        this.weatherDataList = weatherDataList;
    }
}
