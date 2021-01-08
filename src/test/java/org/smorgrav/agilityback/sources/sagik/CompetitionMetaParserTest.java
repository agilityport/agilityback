package org.smorgrav.agilityback.sources.sagik;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;
import org.smorgrav.agilityback.model.Organiser;

import java.io.IOException;
import java.io.InputStream;

public class CompetitionMetaParserTest {

    @Test
    public void parsing_organizer_from_lopplista() throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream("sagik/lopplista.html");
        Document doc = Jsoup.parse(is, "utf-8", TaevlingarParser.searchUrl);
        Organiser organiser = OrganizerParser.parseOrganizer(doc);
        Assert.assertEquals("Nutrolin Arena", organiser.organizerName());
    }

    @Test
    public void parsing_organizer_from_spelresultat() throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream("sagik/spel_resultat.html");
        Document doc = Jsoup.parse(is, "utf-8", TaevlingarParser.searchUrl);
        Organiser organiser = OrganizerParser.parseOrganizer(doc);
        Assert.assertEquals("Göteborg Mölndal BK", organiser.organizerName());
    }
}
