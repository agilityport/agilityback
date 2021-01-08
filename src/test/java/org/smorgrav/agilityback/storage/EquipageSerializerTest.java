package org.smorgrav.agilityback.storage;

import org.junit.Assert;
import org.junit.Test;
import org.smorgrav.agilityback.model.Dog;
import org.smorgrav.agilityback.model.Equipage;
import org.smorgrav.agilityback.model.Person;

import java.time.LocalDateTime;
import java.util.Map;

public class EquipageSerializerTest {
    @Test
    public void serialization_round_trip_empty_event() {
        Map<String, Object> serialized = EquipageSerializer.serialize(Equipage.EMPTY);

        Equipage back = EquipageSerializer.deserialize(new StorageValues(serialized));
        Assert.assertEquals(Equipage.EMPTY, back);
    }

    @Test
    public void serialization_round_trip_full_event() {
        Equipage event = Equipage.EMPTY
                .withDog(Dog.EMPTY
                        .withName("Frodo")
                        .withSourceId("sourcy")
                        .withBreed("breedy")
                        .withId("Idy"))
                .withFault(10.4)
                .withPlace(2)
                .withRunTime(45.67)
                .withStartNumber(3)
                .withHandler(Person.EMPTY
                        .withName("Toby")
                        .withClub("Clubby")
                        .withId("yboT")
                        .withSourceId("externalId"))
                .withActualStart(LocalDateTime.of(2012, 12, 12, 12, 12, 12))
                .withEstimatedStart(LocalDateTime.of(2012, 12, 12, 12, 12, 13));

        Map<String, Object> serialized = EquipageSerializer.serialize(event);
        Equipage back = EquipageSerializer.deserialize(new StorageValues(serialized));
        Assert.assertEquals(event, back);
    }
}
