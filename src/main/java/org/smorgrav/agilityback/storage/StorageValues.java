package org.smorgrav.agilityback.storage;

import com.google.cloud.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A wrapper around the firestore value map to and make the firestore
 * connection not so damn high!.
 * <p>
 * * It is an abstraction of how data is passed to and from firestore.
 * * We can put in a lot of conveniences here.
 * * Makes it easier to test data transfer eg serialization
 * <p>
 * Id is required - it is the document id of these values. Not the complete path (e.g not the collection);
 */
public class StorageValues {

    private final Map<String, Object> values;
    private final String id;

    public StorageValues(Map<String, Object> values) {
        this.values = values;
        if (!values.containsKey("id")) {
            throw new IllegalArgumentException("FieldVaules needs an id");
        }
        id = (String) values.get("id");
    }

    public StorageValues(String id, Map<String, Object> values) {
        this.values = values;
        this.id = id;
    }

    public StorageValues(String id) {
        values = new HashMap<>();
        this.id = id;
    }

    public StorageValues(QueryDocumentSnapshot snapshot) {
        values = snapshot.getData();
        id = snapshot.getId();
    }

    public boolean isValid() {
        return !values.isEmpty();
    }

    public Long getLong(String key) {
        return (Long) values.get(key);
    }

    public Integer getInt(String key) {
        return (Integer) values.get(key);
    }

    public Double getDouble(String key) {
        return (Double) values.get(key);
    }

    public Boolean getBool(String key) {
        return (Boolean) values.get(key);
    }

    public Boolean getBool(String key, Boolean defaultValue) {
        Boolean result = (Boolean) values.get(key);
        return result != null ? result : defaultValue;
    }

    public String getString(String key) {
        return (String) values.get(key);
    }

    public String getString(String key, String defaultValue) {
        String result = (String) values.get(key);
        return result != null ? result : defaultValue;
    }

    public String id() {
        return id;
    }

    public ArrayList<String> getStrList(String key) {
        return (ArrayList<String>) values.get(key);
    }

    public Map<String, Object> values() {
        return values;
    }

    public StorageValues set(String key, String value) {
        values.put(key, value);
        return this;
    }

    public StorageValues set(String key, Boolean value) {
        values.put(key, value);
        return this;
    }
}