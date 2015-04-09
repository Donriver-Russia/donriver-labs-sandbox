/**
 * Copyright DonRiver Inc. All Rights Reserved.
 * Created on: 09.04.15
 * Created by: Alexey Lugovoy
 */
package com.donriver.labs.sandbox.servlets.weather.gateway.servlets;

import com.donriver.labs.sandbox.servlets.weather.gateway.listeners.SensorUpdatesDispatcher;
import com.donriver.labs.sandbox.servlets.weather.gateway.listeners.impl.SensorUpdateListenerImpl;
import com.donriver.labs.sandbox.servlets.weather.gateway.listeners.impl.SensorUpdatesDispatcherImpl;

import javax.servlet.AsyncContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "eventSendingServlet", asyncSupported = true, urlPatterns = {"/listen-updates"})
public class EventSendingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AsyncContext asyncContext = req.startAsync();
        resp.setContentType("text/event-stream;charset=UTF-8");
        resp.addHeader("Connection", "keep-alive");
        resp.addHeader("Cache-Control", "no-cache");
        ServletContext sc = req.getServletContext();
        ((SensorUpdatesDispatcher) sc.getAttribute(SensorUpdatesDispatcherImpl.CONTEXT_ATTR_NAME)).
                addListener(new SensorUpdateListenerImpl(asyncContext));
    }
}
