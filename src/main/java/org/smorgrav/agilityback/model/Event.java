package org.smorgrav.agilityback.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * TODO Add link to course
 * TODO Add judge - as string or as person?
 * TODO Add fee as info string - or?
 */
public class Event {

    public static LocalDateTime NO_TIME = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
    public static String BLANK = "";

    public static String DEFAULT_RING = "";
    public static Event EMPTY = new Event(BLANK, BLANK, BLANK, BLANK, EventType.unknown, List.of(Size.unknown), TrialType.Other, DEFAULT_RING, NO_TIME, Duration.ZERO, Duration.ZERO, NO_TIME, NO_TIME, NO_TIME);
    private final String id;
    private final String competitionId; // A storage reference to the competition this event was held
    private final String sourceId;
    private final String name;          // E.g A or B Used to differentiate multiple races for same size and trial on same date - derived if not set
    private final EventType eventType;
    private final TrialType trialType;
    private final List<Size> sizes;     //Typically just one, and at least one (which might be unknown) - courseWalks are typically combined
    private final String ring;          // When a competition has multiple rings - this becomes important for scheduling
    //
    // Variables below here is typically not available from external systems but derived or annotated
    //
    private final LocalDateTime schedule; // When is this event scheduled to start
    private final Duration duration;      // Only used for non trial events (e.g to indicate coursewalk length)
    private final Duration minDuration;   // Only used for trial events as a low threshold (or placeholder for participants) ond buffer
    //
    // Equipage list is lazy loaded
    //
    private final Set<Equipage> equipageList = new HashSet<>(); // TODO Make a sorted set when working on scheduler again
    //
    // These are mutable variables
    //
    private LocalDateTime estimatedStart; // Will change every X seconds
    private LocalDateTime started;        // Static when we have it
    private LocalDateTime ended;          // Static when we have it

    public Event(String id, String competitionId, String sourceId, String name, EventType eventType, List<Size> sizes, TrialType trialType, String ring, LocalDateTime schedule, Duration duration, Duration minDuration, LocalDateTime estimatedStart, LocalDateTime started, LocalDateTime ended) {
        this.id = Objects.requireNonNull(id);
        this.competitionId = Objects.requireNonNull(competitionId);
        this.sourceId = Objects.requireNonNull(sourceId);
        this.name = Objects.requireNonNull(name);
        this.eventType = Objects.requireNonNull(eventType);
        this.trialType = Objects.requireNonNull(trialType);
        this.schedule = Objects.requireNonNull(schedule);
        this.duration = Objects.requireNonNull(duration);
        this.minDuration = Objects.requireNonNull(minDuration);
        this.ring = Objects.requireNonNull(ring);
        this.sizes = Objects.requireNonNull(sizes);
        if (sizes.isEmpty()) {
            throw new IllegalArgumentException("Need at least one size");
        }
        this.estimatedStart = Objects.requireNonNull(estimatedStart);
        this.started = started;
        this.ended = ended;
    }

    public static Event newCourseWalk(Size size, TrialType trialType, LocalDateTime schedule, Duration duration) {
        return new Event(UUID.randomUUID().toString(), BLANK, BLANK, BLANK, EventType.courseWalk, List.of(size), trialType, DEFAULT_RING, schedule, duration, duration, NO_TIME, NO_TIME, NO_TIME);
    }

    public static Event newTrial(String sourceId, String name, Size size, TrialType trialType, LocalDateTime schedule) {
        return new Event(UUID.randomUUID().toString(), BLANK, sourceId, name, EventType.trial, List.of(size), trialType, DEFAULT_RING, schedule, Duration.ofDays(0), Duration.ofHours(0), NO_TIME, NO_TIME, NO_TIME);
    }

    public static Event newBuild(Size size, TrialType trialType, LocalDateTime schedule, Duration duration) {
        return new Event(UUID.randomUUID().toString(), BLANK, BLANK, BLANK, EventType.build, List.of(size), trialType, DEFAULT_RING, schedule, duration, duration, NO_TIME, NO_TIME, NO_TIME);
    }

    public static Event newBuffer(Size size, TrialType trialType, LocalDateTime schedule, Duration duration) {
        return new Event(UUID.randomUUID().toString(), BLANK, BLANK, BLANK, EventType.buffer, List.of(size), trialType, DEFAULT_RING, schedule, duration, Duration.ofHours(0), NO_TIME, NO_TIME, NO_TIME);
    }

    public static Duration estimatedTimePrEquipage() {
        //TODO calculate on floating window on 15 dogs
        return Duration.ofSeconds(60);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Event event = (Event) o;
        return id.equals(event.id) &&
                competitionId.equals(event.competitionId) &&
                sourceId.equals(event.sourceId) &&
                name.equals(event.name) &&
                eventType == event.eventType &&
                trialType == event.trialType &&
                sizes.equals(event.sizes) &&
                ring.equals(event.ring) &&
                schedule.equals(event.schedule) &&
                duration.equals(event.duration) &&
                minDuration.equals(event.minDuration) &&
                equipageList.equals(event.equipageList) &&
                Objects.equals(estimatedStart, event.estimatedStart) &&
                Objects.equals(started, event.started) &&
                Objects.equals(ended, event.ended);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sourceId, competitionId, name, eventType, trialType, sizes, ring, schedule, duration, minDuration, equipageList, estimatedStart, started, ended);
    }

    public String name() {
        return name;
    }

    public String id() {
        return id;
    }

    public String competitionId() {
        return competitionId;
    }

    public String sourceId() {
        return sourceId;
    }

    public String eventSummary() {
        return eventType.toString() + " " + trialType.toString(); //TODO add sizes
    }

    public boolean isSize(Size wanted) {
        return sizes.contains(wanted);
    }

    public String ring() {
        return ring;
    }

    public LocalDate date() {
        return schedule.toLocalDate();
    }

    public TrialType trialType() {
        return trialType;
    }

    public EventType eventType() {
        return eventType;
    }

    public List<Size> sizes() {
        return sizes;
    }

    public LocalDateTime schedule() {
        return schedule;
    }

    public Duration duration() {
        return duration;
    }

    public Duration minDuration() {
        return minDuration;
    }

    public Event withId(String newId) {
        if (newId == null) {
            return this;
        }
        return new Event(newId, competitionId, sourceId, name, eventType, sizes, trialType, ring, schedule, duration, minDuration, estimatedStart, started, ended);
    }

    public Event withRandomId() {
        return new Event(UUID.randomUUID().toString(), competitionId, sourceId, name, eventType, sizes, trialType, ring, schedule, duration, minDuration, estimatedStart, started, ended);
    }

    public Event withCompetitionId(String newId) {
        if (newId == null) {
            return this;
        }
        return new Event(id, newId, sourceId, name, eventType, sizes, trialType, ring, schedule, duration, minDuration, estimatedStart, started, ended);
    }

    public Event withSourceId(String newId) {
        if (newId == null) {
            return this;
        }
        return new Event(id, competitionId, newId, name, eventType, sizes, trialType, ring, schedule, duration, minDuration, estimatedStart, started, ended);
    }

    public Event withName(String newName) {
        if (newName == null) {
            return this;
        }
        return new Event(id, competitionId, sourceId, newName, eventType, sizes, trialType, ring, schedule, duration, minDuration, estimatedStart, started, ended);
    }

    public Event withEventType(EventType newType) {
        if (newType == null) {
            return this;
        }
        return new Event(id, competitionId, sourceId, name, newType, sizes, trialType, ring, schedule, duration, minDuration, estimatedStart, started, ended);
    }

    public Event withTrialType(TrialType newType) {
        if (newType == null) {
            return this;
        }
        return new Event(id, competitionId, sourceId, name, eventType, sizes, newType, ring, schedule, duration, minDuration, estimatedStart, started, ended);
    }

    public Event withSizes(List<Size> newSizes) {
        if (newSizes == null) {
            return this;
        }
        return new Event(id, competitionId, sourceId, name, eventType, newSizes, trialType, ring, schedule, duration, minDuration, estimatedStart, started, ended);
    }

    public Event withRing(String newRing) {
        if (newRing == null) {
            return this;
        }
        return new Event(id, competitionId, sourceId, name, eventType, sizes, trialType, newRing, schedule, duration, minDuration, estimatedStart, started, ended);
    }

    public Event withSchedule(LocalDateTime newSchedule) {
        if (newSchedule == null) {
            return this;
        }
        return new Event(id, competitionId, sourceId, name, eventType, sizes, trialType, ring, newSchedule, duration, minDuration, estimatedStart, started, ended);
    }

    public Event withDuration(Duration newDuration) {
        if (newDuration == null) {
            return this;
        }
        return new Event(id, competitionId, sourceId, name, eventType, sizes, trialType, ring, schedule, newDuration, minDuration, estimatedStart, started, ended);
    }

    public Event withMinDuration(Duration newMinDuration) {
        if (newMinDuration == null) {
            return this;
        }
        return new Event(id, competitionId, sourceId, name, eventType, sizes, trialType, ring, schedule, duration, newMinDuration, estimatedStart, started, ended);
    }

    public Event withEstimatedStart(LocalDateTime newEstimatedStart) {
        if (newEstimatedStart == null) {
            return this;
        }
        return new Event(id, competitionId, sourceId, name, eventType, sizes, trialType, ring, schedule, duration, minDuration, newEstimatedStart, started, ended);
    }

    public Event withStarted(LocalDateTime newStarted) {
        if (newStarted == null) {
            return this;
        }
        return new Event(id, competitionId, sourceId, name, eventType, sizes, trialType, ring, schedule, duration, minDuration, estimatedStart, newStarted, ended);
    }

    public Event withEnded(LocalDateTime newEnded) {
        if (newEnded == null) {
            return this;
        }
        return new Event(id, competitionId, sourceId, name, eventType, sizes, trialType, ring, schedule, duration, minDuration, estimatedStart, started, newEnded);
    }

    public Optional<LocalDateTime> started() {
        return Optional.ofNullable(started.equals(NO_TIME) ? null : started);
    }

    public Event setStarted(LocalDateTime started) {
        this.started = started;
        return this;
    }

    public Optional<LocalDateTime> ended() {
        return Optional.ofNullable(ended.equals(NO_TIME) ? null : ended);
    }

    public Event setEnded(LocalDateTime ended) {
        if (ended == null) {
            this.ended = NO_TIME;
        } else {
            this.ended = ended;
        }
        return this;
    }

    public LocalDateTime estimatedStart() {
        if (estimatedStart == null) {
            return schedule();
        } else {
            return estimatedStart;
        }
    }

    public void setEstimatedStart(LocalDateTime estimatedStart) {
        this.estimatedStart = estimatedStart;
    }

    public void add(Equipage eq) {
        equipageList.add(eq);
    }

    public LocalDateTime estimatedEnd() {
        if (ended().isPresent()) {
            return ended().get();
        }

        // Estimated end is the estimated last equipage plus the time for it to complete
        if (!equipageList.isEmpty()) {
            Optional<LocalDateTime> lastStart = Optional.empty(); //equipageList.last().estimatedStart();
            // Ok, something is wrong and we just mulitiply a fixed duration with number of starts
            if (lastStart.isPresent()) {
                return lastStart.get().plus(estimatedTimePrEquipage());
            } else {
                return schedule().plus(estimatedTimePrEquipage().multipliedBy(equipageList.size()));
            }
        }

        // If no equipages, it event ends instantly
        return schedule();
    }

    public Set<Equipage> equipages() {
        return equipageList;
    }

    public void updateSchedule() {
        // If the event has ended - there is nothing to update
        if (ended().isPresent()) {
            return;
        }

        LocalDateTime prevEndtime = null;
        for (Equipage equipage : equipages()) {
            if (prevEndtime == null) {
                // First equipage starts when event starts
                equipage.setEstimatedStart(estimatedStart());
                prevEndtime = equipage.estimatedEnd(estimatedTimePrEquipage());
            } else {
                // Next equipage is based on prev endtime
                equipage.setEstimatedStart(prevEndtime);
                prevEndtime = equipage.estimatedEnd(estimatedTimePrEquipage());
            }
        }
    }
}
