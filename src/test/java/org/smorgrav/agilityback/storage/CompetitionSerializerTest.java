package org.smorgrav.agilityback.storage;

import org.junit.Assert;
import org.junit.Test;
import org.smorgrav.agilityback.model.Competition;
import org.smorgrav.agilityback.model.CompetitionStatus;
import org.smorgrav.agilityback.model.CompetitionType;
import org.smorgrav.agilityback.model.Organiser;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class CompetitionSerializerTest {
    @Test
    public void serialization_round_trip_empty_event() {
        Map<String, Object> serialized = CompetitionSerializer.serialize(Competition.EMPTY);
        Competition back = CompetitionSerializer.deserialize(new StorageValues(serialized));
        Assert.assertEquals(Competition.EMPTY, back);
    }

    @Test
    public void serialization_round_trip_full_event() {
        Competition comp = Competition.EMPTY
                .withOrganiser(Organiser.EMPTY
                        .withContactPerson("Jojo")
                        .withContactPhone("phonyphone")
                        .withContactEmail("mailymail")
                        .withAddressLine1("lineyline")
                        .withAddressLine2("lololand")
                        .withCompetitionLeader("tobben")
                        .withOrganizerName("ateam")
                )
                .withEventSummary("Agility 3x and Team")
                .withStatus(CompetitionStatus.Planned)
                .withType(CompetitionType.Official)
                .withSourceIds(List.of("source1", "sorese2"))
                .withToDate(LocalDate.of(2012, 12, 12))
                .withFromDate(LocalDate.of(2012, 11, 12))
                .withName("Jumpytiy Jump")
                .withId("Di")
                .withRegistrationDeadline(LocalDate.of(2012, 11, 11));


        Map<String, Object> serialized = CompetitionSerializer.serialize(comp);
        Competition back = CompetitionSerializer.deserialize(new StorageValues(serialized));
        Assert.assertEquals(comp, back);
    }
}
