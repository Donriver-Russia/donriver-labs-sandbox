/**
 * Copyright DonRiver Inc. All Rights Reserved.
 * Created on: 08.04.15
 * Created by: Alexey Lugovoy
 */
package com.donriver.labs.sandbox.servlets.weather.sensor.generators;

import com.donriver.labs.sandbox.servlets.weather.data.WeatherData;
import com.donriver.labs.sandbox.servlets.weather.data.WindDirection;

import java.util.Date;
import java.util.Random;

public class WeatherDataGenerator {
    private static final Random RANDOM = new Random();

    public static WeatherData generateRandom() {
        int tempreture = RANDOM.nextInt(50) - 20;
        int humidity = RANDOM.nextInt(100);
        int windSpeed = RANDOM.nextInt(50);
        WindDirection windDirection = WindDirection.values()[RANDOM.nextInt(WindDirection.values().length)];
        return build(tempreture, humidity, windSpeed, windDirection);
    }

    private static WeatherData build(int temperature, int humidity, int windSpeed, WindDirection windDirection) {
        WeatherData data = new WeatherData();
        data.setTemperature(temperature);
        data.setHumidity(humidity);
        data.setWindSpeed(windSpeed);
        data.setWindDirection(windDirection);
        data.setTimestamp(new Date());
        return data;
    }
}
