package org.smorgrav.agilityback.sources.sagik;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.smorgrav.agilityback.model.Competition;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class SagikSourceTest {

    @Test
    @Ignore
    public void test_live_taevling() throws IOException {
        SagikSource source = new SagikSource();
        // Fetch 2 days into the future as we only want to trigger the taevlinger part
        List<Competition> competitions = source.fetchCompetitions(LocalDate.now().plusDays(2), LocalDate.now().plusDays(7));
        Assert.assertTrue(competitions.size() > 0);
    }

    @Test
    @Ignore
    public void test_live_resultat() throws IOException {
        SagikSource source = new SagikSource();
        // Fetch some days into the past as we only want to trigger the result part
        List<Competition> competitions = source.fetchCompetitions(LocalDate.of(2020, 10, 1), LocalDate.of(2020, 10, 1));
        competitions.forEach(competition -> {
            competition.events().forEach(event -> {
                try {
                    source.fetchEquipages(competition, event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
        Assert.assertTrue(competitions.size() > 0);
    }
}
