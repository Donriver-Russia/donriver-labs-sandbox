/**
 * Copyright DonRiver Inc. All Rights Reserved.
 * Created on: 08.04.15
 * Created by: Alexey Lugovoy
 */
package com.donriver.labs.sandbox.servlets.weather.sensor.client;

import com.donriver.labs.sandbox.servlets.weather.data.SensorLocation;

public interface ClientFactory {
    GatewayClient createClient(SensorLocation sensorLocation);
}
