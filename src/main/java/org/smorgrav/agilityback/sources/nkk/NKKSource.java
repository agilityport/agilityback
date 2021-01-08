package org.smorgrav.agilityback.sources.nkk;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.smorgrav.agilityback.model.Competition;
import org.smorgrav.agilityback.model.Event;
import org.smorgrav.agilityback.model.Organiser;
import org.smorgrav.agilityback.model.SourceName;
import org.smorgrav.agilityback.sources.Source;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

public class NKKSource implements Source {

    private static final Logger log = Logger.getLogger(NKKSource.class.getName());
    static String searchUrl = "https://www.dogweb.no/nkk/public/openPage/frame/terminliste-utstilling-lydighet-agility/sok.html";

    public static List<Competition> scrapeUrl(final int year) throws IOException {

        final Connection.Response res = Jsoup.connect(searchUrl)
                .data("Type", "3", "AAR", "" + year)
                .method(Connection.Method.POST)
                .execute();

        final Document doc = res.parse();
        return parse(doc, LocalDate.of(year - 1, 12, 31), LocalDate.of(year + 1, 1, 1));
    }

    static String[] lines(final Element element) {
        element.select("br").after("\\n");
        final String preClean = element.html().replaceAll("\\\\n", "\n");
        final String cleaned = Jsoup.clean(preClean, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
        return Arrays.stream(cleaned.split("[\\r\\n]+")).map(String::trim).filter(e -> !e.isBlank()).toArray(String[]::new);
    }

    static Organiser extractOrganiser(final String[] lines) {
        Organiser org = Organiser.EMPTY;
        if (lines.length > 0) {
            org = org.withOrganizerName(lines[0]);
        }
        if (lines.length > 3) {
            org = org.withAddressLine1(lines[1])
                    .withAddressLine1(lines[2])
                    .withContactEmail(lines[3]);
        }

        return org;
    }

    static String extractSourceId(final String[] lines) {
        if (lines.length > 0) {
            return lines[0];
        }
        return "";
    }

    static LocalDate[] extractDates(final String[] lines, final LocalDate earliest, final LocalDate latest) {
        final LocalDate[] dates = new LocalDate[2];
        if (lines.length == 2) {
            final LocalDate from = LocalDate.parse(lines[0].trim(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            if (from.isAfter(earliest) && from.isBefore(latest)) {
                dates[0] = from;
            }

            final LocalDate to = LocalDate.parse(lines[1].trim(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            if ((to.isEqual(from) || to.isAfter(from)) && to.isBefore(latest)) {
                dates[1] = to;
            }
        }
        return dates;
    }

    static List<Competition> parse(final Document document, final LocalDate earliest, final LocalDate latest) {
        document.outputSettings(new Document.OutputSettings().prettyPrint(false));
        final Element outerTable = document.select("table").get(0);
        final Element innerTable = outerTable.select("table").get(0);
        final Elements rows = innerTable.select("tr");
        final List<Competition> competitions = new ArrayList<>();

        rows.forEach(row -> {
            final Elements cells = row.select("td");
            if (cells.size() == 6) {
                final LocalDate[] dates = extractDates(lines(cells.get(0)), earliest, latest);
                
                final Organiser organiser = extractOrganiser(lines(cells.get(1)));
                final Competition competition = Competition.EMPTY
                        .withRandomId()
                        .withSource(SourceName.nkk)
                        .withFromDate(dates[0])
                        .withToDate(dates[1])
                        .withOrganiser(organiser)
                        .withSourceIds(List.of(extractSourceId(lines(cells.get(2)))))
                        .withName(organiser.organizerName());

                if (competition.isValid()) {
                    competitions.add(competition);
                }
            }
        });

        return mergeCompetitions(competitions);
    }

    static List<Competition> mergeCompetitions(final List<Competition> competitions) {
        competitions.sort(new Comparator<>() {
            @Override
            public int compare(final Competition o1, final Competition o2) {
                final int byName = o1.name().compareTo(o2.name());
                if (byName == 0) {
                    return o1.fromDate().compareTo(o2.fromDate());
                }
                return byName;
            }
        });

        // Now The list is sorted so just iterate over and find competitions with the same name and continuous dates
        if (competitions.size() > 1) {
            final List<Competition> mergedComps = new ArrayList<>();
            Competition last = competitions.get(0);
            for (int i = 1; i < competitions.size(); i++) {
                final Competition next = competitions.get(i);
                if (next.name().equals(last.name()) && next.fromDate().isBefore(last.toDate().plusDays(2))) {
                    final LocalDate newToDate = last.toDate().isAfter(next.toDate()) ? last.toDate() : next.toDate();
                    last = last.withToDate(newToDate);
                    last.addSourceIds(next.sourceIds());
                } else {
                    mergedComps.add(last);
                    last = next;
                }
            }

            return mergedComps;
        }

        return competitions;
    }

    @Override
    public SourceName name() {
        return SourceName.nkk;
    }

    @Override
    public List<Competition> fetchCompetitions(final LocalDate fromIncluding, final LocalDate toIncluding) throws IOException {
        return null;
    }

    @Override
    public void fetchEvents(final Competition competition) throws IOException {

    }

    @Override
    public void fetchEquipages(final Competition competition, final Event event) throws IOException {

    }
}

