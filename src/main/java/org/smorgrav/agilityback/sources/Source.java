package org.smorgrav.agilityback.sources;

import org.smorgrav.agilityback.model.Competition;
import org.smorgrav.agilityback.model.Event;
import org.smorgrav.agilityback.model.SourceName;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * A source is an external source of agility fixtures, results and whatever.
 */
public interface Source {

    public SourceName name();

    /**
     * A competition list is a list of all available competitions between two dates.
     * Doe not include evnt information.
     * <p>
     * This is used to decide which of the returned competitions to further look at.
     *
     * @return Shallow imported competitions
     * @throws IOException
     */
    List<Competition> fetchCompetitions(LocalDate fromIncluding, LocalDate toIncluding) throws IOException;

    /**
     * Add all relevant events to the competition. This does not include the equipages.
     *
     * @param competition The competition to add events to and for
     * @throws IOException
     */
    void fetchEvents(Competition competition) throws IOException;

    /**
     * Import all relevant information for this particular event.
     * <p>
     * This is used when the system is looking only to update ongoing events and
     * the idea is that this is not that heavy on the target system.
     *
     * @param competition The competition this event belongs to
     * @param event       The event to fetch equipages for
     * @throws IOException
     */
    void fetchEquipages(Competition competition, Event event) throws IOException;
}
