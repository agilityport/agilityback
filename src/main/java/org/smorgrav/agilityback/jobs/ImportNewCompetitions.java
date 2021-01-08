package org.smorgrav.agilityback.jobs;

import org.smorgrav.agilityback.model.Competition;
import org.smorgrav.agilityback.sources.Source;
import org.smorgrav.agilityback.storage.Storage;
import org.smorgrav.agilityback.storage.StorageValues;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This is a job that is scheduled periodically to import new competition data from predefined sources.
 * <p>
 * In steady state this will only check the source ~2 times a day. There are other jobs that
 * maintains ongoing competitions and events.
 *
 * <p>
 * This is much easier than to update existing competitions :)
 */
public class ImportNewCompetitions {

    private static final Logger LOG = Logger.getLogger(ImportNewCompetitions.class.getName());

    public static void importNewCompetitions(Source source, Storage storage, int limit) throws IOException {

        // Read config - where to start importing from
        // TODO make config nested one more step and make it robust to sourcename changes
        StorageValues config = storage.readConfig(source.name().name());

        // Do most of the job
        importNewCompetitions(source, storage, config, limit);

        // Store back the new place to start from
        storage.writeConfig(config);
    }

    public static void importNewCompetitions(Source source, Storage storage, StorageValues config, int limit) throws IOException {
        LOG.info("Importing new competitions from " + source.name());

        if (!config.getBool("enabled", true)) {
            LOG.info("The sagik integration is not enabled according to config");
            return;
        }

        String fromStr = config.getString("from", LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        LocalDate from = LocalDate.parse(fromStr, DateTimeFormatter.ISO_DATE);

        // During initialization (i.e batch uploading new one
        // TODO add config for when we scan over more events
        // (when we have caught up we cmust be able to pick up new events that is registered not that far in the future)
        // Maybe also check new events oin the past
        LocalDate to = from.plusDays(30); //Combined with limit this is good enough

        LOG.info("Import upto one week from " + fromStr);

        // Load all competitions between these dates from db between from and to
        List<Competition> existingCompetitions = storage.read(source.name(), from, to);

        LOG.info("Got " + existingCompetitions.size() + " competitions from storage");

        // Gather all sourceIds and use this to detect new - if a source id has moved out of the date range
        // then this will create a duplicate entry - but let's handle that case as a maintenance task
        List<String> knownSourceIds = existingCompetitions.stream()
                .map(Competition::sourceIds)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        // Fetch all competitions between these dates from source
        List<Competition> sourceCompetitions = source.fetchCompetitions(from, to);

        LOG.info("Got " + sourceCompetitions.size() + " competitions from source");

        List<Competition> newCompetitions = sourceCompetitions.stream()
                .filter(competition -> Collections.disjoint(competition.sourceIds(), knownSourceIds))
                .collect(Collectors.toList());

        if (newCompetitions.size() > limit) {
            LOG.info("Found (limited)" + limit + " new competitions from " + source.name());
        } else {
            LOG.info("Found " + newCompetitions.size() + " new competitions from " + source.name());
        }

        // Fetch complete dataset for all new competitions
        newCompetitions.stream().limit(limit).forEach(competition -> {
            try {
                source.fetchEvents(competition);
                LOG.info("Found " + competition.events().size() + " events for sourceId " + String.join(",", competition.sourceIds()));
            } catch (IOException e) {
                LOG.warning("Unable to fetch new events from "
                        + source.name()
                        + " with sourceId "
                        + String.join(",", competition.sourceIds()));
            }

            competition.events().forEach(event -> {
                try {
                    source.fetchEquipages(competition, event);
                    LOG.info("Found " + event.equipages().size() + " equipages for eventId " + String.join(",", event.sourceId()));
                } catch (IOException e) {
                    LOG.warning("Unable to fetch new equipages from "
                            + source.name()
                            + " with sourceId "
                            + String.join(",", competition.sourceIds())
                            + " eventId "
                            + event.sourceId());
                }
            });

            LOG.info("Persisting new competition");
            storage.write(competition, true);
        });

        // Figure out which date we reached
        LocalDate newFrom = newCompetitions.stream().map(Competition::fromDate).max(LocalDate::compareTo).orElse(to);

        LOG.info("New from date: " + newFrom.format(DateTimeFormatter.ISO_DATE));

        // Write back where date we reached - to start where we come back
        config.values().put("from", newFrom.format(DateTimeFormatter.ISO_DATE));
    }
}
