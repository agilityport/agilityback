package org.smorgrav.agilityback.storage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SerializationUtils {

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static String serialize(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return formatter.format(dateTime);
    }

    static LocalDateTime deserialize(String dateTime) {
        if (dateTime == null || dateTime.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(dateTime, formatter);
    }
}
