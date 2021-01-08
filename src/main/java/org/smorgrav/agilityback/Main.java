package org.smorgrav.agilityback;

import com.sun.net.httpserver.HttpServer;
import org.smorgrav.agilityback.httphandlers.CronHandler;
import org.smorgrav.agilityback.httphandlers.DefaultHandler;
import org.smorgrav.agilityback.httphandlers.RestApiHandler;
import org.smorgrav.agilityback.storage.FirestoreStorage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

public class Main {
    static Logger LOG = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Make sure we can connect to storage before we start
        FirestoreStorage storage = new FirestoreStorage();

        server.createContext("/api", new RestApiHandler(storage)); // Public api  - authenticated
        server.createContext("/cron", new CronHandler(storage));   // Cron tasks  - admin only
        server.createContext("/", new DefaultHandler());           // Default handler - public

        LOG.info("Starting HTTPServer");
        server.start();
    }
}
