/**
 * Copyright DonRiver Inc. All Rights Reserved.
 * Created on: 26.03.15
 * Created by: Alexey Lugovoy
 */
package com.donriver.labs.server.sockets;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private String method;
    private String protocol;
    private String uri;
    private Map<String, String> headers = new HashMap<>();
    private String body;

    public HttpRequest(String method, String uri, String protocol) {
        this.method = method;
        this.protocol = protocol;
        this.uri = uri;
    }

    public String getMethod() {
        return method;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getUri() {
        return uri;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
