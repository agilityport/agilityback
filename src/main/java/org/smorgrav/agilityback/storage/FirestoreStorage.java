package org.smorgrav.agilityback.storage;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.smorgrav.agilityback.model.Competition;
import org.smorgrav.agilityback.model.Equipage;
import org.smorgrav.agilityback.model.Event;
import org.smorgrav.agilityback.model.SourceName;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Model storage is storage for model entities.
 */
public class FirestoreStorage implements Storage {

    private static final Logger LOG = Logger.getLogger(FirestoreStorage.class.getName());

    private final Firestore firestore;

    public FirestoreStorage() throws IOException {
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .setProjectId("agilityport")
                .build();
        FirebaseApp.initializeApp(options);
        firestore = FirestoreClient.getFirestore();
    }

    public StorageValues readConfig(String configId) {
        try {
            return new StorageValues(configId, firestore.collection("config").document(configId).get().get().getData());
        } catch (InterruptedException | ExecutionException e) {
            LOG.warning("Failed to read config " + configId);
            LOG.warning(e.getMessage());
            return new StorageValues(configId);
        }
    }

    public void writeConfig(StorageValues values) {
        try {
            firestore.collection("config").document(values.id()).set(values.values()).get();
        } catch (InterruptedException | ExecutionException e) {
            LOG.warning("Failed to write config " + values.id());
            LOG.warning(e.getMessage());
        }
    }

    /**
     * @param competition The competition to persist in storage
     * @param deepWrite   True if events and equipages should be stored too
     */
    @Override
    public void write(Competition competition, boolean deepWrite) {
        if (competition.id().isBlank()) {
            throw new IllegalArgumentException("Computation Id must be set before writing");
        }

        Map<String, Object> fields = CompetitionSerializer.serialize(competition);
        try {
            firestore.collection("competitions").document(competition.id()).set(fields).get();

            if (deepWrite) {
                competition.events().forEach(event -> {
                    write(event);
                    event.equipages().forEach(this::write);
                });
            }
        } catch (InterruptedException | ExecutionException e) {
            LOG.warning("Failed to write events for competition " + competition.id());
            LOG.warning(e.getMessage());
        }
    }

    @Override
    public void write(Event event) {
        if (event.id().isBlank() || event.competitionId().isBlank()) {
            throw new IllegalArgumentException("Computation and event id must be set was " +
                    event.id() + " " + event.competitionId());
        }

        Map<String, Object> fields = EventSerializer.serialize(event);
        try {
            firestore.collection("competitions").document(event.competitionId()).collection("events").document(event.id()).set(fields).get();
        } catch (InterruptedException | ExecutionException e) {
            LOG.warning("Failed to write event " + event.id());
            LOG.warning(e.getMessage());
        }
    }

    @Override
    public void write(Equipage equipage) {
        if (equipage.id().isBlank() || equipage.competitionId().isBlank() || equipage.eventId().isBlank()) {
            throw new IllegalArgumentException("Competition, event and equipage Id must be set but was " +
                    equipage.id() + " " + equipage.eventId() + " " + equipage.competitionId());
        }
        Map<String, Object> fields = EquipageSerializer.serialize(equipage);
        try {
            firestore.collection("competitions").document(equipage.competitionId())
                    .collection("events").document(equipage.eventId())
                    .collection("equipages").document(equipage.id())
                    .set(fields).get();
        } catch (InterruptedException | ExecutionException e) {
            LOG.warning("Failed to write equipages for equipage " + equipage.id());
            LOG.warning(e.getMessage());
        }
    }

    @Override
    public void readEvents(Competition competition) {
        try {
            List<Event> events = firestore.collection("competitions")
                    .document(competition.id())
                    .collection("events")
                    .whereNotEqualTo("id", "not_an_id").get().get().getDocuments()
                    .stream()
                    .map(StorageValues::new)
                    .map(EventSerializer::deserialize)
                    .collect(Collectors.toList());
            competition.events().addAll(events);
        } catch (ExecutionException | InterruptedException e) {
            LOG.warning("Failed to read events for competition " + competition.id());
            LOG.warning(e.getMessage());
        }
    }

    @Override
    public void readEquipages(Event event) {
        try {
            List<Equipage> equipages = firestore.collection("competitions")
                    .document(event.competitionId())
                    .collection("events")
                    .document(event.id())
                    .collection("equipages")
                    .whereNotEqualTo("id", "not_an_id").get().get().getDocuments()
                    .stream()
                    .map(StorageValues::new)
                    .map(EquipageSerializer::deserialize)
                    .collect(Collectors.toList());
            event.equipages().addAll(equipages);
        } catch (ExecutionException | InterruptedException e) {
            LOG.warning("Failed to read equipages for event " + event.id());
            LOG.warning(e.getMessage());
        }
    }

    @Override
    public List<Competition> read(SourceName sourceName, LocalDate from, LocalDate to) {
        try {
            return firestore.collection("competitions")
                    .whereEqualTo("source", sourceName.name())
                    .whereGreaterThanOrEqualTo("fromDate", from.toEpochDay())
                    .whereLessThanOrEqualTo("fromDate", to.toEpochDay())
                    .get().get().getDocuments()
                    .stream().map(StorageValues::new)
                    .map(CompetitionSerializer::deserialize)
                    .collect(Collectors.toList());
        } catch (ExecutionException | InterruptedException e) {
            LOG.warning("Failed to read competitions with source "
                    + sourceName.name()
                    + " between "
                    + from.format(DateTimeFormatter.ISO_DATE)
                    + " and "
                    + to.format(DateTimeFormatter.ISO_DATE));
            LOG.warning(e.getMessage());
        }

        return new ArrayList<>();
    }
}
