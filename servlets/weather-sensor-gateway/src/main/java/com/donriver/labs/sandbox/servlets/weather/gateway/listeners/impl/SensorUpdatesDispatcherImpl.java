/**
 * Copyright DonRiver Inc. All Rights Reserved.
 * Created on: 09.04.15
 * Created by: Alexey Lugovoy
 */
package com.donriver.labs.sandbox.servlets.weather.gateway.listeners.impl;

import com.donriver.labs.sandbox.servlets.weather.data.UpdateRequest;
import com.donriver.labs.sandbox.servlets.weather.gateway.listeners.SensorUpdateListener;
import com.donriver.labs.sandbox.servlets.weather.gateway.listeners.SensorUpdatesDispatcher;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

@WebListener("sensorUpdatesDispatcherImpl")
public class SensorUpdatesDispatcherImpl implements SensorUpdatesDispatcher, ServletContextListener {
    public static final String CONTEXT_ATTR_NAME = "SensorUpdatesDispatcherImpl";

    private Collection<SensorUpdateListener> sensorUpdateListeners = new LinkedList<>();

    @Override
    public void addListener(SensorUpdateListener listener) {
        sensorUpdateListeners.add(listener);
    }

    @Override
    public void notifyListeners(UpdateRequest request) {
        for (Iterator<SensorUpdateListener> listenerIterator = sensorUpdateListeners.iterator(); listenerIterator.hasNext(); ) {
            SensorUpdateListener listener = listenerIterator.next();
            if (!listener.isListening()) {
                listenerIterator.remove();
                continue;
            }
            listener.handleUpdate(request);
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        sc.setAttribute(CONTEXT_ATTR_NAME, this);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
