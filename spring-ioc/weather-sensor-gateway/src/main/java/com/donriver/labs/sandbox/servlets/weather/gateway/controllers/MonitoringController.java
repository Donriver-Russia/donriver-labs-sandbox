/**
 * Copyright DonRiver Inc. All Rights Reserved.
 * Created on: 23.04.15
 * Created by: Alexey Lugovoy
 */
package com.donriver.labs.sandbox.servlets.weather.gateway.controllers;

import com.donriver.labs.sandbox.servlets.weather.data.WeatherData;
import com.donriver.labs.sandbox.servlets.weather.gateway.dto.SensorsData;
import com.donriver.labs.sandbox.servlets.weather.gateway.storage.SensorDataStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

@Controller("monitoringController")
@RequestMapping("/monitor")
public class MonitoringController {

    @Autowired
    private SensorDataStorage sensorDataStorage;

    @RequestMapping(value = "/{locationName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    SensorsData getData(@PathVariable String locationName) {
        Queue<WeatherData> dataForLocation = sensorDataStorage.getDataForLocation(locationName);
        List<WeatherData> weatherDataList = dataForLocation == null ? null : new ArrayList<>(dataForLocation);
        SensorsData data = new SensorsData();
        data.setWeatherDataList(weatherDataList);
        return data;
    }
}
