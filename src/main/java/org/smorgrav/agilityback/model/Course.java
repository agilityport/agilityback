package org.smorgrav.agilityback.model;

import java.util.Objects;

public class Course {

    public static final Course EMPTY = new Course(0,0,0,"","","");

    private final double length;
    private final double standardTime;
    private final double maxTime;
    private final String designerName;
    private final String externalUrl;
    private final String internalUrl;

    private Course(double length, double standardTime, double maxTime, String designerName, String externalUrl, String internalUrl) {
        this.length = length;
        this.standardTime = standardTime;
        this.maxTime = maxTime;
        this.designerName = Objects.requireNonNull(designerName);
        this.externalUrl = Objects.requireNonNull(externalUrl);
        this.internalUrl = Objects.requireNonNull(internalUrl);
    }

    public double length() {
        return length;
    }

    public double standardTime() {
        return standardTime;
    }

    public double maxTime() {
        return maxTime;
    }

    public String designerName() {
        return designerName;
    }

    public String externalUrl() {
        return externalUrl;
    }

    public String internalUrl() {
        return internalUrl;
    }

    public Course withLength(double newLength) {
        return new Course(newLength, standardTime, maxTime, designerName, externalUrl, internalUrl);
    }

    public Course withStandardTime(double newStandardTime) {
        return new Course(length, newStandardTime, maxTime, designerName, externalUrl, internalUrl);
    }

    public Course withMaxTime(double newMaxTime) {
        return new Course(length, standardTime, newMaxTime, designerName, externalUrl, internalUrl);
    }

    public Course withDesignerName(String newName) {
        if (newName == null) {
            return this;
        }
        return new Course(length, standardTime, maxTime, newName, externalUrl, internalUrl);
    }

    public Course withExternalUrl(String newUrl) {
        if (newUrl == null) {
            return this;
        }
        return new Course(length, standardTime, maxTime, designerName, newUrl, internalUrl);
    }

    public Course withInternalUrl(String newUrl) {
        if (newUrl == null) {
            return this;
        }
        return new Course(length, standardTime, maxTime, designerName, externalUrl, newUrl);
    }
}
