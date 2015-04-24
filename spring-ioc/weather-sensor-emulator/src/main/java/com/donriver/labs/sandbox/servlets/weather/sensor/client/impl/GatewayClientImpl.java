/**
 * Copyright DonRiver Inc. All Rights Reserved.
 * Created on: 08.04.15
 * Created by: Alexey Lugovoy
 */
package com.donriver.labs.sandbox.servlets.weather.sensor.client.impl;

import com.donriver.labs.sandbox.servlets.weather.data.SensorLocation;
import com.donriver.labs.sandbox.servlets.weather.data.UpdateRequest;
import com.donriver.labs.sandbox.servlets.weather.data.WeatherData;
import com.donriver.labs.sandbox.servlets.weather.sensor.client.GatewayClient;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GatewayClientImpl implements GatewayClient {

    private static final Logger logger = LoggerFactory.getLogger(GatewayClientImpl.class);

    private final SensorLocation location;
    private final HttpClient httpClient;
    private final String baseUrl;

    private static final Gson gson = new Gson();

    public GatewayClientImpl(SensorLocation location, HttpClient httpClient, String baseUrl) {
        this.location = location;
        this.baseUrl = baseUrl;
        this.httpClient = httpClient;
    }

    @Override
    public void register() {
        //TBD
    }

    @Override
    public void sendData(WeatherData weatherData) {
        try {
            httpClient.execute(prepareUpdateRequest(weatherData), new ResponseHandler<Void>() {
                @Override
                public Void handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
                    logger.info(getLocationStr() + ": data sent. Received response with status " + httpResponse.getStatusLine());
                    return null;
                }
            });
        } catch (IOException e) {
            logger.error(getLocationStr() + ":Failed to send data", e);
        }
    }

    private HttpUriRequest prepareUpdateRequest(WeatherData weatherData) throws IOException {
        HttpPost request = new HttpPost(baseUrl + "/sensor-readings");
        UpdateRequest gatewayRequest = createGatewayRequest(weatherData);
        request.addHeader("Content-type", "application/json");
        request.addHeader("Accept", "application/json");
        String rqStr = gson.toJson(gatewayRequest);
        if (logger.isDebugEnabled()) {
            logger.debug("Going to send request: " + rqStr);
        }
        request.setEntity(new StringEntity(rqStr));
        return request;
    }

    private UpdateRequest createGatewayRequest(WeatherData weatherData) {
        UpdateRequest gatewayRequest = new UpdateRequest();
        gatewayRequest.setLocation(location);
        gatewayRequest.setWeatherData(weatherData);
        return gatewayRequest;
    }

    private String getLocationStr() {
        return location.getLocationName() + "[" + location.getLatitude() + "," + location.getLongitude() + "]";
    }
}
