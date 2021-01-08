package org.smorgrav.agilityback.model;

public enum EventType {
    courseWalk, // Typically a group of handlers at the same time
    trial, // The main event where the equipages are doing their thing
    build, // Building a new course
    buffer, // Space that can be removed if needed
    unknown
}