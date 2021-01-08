package org.smorgrav.agilityback.sources.sagik;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;
import org.smorgrav.agilityback.model.Competition;
import org.smorgrav.agilityback.model.Event;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TaevlingarParserTest {

    @Test
    public void parsing_taevlingar() throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream("sagik/taevlingar.html");
        Document doc = Jsoup.parse(is, "utf-8", TaevlingarParser.searchUrl);
        List<Competition> comps = TaevlingarParser.parseTaevlingar(doc, LocalDate.of(2021, 1, 1),
                LocalDate.of(2021, 12, 31));
        Assert.assertEquals(108, comps.size());
    }

    @Test
    public void parsing_lopplista_events() throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream("sagik/lopplista.html");
        Document doc = Jsoup.parse(is, "utf-8", TaevlingarParser.searchUrl);
        List<Event> events = TaevlingarParser.parseLopplistaEvents(doc, LocalDateTime.now());
        Assert.assertEquals(40, events.size());
    }
}