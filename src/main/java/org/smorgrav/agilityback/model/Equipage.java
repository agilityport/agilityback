package org.smorgrav.agilityback.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Equipage {

    public static String BLANK = "";
    public static LocalDateTime NO_TIME = LocalDateTime.of(1970, 1, 1, 0, 0, 0);
    public static Equipage EMPTY = new Equipage(BLANK, BLANK, BLANK, Dog.EMPTY, Person.EMPTY, 0, EquipageState.WAITING, 0.0, 0.0, 0, NO_TIME, NO_TIME);

    private final Dog dog;
    private final Person handler;
    private final String id;
    private final String eventId;
    private final String competitionId;
    // These are stuff that typically can change within a session and thus mutable (really?)
    private final int startNumber; // 0 means not set
    private EquipageState state;
    private double fault;       //Assumed no faults until faults is given :)
    private double runTime;     //0 means not set
    private int place;          //0 meands not set
    private LocalDateTime estimatedStart;
    private LocalDateTime actualStart;

    private Equipage(String id, String eventId, String competitionId, Dog dog, Person handler, int startNumber, EquipageState state, double fault, double runTime, int place, LocalDateTime estimatedStart, LocalDateTime actualStart) {
        this.id = Objects.requireNonNull(id);
        this.eventId = Objects.requireNonNull(eventId);
        this.competitionId = Objects.requireNonNull(competitionId);
        this.dog = Objects.requireNonNull(dog);
        this.handler = Objects.requireNonNull(handler);
        this.startNumber = startNumber;
        this.state = Objects.requireNonNull(state);
        this.fault = fault;
        this.runTime = runTime;
        this.place = place;
        this.estimatedStart = Objects.requireNonNull(estimatedStart);
        this.actualStart = Objects.requireNonNull(actualStart);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Equipage equipage = (Equipage) o;
        return startNumber == equipage.startNumber &&
                Double.compare(equipage.fault, fault) == 0 &&
                Double.compare(equipage.runTime, runTime) == 0 &&
                place == equipage.place &&
                dog.equals(equipage.dog) &&
                handler.equals(equipage.handler) &&
                id.equals(equipage.id) &&
                competitionId.equals(equipage.competitionId) &&
                eventId.equals(equipage.eventId) &&
                state == equipage.state &&
                estimatedStart.equals(equipage.estimatedStart) &&
                actualStart.equals(equipage.actualStart);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dog, competitionId, eventId, handler, id, startNumber, state, fault, runTime, place, estimatedStart, actualStart);
    }

    public String derivedId() {
        return dog.name() + handler().name();
    }

    public Equipage withId(String newId) {
        return new Equipage(newId, eventId, competitionId, dog, handler, startNumber, state, fault, runTime, place, estimatedStart, actualStart);
    }

    public Equipage withRandomId() {
        return new Equipage(UUID.randomUUID().toString(), eventId, competitionId, dog, handler, startNumber, state, fault, runTime, place, estimatedStart, actualStart);
    }

    public Equipage withEventId(String newId) {
        return new Equipage(id, newId, competitionId, dog, handler, startNumber, state, fault, runTime, place, estimatedStart, actualStart);
    }

    public Equipage withCompetitionId(String newId) {
        return new Equipage(id, eventId, newId, dog, handler, startNumber, state, fault, runTime, place, estimatedStart, actualStart);
    }

    public Equipage withHandler(Person newHandler) {
        return new Equipage(id, eventId, competitionId, dog, newHandler, startNumber, state, fault, runTime, place, estimatedStart, actualStart);
    }

    public Equipage withDog(Dog newDog) {
        return new Equipage(id, eventId, competitionId, newDog, handler, startNumber, state, fault, runTime, place, estimatedStart, actualStart);
    }

    public Equipage withStartNumber(int newStartNumber) {
        return new Equipage(id, eventId, competitionId, dog, handler, newStartNumber, state, fault, runTime, place, estimatedStart, actualStart);
    }

    public Equipage withFault(double newFault) {
        return new Equipage(id, eventId, competitionId, dog, handler, startNumber, state, newFault, runTime, place, estimatedStart, actualStart);
    }

    public Equipage withRunTime(double newRunTime) {
        return new Equipage(id, eventId, competitionId, dog, handler, startNumber, state, fault, newRunTime, place, estimatedStart, actualStart);
    }

    public Equipage withPlace(int newPlace) {
        return new Equipage(id, eventId, competitionId, dog, handler, startNumber, state, fault, runTime, newPlace, estimatedStart, actualStart);
    }

    public Equipage withEstimatedStart(LocalDateTime newEstimatedStart) {
        return new Equipage(id, eventId, competitionId, dog, handler, startNumber, state, fault, runTime, place, newEstimatedStart, actualStart);
    }

    public Equipage withActualStart(LocalDateTime newActualStart) {
        return new Equipage(id, eventId, competitionId, dog, handler, startNumber, state, fault, runTime, place, estimatedStart, newActualStart);
    }

    public String id() {
        return id;
    }

    public String competitionId() {
        return competitionId;
    }

    public String eventId() {
        return eventId;
    }

    public Dog dog() {
        return dog;
    }

    public Person handler() {
        return handler;
    }

    public int startNumber() {
        return startNumber;
    }

    public EquipageState state() {
        return state;
    }

    public Equipage setState(EquipageState state) {
        this.state = state;
        return this;
    }

    public double fault() {
        return fault;
    }

    public Equipage setFault(double fault) {
        this.fault = fault;
        return this;
    }

    public double runTime() {
        return runTime;
    }

    public Equipage setRunTime(double runTime) {
        this.runTime = runTime;
        return this;
    }

    public int place() {
        return place;
    }

    public Equipage setPlace(int place) {
        this.place = place;
        return this;
    }

    public void setEstimatedStart(LocalDateTime time) {
        estimatedStart = time;
    }

    public void setActualStart(LocalDateTime time) {
        actualStart = time;
    }

    public LocalDateTime actualStart() {
        return actualStart;
    }

    public LocalDateTime estimatedStart() {
        return estimatedStart;
    }

    private long runtimeToMilliseconds() {
        return (long) (runTime * 1000);
    }

    public LocalDateTime estimatedEnd(Duration avgEquipageTime) {
        if (!actualStart.equals(NO_TIME)) {
            if (runTime > 0) {
                return actualStart.plus(Duration.ofMillis(runtimeToMilliseconds()));
            } else {
                return actualStart.plus(avgEquipageTime);
            }
        }
        if (!estimatedStart.equals(NO_TIME)) {
            return estimatedStart.plus(avgEquipageTime);
        }
        return NO_TIME;
    }
}
