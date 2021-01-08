package org.smorgrav.agilityback.httphandlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.smorgrav.agilityback.jobs.ImportNewCompetitions;
import org.smorgrav.agilityback.sources.sagik.SagikSource;
import org.smorgrav.agilityback.storage.Storage;

import java.io.IOException;
import java.util.logging.Logger;

public class CronHandler implements HttpHandler {
    static Logger LOG = Logger.getLogger(CronHandler.class.getName());

    private final Storage storage;

    public CronHandler(Storage storage) {
        this.storage = storage;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try {
            PathMatcher path = new PathMatcher(exchange.getRequestURI());
            if (path.match("/cron")) {
                HttpUtils.writeResponse(200, "Hello World!", exchange);
            }
            if (path.match("/cron/import")) {
                try {
                    ImportNewCompetitions.importNewCompetitions(new SagikSource(), storage, 10);
                    HttpUtils.writeResponse(200, "Done", exchange);
                } catch (IOException e) {
                    LOG.warning("Failed to import sources from sagik");
                    LOG.warning(e.getMessage());
                    HttpUtils.writeError(e, exchange);
                }
            } else {
                HttpUtils.writeNotFound(exchange);
            }
        } catch (Throwable t) {
            HttpUtils.writeError(t, exchange);
        }
    }
}
