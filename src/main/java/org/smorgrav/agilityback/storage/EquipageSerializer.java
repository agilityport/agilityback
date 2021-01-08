package org.smorgrav.agilityback.storage;

import org.smorgrav.agilityback.model.Dog;
import org.smorgrav.agilityback.model.Equipage;
import org.smorgrav.agilityback.model.Person;

import java.util.HashMap;
import java.util.Map;

public class EquipageSerializer {

    static Equipage deserialize(StorageValues fields) {
        return Equipage.EMPTY
                .withId(fields.id())
                .withStartNumber(fields.getInt("startNumber"))
                .withFault(fields.getDouble("fault"))
                .withRunTime(fields.getDouble("runTime"))
                .withPlace(fields.getInt("place"))
                .withEventId(fields.getString("eventId"))
                .withCompetitionId(fields.getString("competitionId"))
                .withEstimatedStart(SerializationUtils.deserialize(fields.getString("estimatedStart")))
                .withActualStart(SerializationUtils.deserialize(fields.getString("actualStart")))
                .withDog(Dog.EMPTY
                        .withId(fields.getString("dog.id"))
                        .withSourceId(fields.getString("dog.sourceId"))
                        .withName(fields.getString("dog.name"))
                        .withBreed(fields.getString("dog.breed")))
                .withHandler(Person.EMPTY
                        .withId(fields.getString("handler.id"))
                        .withSourceId(fields.getString("handler.sourceId"))
                        .withName(fields.getString("handler.name"))
                        .withClub(fields.getString("handler.club"))
                );
    }

    static Map<String, Object> serialize(Equipage equipage) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("id", equipage.id());
        fields.put("startNumber", equipage.startNumber());
        fields.put("fault", equipage.fault());
        fields.put("runTime", equipage.runTime());
        fields.put("place", equipage.place());
        fields.put("competitionId", equipage.competitionId());
        fields.put("eventId", equipage.eventId());
        fields.put("estimatedStart", SerializationUtils.serialize(equipage.estimatedStart()));
        fields.put("actualStart", SerializationUtils.serialize(equipage.actualStart()));
        fields.put("dog.id", equipage.dog().id());
        fields.put("dog.name", equipage.dog().name());
        fields.put("dog.breed", equipage.dog().breed());
        fields.put("dog.sourceId", equipage.dog().sourceId());
        fields.put("handler.name", equipage.handler().name());
        fields.put("handler.club", equipage.handler().club());
        fields.put("handler.sourceId", equipage.handler().sourceId());
        fields.put("handler.id", equipage.handler().id());

        return fields;
    }
}
