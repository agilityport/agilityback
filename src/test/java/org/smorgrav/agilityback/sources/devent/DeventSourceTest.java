package org.smorgrav.agilityback.sources.devent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.checkerframework.checker.units.qual.C;
import org.junit.Test;
import org.smorgrav.agilityback.model.Competition;
import org.smorgrav.agilityback.model.CompetitionStatus;
import org.smorgrav.agilityback.model.CompetitionType;
import org.smorgrav.agilityback.model.Event;
import org.smorgrav.agilityback.model.Organiser;
import org.smorgrav.agilityback.model.SourceName;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DeventSourceTest {

    private boolean getBool(JsonNode node, String fieldName, boolean def) {
        try {
            return Boolean.parseBoolean(node.get(fieldName).get("booleanValue").toString());
        } catch (Exception e) {
            return def;
        }
    }


    private double getDouble(JsonNode node, String fieldName, double def) {
        try {
            return Integer.parseInt(node.get(fieldName).get("doubleValue").toString());
        } catch (Exception e) {
            return def;
        }
    }


    private int getInteger(JsonNode node, String fieldName, int def) {
        try {
            return Integer.parseInt(node.get(fieldName).get("integerValue").toString());
        } catch (Exception e) {
            return def;
        }
    }

    private String getString(JsonNode node, String fieldName) {
        try {
            return node.get(fieldName).get("stringValue").toString();
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDate getDate(JsonNode node, String fieldName) {
        String date = getString(node, fieldName);
        if (date == null) {
            return null;
        }
        return LocalDate.parse(date);
    }

    @Test
    public void testFetchCompetitions() throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream("devent/devent.json");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readTree(is);
        JsonNode documents = actualObj.get("documents");
        for (Iterator<JsonNode> i = documents.iterator(); i.hasNext(); ) {
            JsonNode document = i.next();
            JsonNode toplevel = document.get("fields");
            JsonNode rings = toplevel.get("ringLayout");
            System.out.println("Hello");
            List<Event> events = new ArrayList<>();

            Competition compe = Competition.EMPTY
                    .withRandomId()
                    .withName(getString(toplevel,"name"))
                    .withFromDate(getDate(toplevel, "startDate"))
                    .withToDate(getDate(toplevel, "endDate"))
                    .withSource(SourceName.devent)
                    .withSourceId(getString(toplevel, "id"))
                    .withOrganiser(Organiser.EMPTY
                            .withOrganizerName(getString(toplevel, "organizer"))
                            .withContactPerson(getString(toplevel, "contactPerson"))
                            .withContactEmail(getString(toplevel, "contactEmail"))
                            .withContactPhone(getString(toplevel, "contactPhone"))
                            .withAddressLine1(getString(toplevel, "address"))
                    )
                    .withRegistrationDeadline(getDate(toplevel, "endOfTicketSale"))
                    .withEventSummary("") //TODO Generate
                    .withStatus(CompetitionStatus.OpenRegistration) //TODO Check closedForAttendance, dates, endOfTicketSale and if any results
                    .withType(CompetitionType.Official) //TODO Check existence of nkk ids
                    .withEvents(events);
        }
    }
}