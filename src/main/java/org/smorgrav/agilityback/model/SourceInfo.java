package org.smorgrav.agilityback.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SourceInfo {

    public static String BLANK = "";
    public final static SourceInfo EMPTY = new SourceInfo(SourceName.unknown, Collections.emptyList(), BLANK, BLANK, BLANK, BLANK, BLANK);

    private final SourceName name;          // The name of the system where we imported this competition from
    private final List<String> sourceIds;     // Some systems needs to model a competition as multiple competitions (I'm looking at you NKK)
    private final String liveUrl;
    private final String infoUrl;
    private final String starListUrl;
    private final String resultsUrl;
    private final String registrationUrl;

    private SourceInfo(SourceName name, List<String> sourceIds, String liveUrl, String infoUrl,
                       String starListUrl, String resultsUrl, String registrationUrl) {
        this.name = name;
        this.sourceIds = sourceIds;
        this.liveUrl = liveUrl;
        this.infoUrl = infoUrl;
        this.starListUrl = starListUrl;
        this.resultsUrl = resultsUrl;
        this.registrationUrl = registrationUrl;
    }

    public SourceName name() {
        return name;
    }

    public List<String> sourceIds() {
        return sourceIds;
    }

    public String liveUrl() {
        return liveUrl;
    }

    public String infoUrl() {
        return infoUrl;
    }

    public String starListUrl() {
        return starListUrl;
    }

    public String resultsUrl() {
        return resultsUrl;
    }

    public String registrationUrl() {
        return registrationUrl;
    }

    public SourceInfo withName(SourceName newName) {
        if (newName == null) return this;
        return new SourceInfo(newName, sourceIds, liveUrl, infoUrl, starListUrl, resultsUrl, registrationUrl);
    }

    public SourceInfo withSourceIds(List<String> newSourceIds) {
        if (newSourceIds == null) return this;
        return new SourceInfo(name, newSourceIds, liveUrl, infoUrl, starListUrl, resultsUrl, registrationUrl);
    }

    public SourceInfo withLiveUrl(String newUrl) {
        if (newUrl == null) return this;
        return new SourceInfo(name, sourceIds, newUrl, infoUrl, starListUrl, resultsUrl, registrationUrl);
    }

    public SourceInfo withInfoUrl(String newUrl) {
        if (newUrl == null) return this;
        return new SourceInfo(name, sourceIds, liveUrl, newUrl, starListUrl, resultsUrl, registrationUrl);
    }

    public SourceInfo withStartListUrl(String newUrl) {
        if (newUrl == null) return this;
        return new SourceInfo(name, sourceIds, liveUrl, infoUrl, newUrl, resultsUrl, registrationUrl);
    }

    public SourceInfo withResultsUrl(String newUrl) {
        if (newUrl == null) return this;
        return new SourceInfo(name, sourceIds, liveUrl, infoUrl, starListUrl, newUrl, registrationUrl);
    }

    public SourceInfo withRegistrationUrl(String newUrl) {
        if (newUrl == null) return this;
        return new SourceInfo(name, sourceIds, liveUrl, infoUrl, starListUrl, resultsUrl, newUrl);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SourceInfo that = (SourceInfo) o;
        return sourceIds.equals(that.sourceIds) &&
                liveUrl.equals(that.liveUrl) &&
                infoUrl.equals(that.infoUrl) &&
                starListUrl.equals(that.starListUrl) &&
                resultsUrl.equals(that.resultsUrl) &&
                registrationUrl.equals(that.registrationUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceIds, liveUrl, infoUrl, starListUrl, resultsUrl, registrationUrl);
    }
}
