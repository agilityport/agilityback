package org.smorgrav.agilityback.httphandlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class HttpUtils {

    static Logger LOG = Logger.getLogger(HttpUtils.class.getName());

    public static void writeResponse(int code, String responseStr, HttpExchange exchange) {
        try {
            byte[] response = responseStr.getBytes();
            exchange.sendResponseHeaders(code, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
                os.flush();
            }
        } catch (IOException e) {
            // Not much we can do here
            LOG.warning(e.getMessage());
        }
    }

    public static void writeError(Throwable throwable, HttpExchange exchange) {
        HttpUtils.writeResponse(500, Arrays.stream(throwable.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n")), exchange);
    }

    public static void writeNotFound(HttpExchange exchange) {
        HttpUtils.writeResponse(404, "Nothing here I'm afraid", exchange);
    }
}
