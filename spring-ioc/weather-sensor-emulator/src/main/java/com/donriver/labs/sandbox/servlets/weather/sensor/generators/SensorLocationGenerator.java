/**
 * Copyright DonRiver Inc. All Rights Reserved.
 * Created on: 08.04.15
 * Created by: Alexey Lugovoy
 */
package com.donriver.labs.sandbox.servlets.weather.sensor.generators;

import com.donriver.labs.sandbox.servlets.weather.data.SensorLocation;

import java.util.Random;

public class SensorLocationGenerator {

    private static final Random RANDOM = new Random();

    public static final SensorLocation[] LOCATIONS = new SensorLocation[]{
            build("Rostov-on-Don", 0, 0),
            build("Salsk", 0, 0),
            build("Belaya Glina", 0, 0),
            build("Millerovo", 0, 0),
            build("Shahty", 0, 0),
            build("Volgodonsk", 0, 0)
    };

    public static SensorLocation generate() {
        return LOCATIONS[RANDOM.nextInt(LOCATIONS.length)];
    }

    private static SensorLocation build(String locationName, int longitude, int latitude) {
        SensorLocation sensorLocation = new SensorLocation();
        sensorLocation.setLatitude(latitude);
        sensorLocation.setLocationName(locationName);
        sensorLocation.setLongitude(longitude);
        return sensorLocation;
    }
}
