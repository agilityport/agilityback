package org.smorgrav.agilityback.sources.devent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.smorgrav.agilityback.model.Competition;
import org.smorgrav.agilityback.model.Event;
import org.smorgrav.agilityback.model.SourceName;
import org.smorgrav.agilityback.sources.Source;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class DeventSource implements Source {

    private final String URL = "https://firestore.googleapis.com/v1/projects/devent-no/databases/(default)/documents/events";

    @Override
    public SourceName name() {
        return SourceName.devent;
    }

    @Override
    public List<Competition> fetchCompetitions(LocalDate fromIncluding, LocalDate toIncluding) throws IOException {

        Connection connection = Jsoup.connect(URL).ignoreContentType(true).maxBodySize(0).header("Accept", "application/json").method(Connection.Method.GET);
        Connection.Response allEvents = connection.execute();
        Document doc = allEvents.parse();
        String bocy = doc.body().text();

        String end = bocy.substring(bocy.length() - 1000);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readTree(bocy);
        System.out.println(actualObj.toPrettyString());
        return Collections.emptyList();
    }

    @Override
    public void fetchEvents(Competition competition) throws IOException {

    }

    @Override
    public void fetchEquipages(Competition competition, Event event) throws IOException {

    }
}
