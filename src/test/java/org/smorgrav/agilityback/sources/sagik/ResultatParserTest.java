package org.smorgrav.agilityback.sources.sagik;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;
import org.smorgrav.agilityback.model.Competition;
import org.smorgrav.agilityback.model.Equipage;
import org.smorgrav.agilityback.model.Event;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

public class ResultatParserTest {

    @Test
    public void parse_resultat() throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream("sagik/resultat.html");
        Document doc = Jsoup.parse(is, "utf-8", TaevlingarParser.searchUrl);

        List<Competition> comps = ResultatParser.parseResultat(doc, LocalDate.MIN, LocalDate.MAX);
        Assert.assertEquals(1833, comps.size());
    }

    @Test
    public void parse_spel_resultat() throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream("sagik/spel_resultat.html");
        Document doc = Jsoup.parse(is, "utf-8", TaevlingarParser.searchUrl);

        List<Event> events = ResultatParser.parseSpelResultat(doc);
        Assert.assertEquals(50, events.size());
    }

    @Test
    public void parse_resultatlistan() throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream("sagik/start-och-resultatlistan_results.html");
        Document doc = Jsoup.parse(is, "utf-8", TaevlingarParser.searchUrl);

        List<Equipage> euipages = ResultatParser.parseResultatListan(doc);
        Assert.assertEquals(6, euipages.size());
    }

    @Test
    public void parse_startlistan() throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream("sagik/start-och-resultatlistan.html");
        Document doc = Jsoup.parse(is, "utf-8", TaevlingarParser.searchUrl);

        List<Equipage> euipages = ResultatParser.parseResultatListan(doc);
        Assert.assertEquals(6, euipages.size());
    }

    @Test
    public void parse_konkurrentlistan() throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream("sagik/konkurrent-resultatlistan.html");
        Document doc = Jsoup.parse(is, "utf-8", TaevlingarParser.searchUrl);

        List<Equipage> euipages = ResultatParser.parseResultatListan(doc);
        Assert.assertEquals(6, euipages.size());
    }
}