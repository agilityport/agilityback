package org.smorgrav.agilityback.sources.sagik;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.smorgrav.agilityback.model.Competition;
import org.smorgrav.agilityback.model.CompetitionStatus;
import org.smorgrav.agilityback.model.CompetitionType;
import org.smorgrav.agilityback.model.Dog;
import org.smorgrav.agilityback.model.Equipage;
import org.smorgrav.agilityback.model.EquipageState;
import org.smorgrav.agilityback.model.Event;
import org.smorgrav.agilityback.model.Person;
import org.smorgrav.agilityback.model.RingDate;
import org.smorgrav.agilityback.model.SourceName;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ResultatParser {

    private static final Logger LOG = Logger.getLogger(ResultatParser.class.getName());

    static String searchUrl = "http://agilitydata.se/resultat/";
    static String resultUrl = "http://agilitydata.se/resultat/spel-resultat/";

    static NumberFormat numberFormat = NumberFormat.getInstance(Locale.FRANCE); // uses comma separator for numbers

    /**
     * This parses the landing page for results - its a list of all historic competitions.
     * Then for all within the date range we fetch the spelResultat to get the organizer data -
     * and while we are at it - get the events - but not equipages
     */
    static List<Competition> resultat(LocalDate fromIncluding, LocalDate toIncluding) throws IOException {
        Connection connection = Jsoup.connect(searchUrl);
        Connection.Response landingPage = connection.method(Connection.Method.POST).execute();
        Document doc = landingPage.parse();
        List<Competition> competitions = parseResultat(doc, fromIncluding, toIncluding);

        // Session info
        String sessionId = landingPage.cookie("ASP.NET_SessionId");
        String ufprt = doc.select("input[name=\"ufprt\"]").attr("value");

        // For all competitions some of the info we need is in each spelResultat - so we
        // fetch that too.
        List<Competition> finalList = new ArrayList<>();
        for (Competition competition : competitions) {
            Connection.Response res = connection
                    .cookie("ASP.NET_SessionId", sessionId)
                    .data("competitionKey", competition.sourceIds().get(0), "ufprt", ufprt, "action", "NextPage")
                    .method(Connection.Method.POST)
                    .followRedirects(true)
                    .execute();

            Document spelResultat = res.parse();
            Competition annotatedCompetition = competition.withOrganiser(OrganizerParser.parseOrganizer(spelResultat));
            List<Event> events = parseSpelResultat(spelResultat);
            annotatedCompetition.events().addAll(events.stream().map(event -> event
                    .withRandomId()
                    .withCompetitionId(competition.id()))
                    .collect(Collectors.toList()));
            finalList.add(annotatedCompetition);
        }

        return finalList;
    }

    /**
     * This parses events from the resultat-spel page you reach when clicking on a competition from
     * the result landing page.
     */
    static List<Event> spelResultat(Competition competition) throws IOException {
        Connection connection = Jsoup.connect(searchUrl);
        Connection.Response landingPage = connection.method(Connection.Method.POST).execute();
        Document doc = landingPage.parse();

        // Session info
        String sessionId = landingPage.cookie("ASP.NET_SessionId");
        String ufprt = doc.select("input[name=\"ufprt\"]").attr("value");

        Connection.Response res = connection
                .cookie("ASP.NET_SessionId", sessionId)
                .data("competitionKey", competition.sourceIds().get(0), "ufprt", ufprt, "action", "NextPage")
                .method(Connection.Method.POST)
                .followRedirects(true)
                .execute();

        Document spelResultatHtml = res.parse();
        return parseSpelResultat(spelResultatHtml).stream().map(event -> event
                .withCompetitionId(competition.id()))
                .collect(Collectors.toList());
    }

    static List<Equipage> resultatListan(Competition competition, Event event) throws IOException {
        Connection connection = Jsoup.connect(resultUrl);
        Connection.Response landingPage = connection.method(Connection.Method.POST).execute();
        Document doc = landingPage.parse();

        // Session info
        String sessionId = landingPage.cookie("ASP.NET_SessionId");
        String ufprt = doc.select("input[name=\"ufprt\"]").attr("value");

        // Now go directly to the page where we get equipages
        Connection connection2 = Jsoup.connect(resultUrl);
        Connection.Response konkurrentListanResponse = connection2
                .cookie("ASP.NET_SessionId", sessionId)
                .data("competitionKey", competition.sourceIds().get(0), "action", "ViewList", "GameKey", event.sourceId(), "ufprt", ufprt)
                .method(Connection.Method.POST)
                .followRedirects(true)
                .execute();
        List<Equipage> konkurrentListan = parseResultatListan(konkurrentListanResponse.parse());

        Connection.Response startListanResponse = Jsoup.connect(resultUrl)
                .cookie("ASP.NET_SessionId", sessionId)
                .data("competitionKey", competition.sourceIds().get(0), "action", "ViewList", "GameKey", event.sourceId(), "ufprt", ufprt, "PageValue", "ViewList")
                .method(Connection.Method.POST)
                .followRedirects(true)
                .execute();
        List<Equipage> startListan = parseResultatListan(startListanResponse.parse());

        Connection.Response resultListanResponse = Jsoup.connect(resultUrl)
                .cookie("ASP.NET_SessionId", sessionId)
                .data("competitionKey", competition.sourceIds().get(0), "action", "ViewList", "GameKey", event.sourceId(), "ufprt", ufprt, "PageValue", "ResultList")
                .method(Connection.Method.POST)
                .followRedirects(true)
                .execute();
        List<Equipage> resultatListan = parseResultatListan(resultListanResponse.parse());

        //Organize all equipages as a map from derived Id - a tool to stich this together:
        Map<String, Equipage> uniqueEquipages = new HashMap<>();
        konkurrentListan.forEach(equipage -> uniqueEquipages.put(equipage.derivedId(), equipage));

        // Start listan is always a superset og konkurrent listan so just replace the equipage
        startListan.forEach(equipage -> uniqueEquipages.put(equipage.derivedId(), equipage));

        // The results are the full record minus the start number - so use this with annotated with the startNumber
        resultatListan.forEach(equipage -> {
            // This should not occure I think - not sure what the condition is then
            if (uniqueEquipages.containsKey(equipage.derivedId())) {
                Equipage startListItem = uniqueEquipages.get(equipage.derivedId());
                Equipage annotatedItem = equipage.withStartNumber(startListItem.startNumber());
                uniqueEquipages.put(equipage.derivedId(), annotatedItem);
            } else {
                uniqueEquipages.put(equipage.derivedId(), equipage);
            }
        });

        // Then at last, annotate event with competition id and event id, and return
        List<Equipage> finalList = new ArrayList<>(uniqueEquipages.values());
        return finalList.stream().map(equipage -> equipage
                .withCompetitionId(competition.id())
                .withEventId(event.id()))
                .collect(Collectors.toList());
    }

    static List<Competition> parseResultat(Document document, LocalDate fromIncluding, LocalDate toIncluding) {
        document.outputSettings(new Document.OutputSettings().prettyPrint(false));
        Elements rows = document.select("tbody tr");

        List<Competition> competitions = new ArrayList<>();
        for (Element row : rows) {
            Elements cells = row.select("td");
            String compId = cells.get(5).text();

            if (compId.equals("0")) {
                continue; //This row is used for advertising
            }

            LocalDate[] dates = ParseUtils.dates(cells.get(1).text(), fromIncluding, toIncluding);
            if (dates[0] == null) {
                continue; // TODO log when this happens?
            }

            String nameStr = cells.get(2).text();

            // As we parse cancelled out in seperate field we can remove it from name
            String name = nameStr.contains("INSTÄLLD ") ? nameStr.replaceAll("INSTÄLLD ", "").trim() : nameStr;
            name = name.contains("INSTÄLLD! ") ? name.replaceAll("INSTÄLLD! ", "").trim() : name;

            String eventSummery = cells.get(3).text();
            CompetitionStatus status = parseStatus(cells.get(4).text(), nameStr);
            CompetitionType type = parseType(cells.get(4).text(), eventSummery);

            competitions.add(Competition.EMPTY
                    .withRandomId()
                    .withSource(SourceName.sagik)
                    .withSourceId(compId)
                    .withStatus(status)
                    .withEventSummary(eventSummery)
                    .withToDate(dates[1])
                    .withFromDate(dates[0])
                    .withRegistrationDeadline(dates[1].minusDays(1))
                    .withName(name)
                    .withType(type));
        }

        return competitions;
    }

    /**
     * This parses the competition page (resultat-spel) you reach when clicking on a competition from
     * the result landing page.
     */
    static List<Event> parseSpelResultat(Document document) {
        document.outputSettings(new Document.OutputSettings().prettyPrint(false));

        // Get ringdate ids (to separate which date the events are on)
        Elements tabs = document.select("a[role=\"tab\"]");
        List<RingDate> ringDates = new ArrayList<>();
        for (Element tab : tabs) {
            String rawId = tab.attr("href"); //This is the element id - keep the #
            String dateStr = tab.text();
            RingDate date = new RingDate(rawId, ParseUtils.date(dateStr));
            ringDates.add(date);
        }

        List<Event> events = new ArrayList<>();
        for (RingDate date : ringDates) {
            Elements panels = document.select(date.name());
            if (panels.size() != 1) {
                LOG.warning("Ring date did correspond to " + panels.size() + " panels and not 1 - ignoring " + date.name());
                continue;
            }
            Elements rows = panels.get(0).select("tbody tr");
            for (Element row : rows) {
                Elements cells = row.select("td");
                String size = cells.get(1).text();
                String name = cells.get(2).text();
                String judge = cells.get(4).text(); // TODO
                String eventId = cells.get(8).text();
                events.add(Event.newTrial(eventId, name, ParseUtils.size(size), ParseUtils.trial(name), date.dateTime(LocalTime.of(12, 00))));
            }
        }

        return events;
    }

    static String dogSourceId(Element dogElement) {
        String dogSourceId = dogElement.select("a").attr("href");
        if (dogSourceId.isBlank()) {
            return null;
        }
        String[] substr = dogSourceId.split("=");
        if (substr.length == 3) {
            String[] substr2 = substr[1].split("&");
            if (substr2.length == 2) {
                return substr2[0].replace("%2F", "/");
            }
        }

        return null;
    }

    static Equipage parseCommonEquipageCells(Elements cells) {
        int startIndex = cells.size() > 4 ? 1 : 0;
        String handlerStr = cells.get(startIndex).text();
        String dogStr = cells.get(startIndex + 1).text();
        String sourceId = dogSourceId(cells.get(startIndex + 1));
        String breed = cells.get(startIndex + 2).text();
        String club = cells.get(startIndex + 3).text();
        Dog dog = Dog.EMPTY.withName(dogStr).withBreed(breed).withSourceId(sourceId);
        Person handler = Person.EMPTY.withName(handlerStr).withClub(club);
        return Equipage.EMPTY.withRandomId().withDog(dog).withHandler(handler);
    }

    /**
     * This parses the result page (start-ock-resultatlistan) for one individual event.
     */
    static List<Equipage> parseResultatListan(Document document) {
        document.outputSettings(new Document.OutputSettings().prettyPrint(false));

        Elements rows = document.select("tbody tr");
        List<Equipage> equipages = new ArrayList<>();
        for (Element row : rows) {
            Elements cells = row.select("td");

            Equipage equipage = parseCommonEquipageCells(cells);

            if (cells.size() == 6) {
                String startNumber = cells.get(0).text().trim();
                equipage = equipage.withStartNumber(Integer.parseInt(startNumber)); // TODO can this be bogus?
            }

            if (cells.size() == 8) {
                String place = cells.get(0).text().trim();
                String faults = cells.get(5).text().trim();
                String time = cells.get(6).text().trim();
                String cert = cells.get(7).text(); // TODO how to use this.

                EquipageState state = time.equals("Disk") ? EquipageState.DISQUALIFIED : time.equals("Struken") ? EquipageState.NOSHOW : EquipageState.FINISHED;
                equipage.setState(state);

                // Sometimes time is disk and place is given - so be carefull here
                if (!place.isBlank() && !place.equals("-") && state.equals(EquipageState.FINISHED)) {
                    equipage.setPlace(Integer.parseInt(place));
                    try {
                        if (!faults.isBlank()) {
                            equipage.setFault(numberFormat.parse(faults).doubleValue());
                        }
                        if (!time.isBlank()) {
                            equipage.setRunTime(numberFormat.parse(time).doubleValue());
                        }
                    } catch (ParseException e) {
                        String msg = String.format("Failed to parse number from 'resultatlistan' - one of place '%s' faults '%s' or time '%s'", place, faults, time);
                        LOG.warning(msg);
                    }
                }
            }

            equipages.add(equipage);
        }

        return equipages;
    }

    static CompetitionStatus parseStatus(String status, String name) {
        String statusInvariant = status.toLowerCase().trim();
        String nameInvariant = name.toLowerCase().trim();

        if (nameInvariant.contains("inställd")) {
            return CompetitionStatus.Cancelled;
        }

        if (statusInvariant.isBlank()) {
            return CompetitionStatus.Unknown;
        }

        switch (statusInvariant) {
            case "slutrapporterad":
                return CompetitionStatus.Archived;
            case "ansökt":
                return CompetitionStatus.Planned;
            case "öppen för anmälan":
                return CompetitionStatus.OpenRegistration;
            case "stängd för anmälan":
                return CompetitionStatus.ClosedRegistration;
            case "resultatrapporterad":
                return CompetitionStatus.Finished;
            default:
                return CompetitionStatus.Unknown;
        }
    }

    static CompetitionType parseType(String type, String eventSummery) {
        String typeInvariant = type.toLowerCase().trim();
        String summaryInvariant = eventSummery.toLowerCase().trim();

        if (summaryInvariant.contains("ineff")) {
            return CompetitionType.UnOfficial;
        }
        if ("ansökt".equals(typeInvariant)) {
            return CompetitionType.AppliedFor;
        }

        return CompetitionType.Official;
    }

}
