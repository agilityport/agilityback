package org.smorgrav.agilityback.storage;

import org.junit.Assert;
import org.junit.Test;
import org.smorgrav.agilityback.model.Event;
import org.smorgrav.agilityback.model.EventType;
import org.smorgrav.agilityback.model.Size;
import org.smorgrav.agilityback.model.TrialType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


public class EventSerializerTest {
    @Test
    public void serialization_round_trip_empty_event() {
        Map<String, Object> serialized = EventSerializer.serialize(Event.EMPTY);

        Event back = EventSerializer.deserialize(new StorageValues("", serialized));
        Assert.assertEquals(Event.EMPTY, back);
    }

    @Test
    public void serialization_round_trip_full_event() {
        Event event = Event.EMPTY
                .withEventType(EventType.buffer)
                .withEnded(LocalDateTime.of(2020, 12, 22, 1, 2, 2))
                .withDuration(Duration.ofSeconds(23))
                .withMinDuration(Duration.ofHours(2))
                .withName("myname")
                .withRing("ringiting")
                .withSizes(List.of(Size.large, Size.medium))
                .withEstimatedStart(LocalDateTime.of(2020, 12, 22, 0, 2, 3))
                .withId("myid")
                .withSchedule(LocalDateTime.of(2020, 12, 22, 0, 2, 3))
                .withSourceId("4343")
                .withStarted(LocalDateTime.of(2020, 12, 22, 0, 2, 3))
                .withTrialType(TrialType.A1);


        Map<String, Object> serialized = EventSerializer.serialize(event);
        Event back = EventSerializer.deserialize(new StorageValues(serialized));
        Assert.assertEquals(event, back);
    }
}