package org.smorgrav.agilityback.sources.nkk;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;
import org.smorgrav.agilityback.model.Competition;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NKKSourceTest {

    @Test
    public void testSample2020Query() throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream("nkk/sok.html");
        Document doc = Jsoup.parse(is, "utf-8", NKKSource.searchUrl);
        List<Competition> comps = NKKSource.parse(doc, LocalDate.of(2020, 1, 1), LocalDate.of(2020, 12, 31));
        Assert.assertEquals(132, comps.size());
    }

    @Test
    public void testDateParsing() {
        LocalDate date = LocalDate.parse("04.12.2020", DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

}