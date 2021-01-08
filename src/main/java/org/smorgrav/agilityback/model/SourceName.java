package org.smorgrav.agilityback.model;

public enum SourceName {
    community(false), //Unverified listings
    agilityport(false), // Verified listings
    hundestevner(true), // NO system
    nkk(true), // Dogweb today
    sagik(true),
    unknown(false); // To make versioning easier (unknown server or client name)

    private final boolean isExternal;

    SourceName(boolean isExternal) {
        this.isExternal = isExternal;
    }

    public boolean isExternal() {
        return isExternal;
    }
}