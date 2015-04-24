/**
 * Copyright DonRiver Inc. All Rights Reserved.
 * Created on: 23.04.15
 * Created by: Alexey Lugovoy
 */
package com.donriver.labs.sandbox.servlets.weather.gateway.controllers;

import com.donriver.labs.sandbox.servlets.weather.data.UpdateRequest;
import com.donriver.labs.sandbox.servlets.weather.gateway.storage.SensorDataStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller("gatewayController")
@RequestMapping("/sensor-readings")
public class GatewayController {

    @Autowired
    @Qualifier("inMemorySensorDataStorage")
    private SensorDataStorage sensorDataStorage;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void acceptSensorUpdate(@RequestBody UpdateRequest updateRequest) {
        sensorDataStorage.addData(updateRequest.getLocation(), updateRequest.getWeatherData());
    }
}
