package org.smorgrav.agilityback.sources.hundestevner;

import org.smorgrav.agilityback.model.Competition;
import org.smorgrav.agilityback.model.Event;
import org.smorgrav.agilityback.model.SourceName;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class HundestevnerLocalClient {

    public static List<Competition> fetchCompetitions() {
        Competition comp = Competition.EMPTY.withId("387").withName("NO 2018").withFromDate(
                LocalDate.of(2018, 10, 11)).withToDate(
                LocalDate.of(2018, 10, 14)).withSource(SourceName.hundestevner);
        return Collections.singletonList(comp);
    }

    private String fromFile(String fileName) {
        InputStream resourceStream = getClass().getResourceAsStream("/" + fileName);
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        try {
            while ((length = resourceStream.read(buffer)) != -1) result.write(buffer, 0, length);
            return result.toString("UTF-8");
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public List<Event> fetchEvents(String competitionId) {
        try {
            ClassHeaders headers = ClassHeaders.fromJson(fromFile("hundestevner/api/classes/event_header_id_" + competitionId));
            return headers.toEventInfo();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
}
