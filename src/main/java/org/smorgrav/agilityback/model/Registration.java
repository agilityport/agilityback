package org.smorgrav.agilityback.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Registration {

    public static final Registration EMPTY = new Registration(LocalDateTime.MIN, LocalDateTime.MAX, false, "", 0,0,0);
    
    private final int maxPrDay;
    private final int maxPrEvent;
    private final int maxCompetition;
    private final LocalDateTime startOfRegistration;
    private final LocalDateTime endOfRegistration;
    private final boolean registrationOpen;
    private final String message;
    
    private Registration(LocalDateTime startOfRegistration, LocalDateTime endOfRegistration, boolean registrationOpen, String message, int maxPrDay, int maxCompetition, int maxPrEvent) {
        this.startOfRegistration = startOfRegistration;
        this.endOfRegistration = endOfRegistration;
        this.registrationOpen = registrationOpen;
        this.message = message;
        this.maxPrDay = maxPrDay;
        this.maxCompetition = maxCompetition;
        this.maxPrEvent = maxPrEvent;
    }

    public int maxPrDay() {
        return maxPrDay;
    }

    public int maxPrEvent() {
        return maxPrEvent;
    }

    public int maxCompetition() {
        return maxCompetition;
    }

    public LocalDateTime startOfRegistration() {
        return startOfRegistration;
    }

    public LocalDateTime endOfRegistration() {
        return endOfRegistration;
    }

    public boolean registrationOpen() {
        return registrationOpen;
    }

    public String message() {
        return message;
    }

    public Registration withStartOfRegistration(LocalDateTime date) {
        if (date == null) {
            return this;
        }
        return new Registration(date, endOfRegistration, registrationOpen, message, maxPrDay, maxCompetition, maxPrEvent);
    }

    public Registration withEndOfRegistration(LocalDateTime date) {
        if (date == null) {
            return this;
        }
        return new Registration(startOfRegistration, date, registrationOpen, message, maxPrDay, maxCompetition, maxPrEvent);
    }

    public Registration withRegistrationOpen(boolean open) {
        return new Registration(startOfRegistration, endOfRegistration, open, message, maxPrDay, maxCompetition, maxPrEvent);
    }

    public Registration withMessage(String newMessage) {
        if (newMessage == null) {
            return null;
        }
        return new Registration(startOfRegistration, endOfRegistration, registrationOpen, newMessage, maxPrDay, maxCompetition, maxPrEvent);
    }

    public Registration withMaxPrDay(int newMax) {
        return new Registration(startOfRegistration, endOfRegistration, registrationOpen, message, newMax, maxCompetition, maxPrEvent);
    }

    public Registration withMaxCompetition(int newMax) {
        return new Registration(startOfRegistration, endOfRegistration, registrationOpen, message, maxPrDay, newMax, maxPrEvent);
    }

    public Registration withMaxPrEvent(int newMax) {
        return new Registration(startOfRegistration, endOfRegistration, registrationOpen, message, maxPrDay, maxCompetition, newMax);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Registration that = (Registration) o;
        return maxPrDay == that.maxPrDay &&
                maxPrEvent == that.maxPrEvent &&
                maxCompetition == that.maxCompetition &&
                registrationOpen == that.registrationOpen &&
                Objects.equals(startOfRegistration, that.startOfRegistration) &&
                Objects.equals(endOfRegistration, that.endOfRegistration) &&
                Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maxPrDay, maxPrEvent, maxCompetition, startOfRegistration, endOfRegistration, registrationOpen, message);
    }
}
