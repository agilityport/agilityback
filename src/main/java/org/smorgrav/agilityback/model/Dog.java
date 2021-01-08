package org.smorgrav.agilityback.model;

import java.util.Objects;

/**
 * TODO Expand this to include certifications and the size of the dog - and use it as a separate collection of dogs
 */
public class Dog {

    public static Dog EMPTY = new Dog("", "", "", "");

    private final String id;
    private final String sourceId;
    private final String name;
    private final String breed;

    private Dog(String id, String sourceId, String name, String breed) {
        this.sourceId = Objects.requireNonNull(sourceId);
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.breed = Objects.requireNonNull(breed);
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

    public String breed() {
        return breed;
    }

    public Dog withId(String newId) {
        return new Dog(newId, sourceId, name, breed);
    }

    public Dog withSourceId(String newSourceId) {
        if (newSourceId == null) {
            return this;
        }
        return new Dog(id, newSourceId, name, breed);
    }

    public Dog withName(String newName) {
        return new Dog(id, sourceId, newName, breed);
    }

    public Dog withBreed(String newBreed) {
        return new Dog(id, sourceId, name, newBreed);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Dog dog = (Dog) o;
        return Objects.equals(sourceId, dog.sourceId) &&
                Objects.equals(id, dog.id) &&
                Objects.equals(name, dog.name) &&
                Objects.equals(breed, dog.breed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceId, id, name, breed);
    }
}
