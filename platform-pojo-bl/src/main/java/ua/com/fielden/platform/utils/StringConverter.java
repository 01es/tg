package ua.com.fielden.platform.utils;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import ua.com.fielden.platform.types.Money;

/**
 * Provides a set of utilities for converting string values to other frequently used types such as {@link Date}, {@link DateTime} etc.
 *
 * @author TG Team
 *
 */
public final class StringConverter {
    private StringConverter() {
    }

    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    public static final Date toDate(final String dateTime) {
        return formatter.parseDateTime(dateTime).toDate();
    }

    public static final DateTime toDateTime(final String dateTime) {
        return formatter.parseDateTime(dateTime);
    }

    public static final String toDayOfWeek(final DateTime dateTime) {
        return dateTime.dayOfWeek().getAsText();
    }

    public static final String toDayOfWeek(final Date dateTime) {
        return toDayOfWeek(new DateTime(dateTime));
    }

    /**
     * Converts integer number representing a day of week to a string representation.
     *
     * @param dayOfWeek -- one of the DateTimeConstants constants
     * @return a string representation of the day of week
     */
    public static final String toDayOfWeek(final int dayOfWeek) {
        return toDayOfWeek(new DateTime().dayOfWeek().setCopy(dayOfWeek));
    }

    public static final Money toMoney(final String amount) {
        return new Money(amount);
    }

    public static void main(final String[] args) {
        System.out.println(toDayOfWeek(DateTimeConstants.SATURDAY));
    }
}
