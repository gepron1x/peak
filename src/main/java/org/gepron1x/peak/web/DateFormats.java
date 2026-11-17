package org.gepron1x.peak.web;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

/**
 * Форматирование дат для страниц: ДД.ММ.ГГГГ ЧЧ:ММ.
 */
@Component("fmt")
public class DateFormats {

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter INPUT_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public String dateTime(LocalDateTime value) {
        return value == null ? "—" : DATE_TIME.format(value);
    }

    public String date(LocalDate value) {
        return value == null ? "" : DATE.format(value);
    }

    public String input(LocalDateTime value) {
        return value == null ? "" : INPUT_DATE_TIME.format(value);
    }
}
