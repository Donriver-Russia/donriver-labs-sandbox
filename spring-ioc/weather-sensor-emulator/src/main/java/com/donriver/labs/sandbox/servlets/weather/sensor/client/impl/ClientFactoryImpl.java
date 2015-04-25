/**
 * Copyright DonRiver Inc. All Rights Reserved.
 * Created on: 08.04.15
 * Created by: Alexey Lugovoy
 */
package com.donriver.labs.sandbox.servlets.weather.sensor.client.impl;

import com.donriver.labs.sandbox.servlets.weather.data.SensorLocation;
import com.donriver.labs.sandbox.servlets.weather.sensor.client.ClientFactory;
import com.donriver.labs.sandbox.servlets.weather.sensor.client.GatewayClient;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class ClientFactoryImpl implements ClientFactory {

    private final HttpClient httpClient;
    private final String baseUrl;

    public ClientFactoryImpl(String baseUrl) {
        this.baseUrl = baseUrl;

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setDefaultMaxPerRoute(100);
        cm.setMaxTotal(200);
        httpClient = HttpClients.custom().setConnectionManager(cm).build();
    }

    @Override
    public GatewayClient createClient(SensorLocation sensorLocation) {
        return new GatewayClientImpl(sensorLocation, httpClient, baseUrl);
    }
}
