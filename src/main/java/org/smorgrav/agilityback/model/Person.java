package org.smorgrav.agilityback.model;

import java.util.Objects;

public class Person {

    public static Person EMPTY = new Person("", "", "", "");

    private final String sourceId;
    private final String id;
    private final String name;
    private final String club;

    private Person(String externalId, String id, String name, String club) {
        sourceId = Objects.requireNonNull(externalId);
        this.id = Objects.requireNonNull(id);
        this.club = Objects.requireNonNull(club);
        this.name = Objects.requireNonNull(name);
    }

    public Person withName(String newName) {
        return new Person(sourceId, id, newName, club);
    }

    public Person withSourceId(String newSourceId) {
        return new Person(newSourceId, id, name, club);
    }

    public Person withId(String newInternalId) {
        return new Person(sourceId, newInternalId, name, club);
    }

    public Person withClub(String newClub) {
        return new Person(sourceId, id, name, newClub);
    }

    public String sourceId() {
        return sourceId;
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String club() {
        return club;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Person person = (Person) o;
        return Objects.equals(sourceId, person.sourceId) &&
                Objects.equals(id, person.id) &&
                Objects.equals(name, person.name) &&
                Objects.equals(club, person.club);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceId, id, name, club);
    }
}
