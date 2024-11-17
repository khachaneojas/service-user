package com.sprk.service.user.util;

import com.sprk.commons.exception.InvalidDataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;





@Component
public class InstantUtils {

    private final TextHelper textHelper;
    @Autowired
    public InstantUtils(TextHelper textHelper) {
        this.textHelper = textHelper;
    }


    public final DateTimeFormatter defaultDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public final DateTimeFormatter defaultTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
    public final DateTimeFormatter defaultTimeWithoutSSFormat = DateTimeFormatter.ofPattern("HH:mm");
    public final DateTimeFormatter defaultDateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public final DateTimeFormatter defaultDateTimeWithoutSSFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");





    public Instant parseInstant(String dateFormat) {
        return parseInstant(dateFormat, null, false);
    }
    public Instant parseInstant(String dateFormat, boolean isDateTimePattern) {
        return parseInstant(dateFormat, null, isDateTimePattern);
    }
    public Instant parseInstant(String dateFormat, String pattern) {
        return parseInstant(dateFormat, pattern, false);
    }
    private Instant parseInstant(String dateFormat, String pattern, boolean isDateTimePattern) {
        ZonedDateTime zonedDateTime = null;
        try {
            zonedDateTime = parseZonedDateTime(dateFormat, pattern, isDateTimePattern);
        } catch (ParseException exception) {
            throw new InvalidDataException("Oops! Looks like the provided string (" + dateFormat + ") is invalid and can't be parsed. Mind checking and trying again?");
        }
        if (null == zonedDateTime)
            return null;

        return zonedDateTime.toInstant();
    }



    public String toString(Instant instant) {
        return toString(instant, null, false);
    }
    public String toString(Instant instant, boolean isDateTimePattern) {
        return toString(instant, null, isDateTimePattern);
    }
    public String toString(Instant instant, String pattern) {
        return toString(instant, pattern, false);
    }
    private String toString(Instant instant, String pattern, boolean isDateTimePattern) {
        if (null == instant)
            return null;

        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("UTC"));
        return formatZonedDateTime(zonedDateTime, pattern, isDateTimePattern);
    }


    public LocalDate toLocalDate(Instant instant) {
        return instant.atZone(ZoneId.of("UTC")).toLocalDate();
    }

    public boolean isPresentOrPastDate(Instant currentInstant, Instant targetInstant) {
        LocalDate currentDate = toLocalDate(currentInstant);
        LocalDate targetDate = toLocalDate(targetInstant);
        return currentDate.isBefore(targetDate) || currentDate.isEqual(targetDate);
    }

    public boolean isPresentOrFutureDate(Instant currentInstant, Instant targetInstant) {
        LocalDate currentDate = toLocalDate(currentInstant);
        LocalDate targetDate = toLocalDate(targetInstant);
        return currentDate.isAfter(targetDate) || currentDate.isEqual(targetDate);
    }

    public boolean isFutureDate(Instant currentInstant, Instant targetInstant) {
        LocalDate currentDate = toLocalDate(currentInstant);
        LocalDate targetDate = toLocalDate(targetInstant);
        return currentDate.isAfter(targetDate);
    }

    public boolean isPresentDate(Instant currentInstant, Instant targetInstant) {
        LocalDate currentDate = toLocalDate(currentInstant);
        LocalDate targetDate = toLocalDate(targetInstant);
        return currentDate.isEqual(targetDate);
    }

    public boolean isPastDate(Instant currentInstant, Instant targetInstant) {
        LocalDate currentDate = toLocalDate(currentInstant);
        LocalDate targetDate = toLocalDate(targetInstant);
        return currentDate.isBefore(targetDate);
    }

    public boolean isAgeAtLeast(Instant birthDate, int minimumAge) {
        LocalDate birthLocalDate = toLocalDate(birthDate);
        LocalDate currentLocalDate = toLocalDate(Instant.now());
        Period age = Period.between(birthLocalDate, currentLocalDate);
        return age.getYears() >= minimumAge;
    }






    private ZonedDateTime parseZonedDateTime(String dateFormat, String pattern, boolean isDateTimePattern) throws ParseException {
        if (textHelper.isBlank(dateFormat))
            return null;

        DateTimeFormatter finalFormat;
        if (!textHelper.isBlank(pattern))
            finalFormat = DateTimeFormatter.ofPattern(pattern);
        else
            finalFormat = isDateTimePattern ? defaultDateTimeFormat : defaultDateFormat;

        try {
            return LocalDate
                    .parse(dateFormat, finalFormat)
                    .atStartOfDay(ZoneId.of("UTC"));
        } catch (DateTimeParseException e) {
            throw new ParseException("Error parsing date/time: " + e.getMessage(), 0);
        }
    }


    private String formatZonedDateTime(ZonedDateTime zonedDateTime, String pattern, boolean isDateTimePattern) {
        if (null == zonedDateTime)
            return null;

        DateTimeFormatter finalFormat;
        if (!textHelper.isBlank(pattern))
            finalFormat = DateTimeFormatter.ofPattern(pattern);
        else
            finalFormat = (
                isDateTimePattern
                        ? defaultDateTimeFormat
                        : defaultDateFormat
            );

        return zonedDateTime.format(finalFormat);
    }
}



