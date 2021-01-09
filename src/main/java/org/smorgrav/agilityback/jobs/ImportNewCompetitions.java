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
 * will maintain ongoing competitions and events.
 *
 * <p>
 * This is much easier than to update existing competitions :)
 */
public class ImportNewCompetitions {

    private static final Logger LOG = Logger.getLogger(ImportNewCompetitions.class.getName());

    public static void importNewCompetitions(Source source, Storage storage, int limit) throws IOException {
        LOG.info("Importing new competitions from " + source.name());

        // Read config - require existing config to run
        StorageValues config = storage.readConfig(source.configId());
        if (!config.isValid()) {
            LOG.info("No config found for configId " + source.configId() + ". Aborting");
            return;
        }

        // Do most of the job - config will be potentially updated as a side effect
        importNewCompetitions(source, storage, config, limit);

        // Store back the new place to start from
        storage.writeConfig(config);
    }

    static void importNewCompetitions(Source source, Storage storage, StorageValues config, int limit) throws IOException {

        if (!config.getBool("enabled", false)) {
            LOG.info(String.format("The %s source is not enabled according to config", source.name()));
            return;
        }

        boolean isCatchupMode = config.getBool("catchup", false);
        LOG.info(String.format("The %s source is in %s", source.name(), isCatchupMode ? "catchup mode" : " steady state"));

        LocalDate from = LocalDate.now().minusMonths(1);
        LocalDate to = LocalDate.now().plusDays(1);

        // In catchup mode we potentially have to look far in the past
        if (isCatchupMode) {
            from = config.getISODate("catchupfrom", LocalDate.now());
            to = from.plusDays(config.getLong("catchupdays", 30L));
        }

        LOG.info(String.format("Looking for new sources between %s and %s ", from.format(DateTimeFormatter.ISO_DATE),
                to.format(DateTimeFormatter.ISO_DATE)));

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
            newCompetitions = newCompetitions.stream().limit(limit).collect(Collectors.toList());
        } else {
            LOG.info("Found " + newCompetitions.size() + " new competitions from " + source.name());
        }

        // Fetch complete dataset for all new competitions
        newCompetitions.forEach(competition -> {
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

        // Write back where date we reached - relevant for catchup
        config.values().put("catchupfrom", newFrom.format(DateTimeFormatter.ISO_DATE));
    }
}
