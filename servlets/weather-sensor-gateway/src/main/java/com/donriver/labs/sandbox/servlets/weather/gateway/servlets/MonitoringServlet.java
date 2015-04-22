/**
 * Copyright DonRiver Inc. All Rights Reserved.
 * Created on: 08.04.15
 * Created by: Alexey Lugovoy
 */
package com.donriver.labs.sandbox.servlets.weather.gateway.servlets;

import com.donriver.labs.sandbox.servlets.weather.data.WeatherData;
import com.donriver.labs.sandbox.servlets.weather.gateway.storage.SensorDataStorage;
import com.donriver.labs.sandbox.servlets.weather.gateway.storage.impl.InMemorySensorDataStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Queue;

@WebServlet(name = "monitoringServlet", urlPatterns = {"/monitor/*"})
public class MonitoringServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(MonitoringServlet.class);

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String locationName = req.getPathInfo().substring(1);
        logger.debug("Requested locationName: " + locationName);
        ServletContext servletContext = req.getServletContext();
        Queue<WeatherData> data = retrieveData(servletContext, locationName);
        resp.setContentType("application/json");
        ServletOutputStream outputStream = resp.getOutputStream();
        uploadData(data, outputStream);
        outputStream.close();
        outputStream.flush();
    }

    private void uploadData(Queue<WeatherData> data, ServletOutputStream outputStream) throws IOException {
        if (data == null) {
            outputStream.print("{\"data\":null}");
            return;
        }
        outputStream.print("{\"data\":[");
        int i = 0;
        for (WeatherData dataItem : data) {
            uploadDataItem(outputStream, dataItem);
            if (i++ < data.size() - 1) {
                outputStream.print(",");
            }
        }
        outputStream.print("]}");
    }

    private void uploadDataItem(ServletOutputStream outputStream, WeatherData dataItem) throws IOException {
        outputStream.print("{\"timestamp\": \"" + dataItem.getTimestamp() + "\",");
        outputStream.print("\"temperature\":" + dataItem.getTemperature() + ",");
        outputStream.print("\"humidity\":" + dataItem.getHumidity() + ",");
        outputStream.print("\"windDirection\": \"" + dataItem.getWindDirection() + "\",");
        outputStream.print("\"windSpeed\":" + dataItem.getWindSpeed() + "}");
    }

    private Queue<WeatherData> retrieveData(ServletContext servletContext, String locationName) {
        SensorDataStorage dataStorage = (SensorDataStorage) servletContext.getAttribute(InMemorySensorDataStorage.CONTEXT_ATTR_NAME);
        return dataStorage.getDataForLocation(locationName);
    }
}
