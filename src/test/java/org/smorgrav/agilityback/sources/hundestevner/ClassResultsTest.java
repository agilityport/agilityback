package org.smorgrav.agilityback.sources.hundestevner;

import com.google.common.io.CharStreams;
import org.junit.Test;
import org.smorgrav.agilityback.model.Equipage;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ClassResultsTest {

    private String fromFile(String fileName) {
        InputStream resourceStream = getClass().getResourceAsStream("/" + fileName);
        try {
            return CharStreams.toString(new InputStreamReader(resourceStream));
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Test
    public void fromJson() throws IOException {
        ClassResults cr = ClassResults.fromJson(fromFile("hundestevner/api/events/results/9164.json"));
        assertEquals(92, cr.results.size());
        assertEquals(37.7, cr.results.get(0).runTime, 0.001);
    }

    @Test
    public void toEventEquipageList() throws IOException {
        ClassResults cr = ClassResults.fromJson(fromFile("hundestevner/api/events/results/9164.json"));
        List<Equipage> list = cr.toEventEquipageList();
        assertEquals(92, list.size());
    }
}