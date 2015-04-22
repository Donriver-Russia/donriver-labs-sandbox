/**
 * Copyright DonRiver Inc. All Rights Reserved.
 * Created on: 08.04.15
 * Created by: Alexey Lugovoy
 */
package com.donriver.labs.sandbox.servlets.weather.gateway.servlets;

import com.donriver.labs.sandbox.servlets.weather.data.SensorLocation;
import com.donriver.labs.sandbox.servlets.weather.data.UpdateRequest;
import com.donriver.labs.sandbox.servlets.weather.data.WeatherData;
import com.donriver.labs.sandbox.servlets.weather.gateway.storage.SensorDataStorage;
import com.donriver.labs.sandbox.servlets.weather.gateway.storage.impl.InMemorySensorDataStorage;
import com.donriver.labs.sandbox.servlets.weather.gateway.listeners.impl.SensorUpdatesDispatcherImpl;
import com.donriver.labs.sandbox.servlets.weather.gateway.listeners.SensorUpdatesDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.ObjectInputStream;

@WebServlet(name = "gatewayServlet", urlPatterns = {"/sensor-readings"})
public class GatewayServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(GatewayServlet.class);

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ObjectInputStream ois = new ObjectInputStream(req.getInputStream());
        try {
            UpdateRequest updateRequest = (UpdateRequest) ois.readObject();
            logRequest(updateRequest);
            ServletContext servletContext = req.getServletContext();
            addNewRequestToStorage(updateRequest, servletContext);
            notifyListeners(updateRequest, servletContext);
        } catch (ClassNotFoundException e) {
            logger.error("Unexpected class.", e);
            resp.setStatus(400);
        }
        resp.getOutputStream().close();
    }

    private void notifyListeners(UpdateRequest updateRequest, ServletContext servletContext) {
        ((SensorUpdatesDispatcher) servletContext.getAttribute(SensorUpdatesDispatcherImpl.CONTEXT_ATTR_NAME)).
                notifyListeners(updateRequest);
    }

    private void logRequest(UpdateRequest updateRequest) {
        if (!logger.isDebugEnabled()) {
            return;
        }
        SensorLocation sensorLocation = updateRequest.getLocation();
        WeatherData data = updateRequest.getWeatherData();
        logger.debug("Received: " + sensorLocation.getLocationName() + "[" + sensorLocation.getLongitude() + "," + sensorLocation
                        .getLatitude() + "]{" +
                        "\n\t reading timestamp: " + data.getTimestamp() +
                        "\n\t temperature: " + data.getTemperature() +
                        "\n\t humidity: " + data.getHumidity() +
                        "\n\t wind[" + data.getWindDirection() + "] speed: " + data.getWindSpeed() +
                        "\n}"
        );
    }

    private void addNewRequestToStorage(UpdateRequest updateRequest, ServletContext servletContext) {
        ((SensorDataStorage) servletContext.getAttribute(InMemorySensorDataStorage.CONTEXT_ATTR_NAME)).
                addData(updateRequest.getLocation(), updateRequest.getWeatherData());
    }
}
