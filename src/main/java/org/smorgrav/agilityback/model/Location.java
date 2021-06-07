package org.smorgrav.agilityback.model;

import java.util.Objects;

public class Location {

    public static String BLANK = "";
    public static double UNKNOWN = 0;
    public static final Location EMPTY = new Location(BLANK, BLANK, BLANK, BLANK, UNKNOWN, UNKNOWN);

    private final String name;
    private final String country;
    private final String region;
    private final String address;
    private final double longitude;
    private final double latitude;

    private Location(String name, String country, String region, String address, double longitude, double latitude) {
        this.name = Objects.requireNonNull(name);
        this.country = Objects.requireNonNull(country);
        this.region = Objects.requireNonNull(region);
        this.address = Objects.requireNonNull(address);
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String name() {
        return name;
    }

    public String country() {
        return country;
    }

    public String region() {
        return region;
    }

    public String address() {
        return address;
    }

    public double longitude() {
        return longitude;
    }

    public double latitude() {
        return latitude;
    }

    public Location withName(String newName) {
        if (newName == null) return this;
        return new Location(newName, country, region, address, longitude, latitude);
    }

    public Location country(String newCountry) {
        if (newCountry == null) return this;
        return new Location(name, newCountry, region, address, longitude, latitude);
    }

    public Location withRegion(String newRegion) {
        if (newRegion == null) return this;
        return new Location(name, country, newRegion, address, longitude, latitude);
    }

    public Location withAddress(String newAddress) {
        if (newAddress == null) return this;
        return new Location(name, country, region, newAddress, longitude, latitude);
    }

    public Location withLongitude(double newLongitude) {
        if (newLongitude == UNKNOWN) return this;
        return new Location(name, country, region, address, newLongitude, latitude);
    }

    public Location withLatitude(double newLatitude) {
        if (newLatitude == UNKNOWN) return this;
        return new Location(name, country, region, address, longitude, newLatitude);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Double.compare(location.longitude, longitude) == 0 &&
                Double.compare(location.latitude, latitude) == 0 &&
                name.equals(location.name) &&
                country.equals(location.country) &&
                region.equals(location.region) &&
                address.equals(location.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, country, region, address, longitude, latitude);
    }
}
