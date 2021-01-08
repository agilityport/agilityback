package org.smorgrav.agilityback.storage;

import org.smorgrav.agilityback.model.Event;
import org.smorgrav.agilityback.model.EventType;
import org.smorgrav.agilityback.model.Size;
import org.smorgrav.agilityback.model.TrialType;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventSerializer {

    static Duration fromLong(Long duration) {
        if (duration == null) {
            return Duration.ofSeconds(0);
        }
        return Duration.ofSeconds(duration);
    }

    static EventType parseType(String type) {
        if (type == null) {
            return EventType.unknown;
        }
        switch (type.toLowerCase()) {
            case "buffer":
                return EventType.buffer;
            case "coursewalk":
                return EventType.courseWalk;
            case "build":
                return EventType.build;
            case "trial":
                return EventType.trial;
            default:
                return EventType.unknown;
        }
    }

    static TrialType parseTrialType(String type) {
        if (type == null) {
            return TrialType.Other;
        }
        switch (type.toLowerCase()) {
            case "aopen":
                return TrialType.AOpen;
            case "ateam":
                return TrialType.ATeam;
            case "a1":
                return TrialType.A1;
            case "a2":
                return TrialType.A2;
            case "a3":
                return TrialType.A3;
            case "jopen":
                return TrialType.JOpen;
            case "jteam":
                return TrialType.ATeam;
            case "j1":
                return TrialType.J1;
            case "j2":
                return TrialType.J2;
            case "j3":
                return TrialType.J3;
            case "show":
                return TrialType.Show;
            default:
                return TrialType.Other;
        }
    }


    static List<Size> parseSizes(List<String> sizes) {
        List<Size> result = new ArrayList<>();
        sizes.forEach(s -> {
            switch (s.toLowerCase()) {
                case "xlarge":
                    result.add(Size.xlarge);
                    break;
                case "large":
                    result.add(Size.large);
                    break;
                case "medium":
                    result.add(Size.medium);
                    break;
                case "small":
                    result.add(Size.small);
                    break;
                case "xsmall":
                    result.add(Size.xsmall);
                    break;
                default:
                    result.add(Size.unknown);
            }
        });

        if (result.isEmpty()) {
            result.add(Size.unknown);
        }

        return result;
    }

    static List<String> serializeSizes(List<Size> sizes) {
        List<String> result = new ArrayList<>();
        sizes.forEach(size -> {
            result.add(size.name());
        });
        return result;
    }

    static Event deserialize(StorageValues fields) {
        return Event.EMPTY
                .withId(fields.id())
                .withCompetitionId(fields.getString("competitionId"))
                .withSourceId(fields.getString("sourceId"))
                .withName(fields.getString("name"))
                .withEventType(parseType(fields.getString("eventType")))
                .withSizes(parseSizes(fields.getStrList("sizes")))
                .withTrialType(parseTrialType(fields.getString("trialType")))
                .withRing(fields.getString("ring"))
                .withSchedule(SerializationUtils.deserialize(fields.getString("schedule")))
                .withDuration(fromLong(fields.getLong("duration")))
                .withMinDuration(fromLong(fields.getLong("minDuration")))
                .withEstimatedStart(SerializationUtils.deserialize(fields.getString("estimatedStart")))
                .withStarted(SerializationUtils.deserialize(fields.getString("started")))
                .withEnded(SerializationUtils.deserialize(fields.getString("ended")));
    }

    static Map<String, Object> serialize(Event event) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("id", event.id());
        fields.put("competitionId", event.competitionId());
        fields.put("sourceId", event.sourceId());
        fields.put("name", event.name());
        fields.put("eventType", event.eventType().name());
        fields.put("trialType", event.trialType().name());
        fields.put("sizes", serializeSizes(event.sizes()));
        fields.put("ring", event.ring());
        fields.put("schedule", SerializationUtils.serialize(event.schedule()));
        fields.put("duration", event.duration().getSeconds());
        fields.put("minDuration", event.minDuration().getSeconds());
        fields.put("estimatedStart", SerializationUtils.serialize(event.estimatedStart()));
        fields.put("started", SerializationUtils.serialize(event.started().orElse(null)));
        fields.put("ended", SerializationUtils.serialize(event.ended().orElse(null)));

        return fields;
    }
}
