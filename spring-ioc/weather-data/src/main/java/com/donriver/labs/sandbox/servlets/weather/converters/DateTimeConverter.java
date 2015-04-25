/**
 * Copyright DonRiver Inc. All Rights Reserved.
 * Created on: 23.04.15
 * Created by: Alexey Lugovoy
 */
package com.donriver.labs.sandbox.servlets.weather.converters;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;

public class DateTimeConverter extends JsonDeserializer<Date> {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("MMM d, yyyy hh:mm:ss a").withLocale(Locale
            .CANADA);

    public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String date = jp.getText();
        return DATE_TIME_FORMATTER.parseDateTime(date).toDate();
    }

}
