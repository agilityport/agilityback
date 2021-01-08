package org.smorgrav.agilityback.storage;

import org.smorgrav.agilityback.model.Competition;
import org.smorgrav.agilityback.model.CompetitionStatus;
import org.smorgrav.agilityback.model.CompetitionType;
import org.smorgrav.agilityback.model.Organiser;
import org.smorgrav.agilityback.model.SourceName;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class CompetitionSerializer {

    private static LocalDate parseDate(Long epochDay) {
        if (epochDay == 0) {
            return null;
        }
        return LocalDate.ofEpochDay(epochDay);
    }

    static CompetitionType parseType(String type) {
        if (type == null) {
            return CompetitionType.Unknown;
        }
        switch (type.toLowerCase()) {
            case "unofficial":
                return CompetitionType.UnOfficial;
            case "official":
                return CompetitionType.Official;
            case "appliedfor":
                return CompetitionType.AppliedFor;
            default:
                return CompetitionType.Unknown;
        }
    }

    static CompetitionStatus parseStatus(String status) {
        if (status == null) {
            return CompetitionStatus.Unknown;
        }
        switch (status.toLowerCase()) {
            case "archived":
                return CompetitionStatus.Archived;
            case "finished":
                return CompetitionStatus.Finished;
            case "closedregistration":
                return CompetitionStatus.ClosedRegistration;
            case "openregistration":
                return CompetitionStatus.OpenRegistration;
            case "planned":
                return CompetitionStatus.Planned;
            case "cancelled":
                return CompetitionStatus.Cancelled;
            case "running":
                return CompetitionStatus.Running;
            default:
                return CompetitionStatus.Unknown;
        }
    }

    static SourceName parseSourceName(String source) {
        if (source == null) {
            return SourceName.unknown;
        }
        switch (source.toLowerCase()) {
            case "sagik":
                return SourceName.sagik;
            case "nkk":
                return SourceName.nkk;
            case "community":
                return SourceName.community;
            case "agilityport":
                return SourceName.agilityport;
            case "hundestevner":
                return SourceName.hundestevner;
            default:
                return SourceName.unknown;
        }
    }

    static Competition deserialize(StorageValues fields) {
        return Competition.EMPTY
                .withId(fields.id())
                .withName(fields.getString("name"))
                .withFromDate(parseDate(fields.getLong("fromDate")))
                .withToDate(parseDate(fields.getLong("toDate")))
                .withOrganiser(
                        Organiser.EMPTY
                                .withOrganizerName(fields.getString("organiser.name"))
                                .withAddressLine1(fields.getString("organiser.addressLine1"))
                                .withAddressLine2(fields.getString("organiser.addressLine2"))
                                .withContactEmail(fields.getString("organiser.email"))
                                .withContactPhone(fields.getString("organiser.phone"))
                                .withContactPerson(fields.getString("organiser.contact"))
                                .withCompetitionLeader(fields.getString("organiser.competitionLeader"))
                )
                .withSource(parseSourceName(fields.getString("source")))
                .withSourceIds(fields.getStrList("sourceIds"))
                .withRegistrationDeadline(parseDate(fields.getLong("registrationDeadline")))
                .withType(parseType(fields.getString("type")))
                .withStatus(parseStatus(fields.getString("status")))
                .withEventSummary(fields.getString("eventSummary"));

    }

    static Map<String, Object> serialize(Competition competition) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("id", competition.id());
        fields.put("name", competition.name());
        fields.put("fromDate", competition.fromDate().toEpochDay());
        fields.put("toDate", competition.toDate().toEpochDay());
        fields.put("registrationDeadline", competition.registrationDeadline().toEpochDay());
        fields.put("source", competition.source().name());
        fields.put("sourceIds", competition.sourceIds());
        fields.put("status", competition.status().name());
        fields.put("type", competition.type().name());
        fields.put("eventSummary", competition.eventSummary());
        fields.put("organiser.name", competition.organiser().organizerName());
        fields.put("organiser.addressLine1", competition.organiser().addressLine1());
        fields.put("organiser.addressLine2", competition.organiser().addressLine2());
        fields.put("organiser.email", competition.organiser().contactEmail());
        fields.put("organiser.phone", competition.organiser().contactPhone());
        fields.put("organiser.contact", competition.organiser().contactPerson());
        fields.put("organiser.competitionLeader", competition.organiser().competitionLeader());

        return fields;
    }
}
