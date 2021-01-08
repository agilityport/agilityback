package org.smorgrav.agilityback.sources.sagik;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.smorgrav.agilityback.model.Organiser;

/**
 * TODO PM and Info Pdfs as seperate fields? Or even host them internally?
 */
public class OrganizerParser {

    static Organiser parseOrganizer(Document document) {
        document.outputSettings(new Document.OutputSettings().prettyPrint(false));
        Elements labels = document.select("label");
        Organiser organiser = Organiser.EMPTY;
        for (Element label : labels) {
            String[] parts = label.parent().text().split(":");
            if (parts.length == 2 && !parts[1].trim().isBlank()) {
                String key = parts[0].trim();
                String value = parts[1].trim();
                switch (key) {
                    case "Tävlingsledare":
                        organiser = organiser.withCompetitionLeader(value);
                        break;
                    case "Arrangör":
                        organiser = organiser.withOrganizerName(value);
                        break;
                    case "Plats":
                        organiser = organiser.withAddressLine1(value);
                        break;
                    case "Kontaktperson":
                        organiser = organiser.withContactPerson(value);
                        break;
                    case "Kontakt e-post":
                        organiser = organiser.withContactEmail(value);
                        break;
                    case "Kontakt telefon":
                        organiser = organiser.withContactPhone(value);
                        break;
                }
            }
        }

        return organiser;
    }
}
