package org.smorgrav.agilityback.sources.sagik;

import org.smorgrav.agilityback.model.Size;
import org.smorgrav.agilityback.model.TrialType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ParseUtils {

    private static final DateTimeFormatter DATEFORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    static LocalDate[] dates(String datesStr, LocalDate fromIncluding, LocalDate toIncluding) {
        LocalDate[] dates = new LocalDate[2];
        String[] datesArr = datesStr.split(" - ");

        String fromStr = datesArr[0].trim();
        if (!fromStr.isBlank()) {
            dates[0] = LocalDate.parse(datesArr[0].trim(), DATEFORMAT);
            if (datesArr.length == 2) {
                dates[1] = LocalDate.parse(datesArr[1].trim(), DATEFORMAT);
            } else {
                dates[1] = dates[0];
            }

            // Now check if dates are in rang
            if ((dates[0].isAfter(toIncluding)) || dates[0].isBefore(fromIncluding)) {
                dates[0] = null;
                dates[1] = null;
            }
        }

        return dates;
    }

    static LocalDate date(String dateStr) {
        return dates(dateStr, LocalDate.MIN, LocalDate.MAX)[0];
    }

    static Size size(String size) {
        switch (size) {
            case "Extra Small":
                return Size.xsmall;
            case "Small":
                return Size.small;
            case "Medium":
                return Size.medium;
            case "Large":
                return Size.large;
            case "Extra Large":
                return Size.xlarge;
            default:
                return Size.medium; //TODO throw here
        }
    }

    static TrialType trial(String trial) {
        String invariantTrial = trial.toLowerCase().trim();

        String[] parts = invariantTrial.split(" ");
        if (parts.length == 3) {
            boolean isAgility = parts[0].equals("agility");
            boolean isJump = parts[0].equals("hopp");
            String level = parts[1];

            if (isAgility && level.equals("1")) {
                return TrialType.A1;
            }
            if (isAgility && level.equals("2")) {
                return TrialType.A2;
            }
            if (isAgility && level.equals("3")) {
                return TrialType.A3;
            }
            if (isAgility && level.equals("lag")) {
                return TrialType.ATeam;
            }
            if (isAgility && level.equals("öppenklass")) {
                return TrialType.AOpen;
            }

            if (isJump && level.equals("1")) {
                return TrialType.J1;
            }
            if (isJump && level.equals("2")) {
                return TrialType.J2;
            }
            if (isJump && level.equals("3")) {
                return TrialType.J3;
            }
            if (isJump && level.equals("lag")) {
                return TrialType.JTeam;
            }
            if (isJump && level.equals("öppenklass")) {
                return TrialType.JOpen;
            }
        }

        // TODO notify failure to parse? Throw?
        return TrialType.Other;
    }
}
