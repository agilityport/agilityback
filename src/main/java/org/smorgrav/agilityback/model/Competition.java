package org.smorgrav.agilityback.model;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * A competition is any natural group of events that shares registration ran by one organiser
 * in one place and over the course of one or a few days.
 *
 * Time will show if this is a good categorization. Maybe a competition can last a year and
 * we have on demand registration for events - I'm not sure yet. Or maybe we will create a cup structure
 * with a set of competitions instead.
 */
public class Competition {

    public static String BLANK = "";
    public static Competition EMPTY = new Competition(BLANK, BLANK, LocalDate.MIN, LocalDate.MAX, BLANK, BLANK,
            CompetitionType.Unknown, CompetitionStatus.Planned, Organiser.EMPTY, Federation.UNKNOWN,
            Registration.EMPTY, SourceInfo.EMPTY, Location.EMPTY);

    private final String id;                    // Internal - blank if undecided yet
    private final String name;
    private final LocalDate fromDate;           // Inclusive
    private final LocalDate toDate;             // Inclusive - TODO do we need a set of dates instead
    private final Federation federation;
    private final CompetitionType type;
    private final CompetitionStatus status;
    private final String eventSummary;          // Frontend summary field - meant for humans
    private final String searchSummary;         // System generated and used as a easy fuzzy search field
    private final Organiser organiser;          // A copy of the potential central organizer collection
    private final SourceInfo sourceInfo;        // Information to link back to the source (urls and ids)
    private final Registration registration;    // All about how and when to register
    private final Location location;            // The actual location of the competition - gps, name, country code etc

    // The events are lazy loaded - and will be empty initially - stored in a separate collection.
    // TODO make sorted set when working on scheduler again
    private final Set<Event> events = new HashSet<>();

    private Competition(String id, String name, LocalDate fromDate, LocalDate toDate,
                        String eventSummary, String searchSummary, CompetitionType type,
                        CompetitionStatus status, Organiser organiser, Federation federation,
                        Registration registration, SourceInfo sourceInfo, Location location) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.fromDate = Objects.requireNonNull(fromDate);
        this.toDate = Objects.requireNonNull(toDate);
        this.eventSummary = Objects.requireNonNull(eventSummary);
        this.searchSummary = Objects.requireNonNull(searchSummary);
        this.type = Objects.requireNonNull(type);
        this.status = Objects.requireNonNull(status);
        this.organiser = Objects.requireNonNull(organiser);
        this.federation = Objects.requireNonNull(federation);
        this.registration = Objects.requireNonNull(registration);
        this.sourceInfo = Objects.requireNonNull(sourceInfo);
        this.location = Objects.requireNonNull(location);
    }

    public void addEvent(Event event) {
        events.add(event);
    }

    public Competition withId(String newId) {
        return new Competition(newId, name, fromDate, toDate,eventSummary, searchSummary, type, status, organiser, federation, registration, sourceInfo, location);
    }

    public Competition withRandomId() {
        return new Competition(UUID.randomUUID().toString(), name, fromDate, toDate,eventSummary, searchSummary, type, status, organiser, federation, registration, sourceInfo, location);
    }

    public Competition withName(String newName) {
        if (newName == null) {
            return this;
        }
        return new Competition(id, newName, fromDate, toDate,eventSummary, searchSummary, type, status, organiser, federation, registration, sourceInfo, location);
    }

    public Competition withFromDate(LocalDate newDate) {
        if (newDate == null) {
            return this;
        }
        return new Competition(id, name, newDate, toDate, eventSummary, searchSummary, type, status, organiser, federation, registration, sourceInfo, location);
    }

    public Competition withToDate(LocalDate newDate) {
        if (newDate == null) {
            return this;
        }
        return new Competition(id, name, fromDate, newDate, eventSummary, searchSummary, type, status, organiser, federation, registration, sourceInfo, location);
    }

    public Competition withEventSummary(String newSummary) {
        if (newSummary == null) {
            return this;
        }
        return new Competition(id, name, fromDate, toDate, newSummary, searchSummary, type, status, organiser, federation, registration, sourceInfo, location);
    }

    public Competition withSearchSummary(String newSummary) {
        if (newSummary == null) {
            return this;
        }
        return new Competition(id, name, fromDate, toDate, eventSummary, newSummary, type, status, organiser, federation, registration, sourceInfo, location);
    }

    public Competition withType(CompetitionType newType) {
        if (newType == null) {
            return this;
        }
        return new Competition(id, name, fromDate, toDate, eventSummary, searchSummary, newType, status, organiser, federation, registration, sourceInfo, location);
    }

    public Competition withStatus(CompetitionStatus newStatus) {
        if (newStatus == null) {
            return this;
        }
        return new Competition(id, name, fromDate, toDate, eventSummary, searchSummary, type, newStatus, organiser, federation, registration, sourceInfo, location);
    }

    public Competition withOrganiser(Organiser newOrganiser) {
        if (newOrganiser == null) {
            return this;
        }
        return new Competition(id, name, fromDate, toDate, eventSummary, searchSummary, type, status, newOrganiser, federation, registration, sourceInfo, location);
    }

    public Competition withFederation(Federation newFederation) {
        if (newFederation == null) {
            return this;
        }
        return new Competition(id, name, fromDate, toDate, eventSummary, searchSummary, type, status, organiser, newFederation, registration, sourceInfo, location);
    }

    public Competition withRegistration(Registration newRegistration) {
        if (newRegistration == null) {
            return this;
        }
        return new Competition(id, name, fromDate, toDate, eventSummary, searchSummary, type, status, organiser, federation, newRegistration, sourceInfo, location);
    }

    public Competition withSourceInfo(SourceInfo newSourceInfo) {
        if (newSourceInfo == null) {
            return this;
        }
        return new Competition(id, name, fromDate, toDate, eventSummary, searchSummary, type, status, organiser, federation, registration, newSourceInfo, location);
    }


    public Competition withLocation(Location newLocation) {
        if (newLocation == null) {
            return this;
        }
        return new Competition(id, name, fromDate, toDate, eventSummary, searchSummary, type, status, organiser, federation, registration, sourceInfo, newLocation);
    }

    public Competition withEvents(List<Event> events) {
        if (events == null) {
            return this;
        }
        this.events.clear();
        this.events.addAll(events);
        return this;
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public LocalDate fromDate() {
        return fromDate;
    }

    public boolean isValid() {
        return !toDate.equals(LocalDate.MAX) && !fromDate.equals(LocalDate.MIN);
    }

    public LocalDate toDate() {
        return toDate;
    }

    public Organiser organiser() {
        return organiser;
    }

    public Federation federation() {
        return federation;
    }

    public boolean isRunning() {
        return status == CompetitionStatus.Running;
    }

    public String eventSummary() {
        return eventSummary;
    }

    public String searchSummary() {
        return searchSummary;
    }

    public Set<Event> events() {
        return events;
    }

    public CompetitionStatus status() {
        return status;
    }

    public CompetitionType type() {
        return type;
    }

    public SourceInfo sourceInfo() {
        return this.sourceInfo;
    }

    public Registration registration() {
        return registration;
    }

    public Location location() {
        return location;
    }

    /**
     * For scheduling it is easier when we group events into their respective date and ring.
     */
    public List<RingDate> rings() {
        var groups = events.stream().collect(Collectors.groupingBy(event -> event.ring(),
                Collectors.groupingBy(event -> event.schedule().toLocalDate())));

        List<RingDate> rings = new ArrayList<>();
        groups.forEach((name, map) -> {
            map.forEach((date, ringevents) -> {
                RingDate ring = new RingDate(name, date);
                ring.events().addAll(ringevents);
            });
        });

        return rings;
    }

    public void updateSchedule() {
        rings().forEach(ringDate -> {
            Event prevEvent = null;
            for (Event event : ringDate.events()) {
                if (prevEvent == null) {
                    prevEvent = event;
                    continue;
                }
                prevEvent.updateSchedule(); // Update estimatedEnd of prev event (so we know when next can start)
                event.setEstimatedStart(prevEvent.estimatedEnd().plus(Duration.ofMinutes(1))); //TODO what makes sense here
                prevEvent = event;
            }
            if (prevEvent != null) {
                prevEvent.updateSchedule(); // Update last event
            }
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Competition that = (Competition) o;
        return id.equals(that.id) &&
                name.equals(that.name) &&
                fromDate.equals(that.fromDate) &&
                toDate.equals(that.toDate) &&
                eventSummary.equals(that.eventSummary) &&
                searchSummary.equals(that.searchSummary) &&
                type == that.type &&
                status == that.status &&
                organiser.equals(that.organiser) &&
                Objects.equals(location, that.location) &&
                Objects.equals(sourceInfo, that.sourceInfo) &&
                Objects.equals(registration, that.registration) &&
                Objects.equals(federation,that.federation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, fromDate, toDate, type, status, eventSummary, searchSummary, sourceInfo, organiser, federation, location, registration);
    }
}
