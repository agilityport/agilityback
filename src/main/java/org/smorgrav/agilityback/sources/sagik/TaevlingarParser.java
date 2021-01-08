package org.smorgrav.agilityback.sources.sagik;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.smorgrav.agilityback.model.Competition;
import org.smorgrav.agilityback.model.CompetitionStatus;
import org.smorgrav.agilityback.model.CompetitionType;
import org.smorgrav.agilityback.model.Event;
import org.smorgrav.agilityback.model.SourceName;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaevlingarParser {

    private static final DateTimeFormatter DATEFORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    static String searchUrl = "http://agilitydata.se/taevlingar/";

    /**
     * Fetch landing page and parse competition info for all competitions listed there.
     * <p>
     * This includes also events - because some of the competition data is only listed
     * together with the organizer data.
     */
    static List<Competition> taevlingar(LocalDate from, LocalDate to) throws IOException {
        Connection connection = Jsoup.connect(searchUrl);
        Connection.Response landingPage = connection.method(Connection.Method.POST).execute();
        Document doc = landingPage.parse();
        List<Competition> competitions = parseTaevlingar(doc, from, to);

        // Session info
        String sessionId = landingPage.cookie("ASP.NET_SessionId");
        String ufprt = doc.select("input[name=\"ufprt\"]").attr("value");

        // For all competitions some of the info we need is in each lopplista - so we
        // fetch that too.
        List<Competition> finalList = new ArrayList<>();
        for (Competition competition : competitions) {
            Connection.Response res = connection
                    .cookie("ASP.NET_SessionId", sessionId)
                    .data("competitionKey", competition.sourceIds().get(0), "action", "NextPage", "ufprt", ufprt)
                    .method(Connection.Method.POST)
                    .followRedirects(true)
                    .execute();

            Document loppListaHtml = res.parse();
            List<Event> events = parseLopplistaEvents(loppListaHtml, competition.fromDate().atTime(12, 00)).stream()
                    .map(event -> event.withCompetitionId(competition.id()))
                    .collect(Collectors.toList());
            Competition annotatedCompetition = competition.withOrganiser(OrganizerParser.parseOrganizer(loppListaHtml));
            annotatedCompetition.events().addAll(events);
            finalList.add(annotatedCompetition);
        }

        return finalList;
    }

    /**
     * Fetch all events that belongs to that specific competition, but not nessesarily the equipages.
     * <p>
     * To do this we need to first fetch landing page and then navigate to the loppLista.
     */
    static List<Event> loppLista(Competition competition) throws IOException {
        Connection connection = Jsoup.connect(searchUrl);
        Connection.Response landingPage = connection.method(Connection.Method.POST).execute();
        Document doc = landingPage.parse();

        // Session info
        String sessionId = landingPage.cookie("ASP.NET_SessionId");
        String ufprt = doc.select("input[name=\"ufprt\"]").attr("value");

        Connection.Response res = connection
                .cookie("ASP.NET_SessionId", sessionId)
                .data("competitionKey", competition.sourceIds().get(0), "ufprt", ufprt)
                .method(Connection.Method.POST)
                .followRedirects(true)
                .execute();

        Document loppListaHtml = res.parse();
        List<Event> events = parseLopplistaEvents(loppListaHtml, competition.fromDate().atTime(12, 00));
        return events.stream().map(event ->
                event
                        .withRandomId()
                        .withCompetitionId(competition.id())
        ).collect(Collectors.toList());
    }

    static List<Event> parseLopplistaEvents(Document document, LocalDateTime schedule) {
        document.outputSettings(new Document.OutputSettings().prettyPrint(false));
        Elements tables = document.select("tbody");
        List<Event> events = new ArrayList<>();

        for (Element table : tables) {
            Elements rows = table.select("tr");
            for (Element row : rows) {
                Elements cells = row.select("td");
                String size = cells.get(1).text();
                String name = cells.get(2).text().trim();
                String sourceId = cells.get(9).text();
                events.add(Event.newTrial(sourceId, name, ParseUtils.size(size), ParseUtils.trial(name), schedule));
            }
        }

        return events;
    }

    static List<Competition> parseTaevlingar(Document document, LocalDate fromIncluding, LocalDate toIncluding) {
        document.outputSettings(new Document.OutputSettings().prettyPrint(false));
        Element table = document.select("table").get(0);
        Elements rows = table.select("tr");
        List<Competition> competitions = new ArrayList<>();

        rows.forEach(row -> {
            Elements cells = row.select("td");
            if (cells.size() == 10) {
                String competitionId = cells.get(7).html();
                if (!competitionId.equals("0") && !competitionId.isBlank()) {
                    Element dateCell = cells.get(1);
                    LocalDate[] dates = ParseUtils.dates(dateCell.select("div").html(), fromIncluding, toIncluding);

                    if (dates[0] != null) {
                        Element nameCell = cells.get(2);
                        String name = nameCell.select("div").text();
                        Element eventSummeryCell = cells.get(3);
                        String eventSummery = eventSummeryCell.select("div").text();
                        String regDateStr = cells.get(5).html().trim();
                        LocalDate regDate = !regDateStr.isBlank() ? LocalDate.parse(regDateStr.trim(), DATEFORMAT) : dates[0];
                        String status = cells.get(9).html();

                        // TODO This seems to simple to be true - but I cannot find any examples in the current sagik site
                        CompetitionType competitionType = status.equals("ANSÖKT") ? CompetitionType.AppliedFor : CompetitionType.Official;

                        competitions.add(Competition.EMPTY
                                .withRandomId()
                                .withSource(SourceName.sagik)
                                .withName(name)
                                .withType(competitionType)
                                .withStatus(CompetitionStatus.Planned)
                                .withFromDate(dates[0])
                                .withToDate(dates[1])
                                .withEventSummary(eventSummery)
                                .withRegistrationDeadline(regDate)
                                .withSourceIds(List.of(competitionId)));
                    }
                }
            }
        });

        return competitions;
    }
}
