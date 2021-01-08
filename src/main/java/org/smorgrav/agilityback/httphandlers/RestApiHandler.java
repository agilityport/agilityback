package org.smorgrav.agilityback.httphandlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.smorgrav.agilityback.jobs.ImportNewCompetitions;
import org.smorgrav.agilityback.sources.sagik.SagikSource;
import org.smorgrav.agilityback.storage.Storage;

import java.io.IOException;
import java.util.logging.Logger;

public class RestApiHandler implements HttpHandler {

    private final static Logger LOG = Logger.getLogger(RestApiHandler.class.getName());
    private final Storage storage;

    public RestApiHandler(Storage storage) {
        this.storage = storage;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            PathMatcher path = new PathMatcher(exchange.getRequestURI());
            LOG.info("Handling restapi call to: " + exchange.getRequestURI().getPath());
            if (path.match("/api")) {
                HttpUtils.writeResponse(200, "Rest api root", exchange);
            } else if (path.match("/api/import/competition/source/sagik")) {
                importSagik(exchange);
                HttpUtils.writeResponse(200, "Done", exchange);
            } else {
                HttpUtils.writeNotFound(exchange);
            }
        } catch (Throwable t) {
            HttpUtils.writeError(t, exchange);
        }

        HttpUtils.writeResponse(200, "Done", exchange);
    }

    private void importSagik(HttpExchange exchange) {
        LOG.info("Import sources from sagik");
        try {
            ImportNewCompetitions.importNewCompetitions(new SagikSource(), storage, 2);
        } catch (IOException e) {
            LOG.warning("Failed to import sources from sagik");
            LOG.warning(e.getMessage());
            HttpUtils.writeError(e, exchange);
        }
    }
}
