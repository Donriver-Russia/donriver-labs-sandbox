/**
 * Copyright DonRiver Inc. All Rights Reserved.
 * Created on: 23.03.15
 * Created by: Alexey Lugovoy
 */
package com.donriver.labs.server.sockets;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class SingleThreadHttpServer {

    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(Integer.valueOf(args[0]))) {
            info("########## SingleThreadHttpServer started successfully ############");
            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    handleClientSocket(clientSocket);
                }
            }
        }

    }

    private static void handleClientSocket(Socket clientSocket) {
        try (
                InputStream is = clientSocket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            info("###### Connection with " + getReadableClientAddress(clientSocket) + " established");

            HttpRequest request = tryInitiateRequest(reader.readLine(), writer);
            fillHeaders(reader, writer, request);
            if (request.getMethod().equals("POST") || request.getMethod().equals("PUT")) {
                int contentLength = resolveContentLength(request.getHeaders(), writer);
                request.setBody(readBody(reader, contentLength));
            }

            writeEchoResponse(request, writer);
        } catch (IOException ioe) {
            info("Handling failed. Due to " + ioe.getMessage());
        } catch (IllegalArgumentException ignore) {
            info("Seems to be 400 status: " + ignore.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException ignore) {
            }
        }
    }

    private static int resolveContentLength(Map<String, String> headers, PrintWriter writer) {
        String contentLengthStr = headers.get("Content-Length");
        if (contentLengthStr != null) {
            return Integer.parseInt(contentLengthStr);
        }

        contentLengthStr = headers.get("Content-length");
        if (contentLengthStr != null) {
            return Integer.parseInt(contentLengthStr);
        }

        send400Response(writer, "Cannot find content length header");
        throw new IllegalArgumentException("Cannot find content length header");
    }

    private static void writeEchoResponse(HttpRequest request, PrintWriter writer) {
        String responseMsg = "Protocol: " + request.getProtocol() + "\n" +
                "Method: " + request.getMethod() + "\n" +
                "URI: " + request.getUri() + "\n" +
                "Headers: " + request.getHeaders() + "\n" +
                "Body: " + request.getBody() + "\n";
        sendResponse("200 OK", writer, responseMsg);
    }

    private static String readBody(BufferedReader reader, int contentLength) throws IOException {
        char[] body = new char[contentLength];
        reader.read(body);
        return new String(body);
    }

    private static void fillHeaders(BufferedReader reader, PrintWriter writer, HttpRequest request) throws IOException {
        String headerLine;
        while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
            int separatorIndex = headerLine.indexOf(":");
            if (separatorIndex < 0) {
                send400Response(writer, "Header line incorrect: " + headerLine);
                throw new IllegalArgumentException();
            }
            request.getHeaders().put(headerLine.substring(0, separatorIndex).trim(), headerLine.substring(separatorIndex + 1).trim());
        }
    }

    private static HttpRequest tryInitiateRequest(String firstReqLine, PrintWriter writer) {
        String[] lineParts = firstReqLine.split(" ");
        if (lineParts.length != 3) {
            send400Response(writer, firstReqLine);
            throw new IllegalArgumentException("Wrong first line");
        }
        return new HttpRequest(lineParts[0], lineParts[1], lineParts[2]);
    }

    private static void send400Response(PrintWriter writer, String requestMessage) {
        sendResponse("400 Bad Request", writer, "Bad request: " + requestMessage);
    }

    private static void sendResponse(String statusLine, PrintWriter writer, String responseMessage) {
        String response = "HTTP/1.0 " + statusLine + "\n" +
                "Content-type: text/plain" + "\n" +
                "Content-length: " + responseMessage.length() + "\n" +
                "Connection: close" + "\n" +
                "Server: SingleThreadHttpServer" + "\n" +
                "\n" +
                responseMessage;
        writer.println(response);
        info("sent response: " + response);
    }

    private static String getReadableClientAddress(Socket clientSocket) {
        return clientSocket.getInetAddress().getCanonicalHostName() + ":" + clientSocket.getPort();
    }

    private static void info(String message) {
        System.out.println("[" + getCurrentDate() + "]" + message);
    }

    private static String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
