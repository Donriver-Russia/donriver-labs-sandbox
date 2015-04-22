/**
 * Copyright DonRiver Inc. All Rights Reserved.
 * Created on: 09.04.15
 * Created by: Alexey Lugovoy
 */
package com.donriver.labs.sandbox.servlets.weather.gateway.listeners;

import com.donriver.labs.sandbox.servlets.weather.data.UpdateRequest;

public interface SensorUpdatesDispatcher {

    void addListener(SensorUpdateListener listener);

    void notifyListeners(UpdateRequest request);
}
