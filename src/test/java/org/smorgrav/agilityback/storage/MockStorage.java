package org.smorgrav.agilityback.storage;

import org.smorgrav.agilityback.model.Competition;
import org.smorgrav.agilityback.model.Equipage;
import org.smorgrav.agilityback.model.Event;
import org.smorgrav.agilityback.model.SourceName;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MockStorage implements Storage {

    private static final Logger LOG = Logger.getLogger(MockStorage.class.getName());

    @Override
    public void write(Competition competition, boolean deepWrite) {
        if (competition.id().isBlank()) {
            throw new IllegalArgumentException("Competition id must be set");
        }
        LOG.info("test");
    }

    @Override
    public void write(Event event) {
        if (event.id().isBlank() || event.competitionId().isBlank()) {
            throw new IllegalArgumentException("Competition and event id must be set");
        }
        LOG.info("test");
    }

    @Override
    public void write(Equipage equipage) {
        if (equipage.id().isBlank() || equipage.competitionId().isBlank() || equipage.eventId().isBlank()) {
            throw new IllegalArgumentException("Competition, event and equipage Id must be set");
        }
        LOG.info("test");
    }

    @Override
    public void readEvents(Competition competition) {
        LOG.info("test");
    }

    @Override
    public void readEquipages(Event event) {
        LOG.info("test");
    }

    @Override
    public List<Competition> read(SourceName sourceName, LocalDate from, LocalDate to) {
        LOG.info("test");
        return new ArrayList<>();
    }

    @Override
    public StorageValues readConfig(String configId) {
        return new StorageValues(configId);
    }

    @Override
    public void writeConfig(StorageValues values) {

    }
}
