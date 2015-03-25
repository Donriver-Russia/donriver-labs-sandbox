/**
 * Copyright DonRiver Inc. All Rights Reserved.
 * Created on: 23.03.15
 * Created by: Alexey Lugovoy
 */
package com.donriver.labs.server.sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ThreadPerConnectionsEchoServer {

    public static final String KILL_SERVER_COMMAND = "kill";
    public static final String KILL_CONNECTION_COMMAND = "bye";

    private static ServerSocket serverSocket;
    private static boolean terminated = false;

    public static void main(String[] args) throws IOException {
        try {
            serverSocket = new ServerSocket(8081);
            info("########## ThreadPerConnectionsEchoServer started successfully ############");
            while (!terminated) {
                Socket clientSocket = serverSocket.accept();
                new SocketHandlerThread(clientSocket).start();
            }
            info("########## ThreadPerConnectionsEchoServer gracefully stopped #############");
        } finally {
            shutdownQuietly();
        }

    }

    private static void handleClientSocket(Socket clientSocket) {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            info("###### Connection with " + getReadableClientAddress(clientSocket) + " established");
            writer.println("Welcome to ThreadPerConnectionsEchoServer");
            StringBuilder requestBuilder = new StringBuilder();
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {

                if (KILL_SERVER_COMMAND.equals(inputLine)) {
                    processServerStop(writer);
                    return;
                }

                if (KILL_CONNECTION_COMMAND.equals(inputLine)) {
                    processConnectionStop(writer);
                    break;
                }

                if (isItRequestBodyEnd(inputLine)) {
                    handleRequest(writer, requestBuilder.toString());
                    requestBuilder.setLength(0);
                } else {
                    requestBuilder.append(inputLine).append("\n");
                }
            }

        } catch (IOException ioe) {
            info("Handling failed. Due to " + ioe.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException ignore) {
            }
        }
    }

    private static String getReadableClientAddress(Socket clientSocket) {
        return clientSocket.getInetAddress().getCanonicalHostName() + ":" + clientSocket.getPort();
    }

    private static boolean isItRequestBodyEnd(String inputLine) {
        return inputLine.isEmpty();
    }

    private static void handleRequest(PrintWriter writer, String request) {
        info("[DEBUG] REQUEST: " + request);
        writer.println("[SUCCESSFULLY RECEIVED]: " + request);
    }

    private static void processConnectionStop(PrintWriter writer) {
        info("[RESET] Received stop session command");
        writer.println("Connection reset");
    }

    private static void processServerStop(PrintWriter writer) {
        info("[STOP] Received stop server command");
        writer.println("Server will stop after handling of all open connections");
        shutdownQuietly();
        terminated = true;
    }

    private static void shutdownQuietly() {
        try {
            if (serverSocket.isClosed()) {
                return;
            }
            serverSocket.close();
        } catch (IOException ignored) {
        }
    }

    private static void info(String message) {
        System.out.println("[" + getCurrentDate() + "]Thread#" + Thread.currentThread().getId() + ":" + message);
    }

    private static String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }


    private static class SocketHandlerThread extends Thread {
        private final Socket clientSocket;

        private SocketHandlerThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            handleClientSocket(clientSocket);
        }
    }
}
