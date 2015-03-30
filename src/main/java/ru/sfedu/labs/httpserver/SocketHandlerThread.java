package ru.sfedu.labs.httpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author hacktivist
 */
public class SocketHandlerThread implements Runnable {

    private final Logger log = Logger.getLogger(this.getClass());

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public SocketHandlerThread(Socket socket) {
        this.socket = socket;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream());
        } catch (IOException ex) {
            log.error("Can't open stream: " + ex.getMessage());
        }

    }

    @Override
    public void run() {
        handleClientSocket();
    }

    private void handleClientSocket() {

        log.info("###### Connection with " + getReadableClientAddress(socket) + " established");
        boolean isAlive = true;
        while (isAlive) {
            try {
                HttpRequest request = tryInitiateRequest(reader.readLine());
                fillHeaders(request);
                if (request.getMethod().equals("POST") || request.getMethod().equals("PUT")) {
                    int contentLength = resolveContentLength(request.getHeaders());
                    request.setBody(readBody(contentLength));
                }

                writeEchoResponse(request);
            } catch (IOException ioe) {
                log.info("Handling failed. Due to " + ioe.getMessage());
                isAlive=false;
            } catch (IllegalArgumentException ignore) {
                log.info("Seems to be 400 status: " + ignore.getMessage());
                isAlive=false;
            }
        }
    }

    

    private int resolveContentLength(Map<String, String> headers) {
        String contentLengthStr = headers.get("Content-Length");
        if (contentLengthStr != null) {
            return Integer.parseInt(contentLengthStr);
        }

        contentLengthStr = headers.get("Content-length");
        if (contentLengthStr != null) {
            return Integer.parseInt(contentLengthStr);
        }

        send400Response("Cannot find content length header");
        throw new IllegalArgumentException("Cannot find content length header");
    }

    private void writeEchoResponse(HttpRequest request) {
        String responseMsg = "Protocol: " + request.getProtocol() + "\n"
                + "Method: " + request.getMethod() + "\n"
                + "URI: " + request.getUri() + "\n"
                + "Headers: " + request.getHeaders() + "\n"
                + "Body: " + request.getBody() + "\n";
        sendResponse("200 OK", responseMsg);
    }

    private String readBody(int contentLength) throws IOException {
        char[] body = new char[contentLength];
        reader.read(body);
        return new String(body);
    }

    private void fillHeaders(HttpRequest request) throws IOException {
        String headerLine;
        while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
            int separatorIndex = headerLine.indexOf(":");
            if (separatorIndex < 0) {
                send400Response("Header line incorrect: " + headerLine);
                throw new IllegalArgumentException();
            }
            request.getHeaders().put(headerLine.substring(0, separatorIndex).trim(), headerLine.substring(separatorIndex + 1).trim());
        }
    }

    private HttpRequest tryInitiateRequest(String firstReqLine) {
        String[] lineParts = firstReqLine.split(" ");
        if (lineParts.length != 3) {
            send400Response(firstReqLine);
            throw new IllegalArgumentException("Wrong first line");
        }
        return new HttpRequest(lineParts[0], lineParts[1], lineParts[2]);
    }

    private void send400Response(String requestMessage) {
        sendResponse("400 Bad Request", "Bad request: " + requestMessage);
    }

    private void sendResponse(String statusLine, String responseMessage) {
        String response = "HTTP/1.1 " + statusLine + "\n"
                + "Content-type: text/plain" + "\n"
                + "Transfer-Encoding: chunked" + "\n"
                + "Connection: keep-alive" + "\n"
                + "Server: MultyThreadHttpServer" + "\n"
                + "\n"
                + sendChunked(responseMessage);

        writer.println(response);
        writer.flush();
        log.info("sent response: " + response);
    }

    private String sendChunked(String response) {
        StringReader sreader = new StringReader(response);
        StringBuilder sb = new StringBuilder();
        char[] buff = new char[Constants.CHUNKED_BOUNDS];
        try {
            while (sreader.read(buff) > 0) {
                sb.append(Integer.toHexString(buff.length)).append("\n");
                sb.append(buff).append("\n");
            }
            sb.append("0\n");
        } catch (IOException ex) {
            log.error("IO exception: " + ex.getMessage());
        }
        return sb.toString();
    }

    private String getReadableClientAddress(Socket clientSocket) {
        return clientSocket.getInetAddress().getCanonicalHostName() + ":" + clientSocket.getPort();
    }
}
