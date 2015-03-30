package ru.sfedu.labs.httpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.log4j.Logger;

/**
 *
 * @author hacktivist
 */
public class HttpServer {

    private final Logger log = Logger.getLogger(this.getClass());

    public HttpServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log.info("########## MultyThreadHttpServer started successfully ############");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new SocketHandlerThread(clientSocket)).start();
            }
        } catch (IOException ex) {
            log.error("Can not start the server: "+ex.getMessage());
        }
    }
}
