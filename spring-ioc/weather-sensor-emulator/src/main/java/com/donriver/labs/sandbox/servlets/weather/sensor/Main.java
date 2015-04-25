/**
 * Copyright DonRiver Inc. All Rights Reserved.
 * Created on: 08.04.15
 * Created by: Alexey Lugovoy
 */
package com.donriver.labs.sandbox.servlets.weather.sensor;

import com.donriver.labs.sandbox.servlets.weather.data.SensorLocation;
import com.donriver.labs.sandbox.servlets.weather.sensor.client.ClientFactory;
import com.donriver.labs.sandbox.servlets.weather.sensor.client.GatewayClient;
import com.donriver.labs.sandbox.servlets.weather.sensor.client.impl.ClientFactoryImpl;
import com.donriver.labs.sandbox.servlets.weather.sensor.generators.SensorLocationGenerator;
import com.donriver.labs.sandbox.servlets.weather.sensor.generators.WeatherDataGenerator;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        String baseUrl = args[0];
        ClientFactory clientFactory = new ClientFactoryImpl(baseUrl);
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(SensorLocationGenerator.LOCATIONS.length);
        for (SensorLocation sensorLocation : SensorLocationGenerator.LOCATIONS) {
            final GatewayClient gatewayClient = clientFactory.createClient(sensorLocation);
            executorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    gatewayClient.sendData(WeatherDataGenerator.generateRandom());
                }
            }, 0, 10, TimeUnit.SECONDS);
            Thread.sleep(1000);
        }
    }
}
