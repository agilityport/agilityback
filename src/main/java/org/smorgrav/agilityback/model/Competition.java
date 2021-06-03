package org.smorgrav.agilityback.model;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
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

    public static Competition EMPTY = new Competition("", "", LocalDate.MIN, LocalDate.MAX, LocalDate.MAX, "",
            SourceName.agilityport, new ArrayList<>(), CompetitionType.Unknown, CompetitionStatus.Planned, Organiser.EMPTY, Federation.UNKNOWN);

    private final String id;                    // Internal - blank if undecided yet
    private final String name;
    private final LocalDate fromDate;           // Inclusive
    private final LocalDate toDate;             // Inclusive
    private final LocalDate registrationDeadline;
    private final Federation federation;
    private final CompetitionType type;
    private final CompetitionStatus status;
    private final String eventSummary;          // This may be set by source system but overridden if we have all info we need to generate one better
    private final SourceName source;            // The name of the system where we imported this competition from
    private final List<String> sourceIds;       // Some systems needs to model a competition as multiple competitions (I'm looking at you NKK)
    private final Organiser organiser;          // A copy of the potential central organizer collection

    // The events are lazy loaded - and will be empty initially - stored in a separate collection.
    // TODO make sorted set when working on scheduler again
    private final Set<Event> events = new HashSet<>();

    //TODO assert not null
    private Competition(String id, String name, LocalDate fromDate, LocalDate toDate, LocalDate registrationDeadline,
                        String eventSummary, SourceName source, List<String> sourceIds, CompetitionType type,
                        CompetitionStatus status, Organiser organiser, Federation federation) {
        this.id = Objects.requireNonNull(id);
        this.name = name;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.registrationDeadline = registrationDeadline;
        this.eventSummary = eventSummary;
        this.type = type;
        this.source = source;
        this.sourceIds = new ArrayList<>(sourceIds);
        this.status = status;
        this.organiser = organiser;
        this.federation = federation;
    }

    public void addEvent(Event event) {
        events.add(event);
    }

    public Competition withId(String newId) {
        return new Competition(newId, name, fromDate, toDate, registrationDeadline, eventSummary, source, sourceIds, type, status, organiser, federation);
    }

    public Competition withRandomId() {
        return new Competition(UUID.randomUUID().toString(), name, fromDate, toDate, registrationDeadline, eventSummary, source, sourceIds, type, status, organiser, federation);
    }

    public Competition withName(String newName) {
        if (newName == null) {
            return this;
        }
        return new Competition(id, newName, fromDate, toDate, registrationDeadline, eventSummary, source, sourceIds, type, status, organiser, federation);
    }

    public Competition withFromDate(LocalDate newDate) {
        if (newDate == null) {
            return this;
        }
        return new Competition(id, name, newDate, toDate, registrationDeadline, eventSummary, source, sourceIds, type, status, organiser, federation);
    }

    public Competition withToDate(LocalDate newDate) {
        if (newDate == null) {
            return this;
        }
        return new Competition(id, name, fromDate, newDate, registrationDeadline, eventSummary, source, sourceIds, type, status, organiser, federation);
    }

    public Competition withRegistrationDeadline(LocalDate newDate) {
        if (newDate == null) {
            return this;
        }
        return new Competition(id, name, fromDate, toDate, newDate, eventSummary, source, sourceIds, type, status, organiser, federation);
    }

    public Competition withEventSummary(String newSummary) {
        if (newSummary == null) {
            return this;
        }
        return new Competition(id, name, fromDate, toDate, registrationDeadline, newSummary, source, sourceIds, type, status, organiser, federation);
    }

    public Competition withSource(SourceName newSource) {
        if (newSource == null) {
            return this;
        }
        return new Competition(id, name, fromDate, toDate, registrationDeadline, eventSummary, newSource, sourceIds, type, status, organiser, federation);
    }

    public Competition withSourceIds(List<String> newSourceIds) {
        if (newSourceIds == null) {
            return this;
        }
        return new Competition(id, name, fromDate, toDate, registrationDeadline, eventSummary, source, newSourceIds, type, status, organiser, federation);
    }

    public Competition withSourceId(String newSourceId) {
        if (newSourceId == null) {
            return this;
        }
        return new Competition(id, name, fromDate, toDate, registrationDeadline, eventSummary, source, List.of(newSourceId), type, status, organiser, federation);
    }

    public Competition withType(CompetitionType newType) {
        if (newType == null) {
            return this;
        }
        return new Competition(id, name, fromDate, toDate, registrationDeadline, eventSummary, source, sourceIds, newType, status, organiser, federation);
    }

    public Competition withStatus(CompetitionStatus newStatus) {
        if (newStatus == null) {
            return this;
        }
        return new Competition(id, name, fromDate, toDate, registrationDeadline, eventSummary, source, sourceIds, type, newStatus, organiser, federation);
    }

    public Competition withOrganiser(Organiser newOrganiser) {
        if (newOrganiser == null) {
            return this;
        }
        return new Competition(id, name, fromDate, toDate, registrationDeadline, eventSummary, source, sourceIds, type, status, newOrganiser, federation);
    }

    public Competition withFederation(Federation newFederation) {
        if (newFederation == null) {
            return this;
        }
        return new Competition(id, name, fromDate, toDate, registrationDeadline, eventSummary, source, sourceIds, type, status, organiser, newFederation);
    }

    public Competition withEvents(List<Event> events) {
        if (events == null) {
            return this;
        }
        this.events.clear();
        this.events.addAll(events);
        return this;
    }

    public void addSourceIds(List<String> newSourceIds) {
        if (newSourceIds != null) {
            sourceIds.addAll(newSourceIds);
        }
    }

    public List<String> sourceIds() {
        return sourceIds;
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

    public SourceName source() {
        return source;
    }

    public Organiser organiser() {
        return organiser;
    }

    public boolean isRunning() {
        return status == CompetitionStatus.Running;
    }

    public String eventSummary() {
        return eventSummary;
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

    public LocalDate registrationDeadline() {
        return registrationDeadline;
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
                registrationDeadline.equals(that.registrationDeadline) &&
                type == that.type &&
                status == that.status &&
                eventSummary.equals(that.eventSummary) &&
                source == that.source &&
                sourceIds.equals(that.sourceIds) &&
                organiser.equals(that.organiser) &&
                Objects.equals(federation,that.federation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, fromDate, toDate, registrationDeadline, type, status, eventSummary, source, sourceIds, organiser, federation);
    }
}
