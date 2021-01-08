package org.smorgrav.agilityback.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.TreeSet;

public class RingDate {
    private final String name;
    private final LocalDate date;
    private final TreeSet<Event> events = new TreeSet<>();

    public RingDate(String name, LocalDate date) {
        this.name = name;
        this.date = date;
    }

    public String name() {
        return name;
    }

    public LocalDate date() {
        return date;
    }

    public LocalDateTime dateTime(LocalTime time) {
        return date.atTime(time);
    }

    public TreeSet<Event> events() {
        return events;
    }

    public Event firstEvent() {
        return events.first();
    }

    public Event next(Event event) {
        return events.higher(event);
    }

    public Event prev(Event event) {
        return events.lower(event);
    }
}
