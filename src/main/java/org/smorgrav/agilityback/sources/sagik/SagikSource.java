package org.smorgrav.agilityback.sources.sagik;

import org.smorgrav.agilityback.model.Competition;
import org.smorgrav.agilityback.model.Event;
import org.smorgrav.agilityback.model.SourceName;
import org.smorgrav.agilityback.sources.Source;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * The competition data in sagik is devided into a past (results) and a future (taevlingar) page.
 * For competitions that is not really past or future we try to check both pages for info - jsut in case
 */
public class SagikSource implements Source {

    @Override
    public SourceName name() {
        return SourceName.sagik;
    }

    @Override
    public List<Competition> fetchCompetitions(LocalDate fromIncluding, LocalDate toIncluding) throws IOException {
        List<Competition> shallowCompetitions = new ArrayList<>();
        if (toIncluding.isAfter(LocalDate.now().minusDays(1)))
            shallowCompetitions.addAll(TaevlingarParser.taevlingar(fromIncluding, toIncluding));
        if (fromIncluding.isBefore(LocalDate.now().plusDays(1)))
            shallowCompetitions.addAll(ResultatParser.resultat(fromIncluding, toIncluding));

        return shallowCompetitions;
    }

    @Override
    public void fetchEvents(Competition competition) throws IOException {
        if (competition.events().size() > 0) return; //Assume this is already loaded
        if (competition.toDate().isAfter(LocalDate.now().minusDays(1)))
            competition.events().addAll(TaevlingarParser.loppLista(competition));
        if (competition.fromDate().isBefore(LocalDate.now().plusDays(1)))
            competition.events().addAll(ResultatParser.spelResultat(competition));
    }

    @Override
    public void fetchEquipages(Competition competition, Event event) throws IOException {
        if (event.equipages().size() > 0) return; //Assume this is already loaded
        event.equipages().addAll(ResultatParser.resultatListan(competition, event));
    }
}
