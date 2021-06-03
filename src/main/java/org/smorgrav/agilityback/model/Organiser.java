package org.smorgrav.agilityback.model;

import java.util.Objects;

public class Organiser {
    public static Organiser EMPTY = new Organiser("", "", "", "", "", "", "");

    private final String organizerName;
    private final String addressLine1;
    private final String addressLine2;
    private final String contactPerson;
    private final String contactEmail;
    private final String contactPhone;
    private final String competitionLeader;

    private Organiser(String organizerName, String addressLine1, String addressLine2, String contactPerson, String contactEmail, String contactPhone, String competitionLeader) {
        this.organizerName = Objects.requireNonNull(organizerName);
        this.addressLine1 = Objects.requireNonNull(addressLine1);
        this.addressLine2 = Objects.requireNonNull(addressLine2);
        this.contactPerson = Objects.requireNonNull(contactPerson);
        this.contactEmail = Objects.requireNonNull(contactEmail);
        this.contactPhone = Objects.requireNonNull(contactPhone);
        this.competitionLeader = Objects.requireNonNull(competitionLeader);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Organiser organiser = (Organiser) o;
        return organizerName.equals(organiser.organizerName) &&
                addressLine1.equals(organiser.addressLine1) &&
                addressLine2.equals(organiser.addressLine2) &&
                contactPerson.equals(organiser.contactPerson) &&
                contactEmail.equals(organiser.contactEmail) &&
                contactPhone.equals(organiser.contactPhone) &&
                competitionLeader.equals(organiser.competitionLeader);
    }

    @Override
    public int hashCode() {
        return Objects.hash(organizerName, addressLine1, addressLine2, contactPerson, contactEmail, contactPhone, competitionLeader);
    }

    public Organiser withOrganizerName(String newName) {
        if (newName == null) {
            return this;
        }
        return new Organiser(newName, addressLine1, addressLine2, contactPerson, contactEmail, contactPhone, competitionLeader);
    }

    public Organiser withAddressLine1(String newAddress) {
        if (newAddress == null) {
            return this;
        }
        return new Organiser(organizerName, newAddress, addressLine2, contactPerson, contactEmail, contactPhone, competitionLeader);
    }

    public Organiser withAddressLine2(String newAddress) {
        if (newAddress == null) {
            return this;
        }
        return new Organiser(organizerName, addressLine1, newAddress, contactPerson, contactEmail, contactPhone, competitionLeader);
    }

    public Organiser withContactPerson(String newPerson) {
        if (newPerson == null) {
            return this;
        }
        return new Organiser(organizerName, addressLine1, addressLine2, newPerson, contactEmail, contactPhone, competitionLeader);
    }


    public Organiser withContactEmail(String newEmail) {
        if (newEmail == null) {
            return this;
        }
        return new Organiser(organizerName, addressLine1, addressLine2, contactPerson, newEmail, contactPhone, competitionLeader);
    }

    public Organiser withContactPhone(String newPhone) {
        if (newPhone == null) {
            return null;
        }
        return new Organiser(organizerName, addressLine1, addressLine2, contactPerson, contactEmail, newPhone, competitionLeader);
    }

    public Organiser withCompetitionLeader(String newLeader) {
        if (newLeader == null) {
            return this;
        }
        return new Organiser(organizerName, addressLine1, addressLine2, contactPerson, contactEmail, contactPhone, newLeader);
    }

    public String organizerName() {
        return organizerName;
    }

    public String addressLine1() {
        return addressLine1;
    }

    public String addressLine2() {
        return addressLine2;
    }

    public String contactEmail() {
        return contactEmail;
    }

    public String contactPerson() {
        return contactPerson;
    }

    public String contactPhone() {
        return contactPhone;
    }

    public String competitionLeader() {
        return competitionLeader;
    }
}
