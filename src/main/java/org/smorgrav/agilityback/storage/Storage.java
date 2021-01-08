package org.smorgrav.agilityback.storage;

import org.smorgrav.agilityback.model.Competition;
import org.smorgrav.agilityback.model.Equipage;
import org.smorgrav.agilityback.model.Event;
import org.smorgrav.agilityback.model.SourceName;

import java.time.LocalDate;
import java.util.List;

public interface Storage {

    /**
     * @param competition The competition to persist in storage
     * @param deepWrite   True if events and equipages should be stored too
     */
    void write(Competition competition, boolean deepWrite);

    void write(Event event);

    void write(Equipage equipage);

    void readEvents(Competition competition);

    void readEquipages(Event event);

    List<Competition> read(SourceName sourceName, LocalDate from, LocalDate to);

    StorageValues readConfig(String configId);

    void writeConfig(StorageValues values);

}
