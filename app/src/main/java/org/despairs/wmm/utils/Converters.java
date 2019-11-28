package org.despairs.wmm.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.util.Locale;

import androidx.databinding.BindingConversion;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;

/**
 * Created by EKovtunenko on 27.11.2019.
 */
public class Converters {

    private static final Locale RU = Locale.forLanguageTag("ru");
    private static final DateTimeFormatter BILLING_PERIOD_FORMATTER = DateTimeFormatter.ofPattern("LLLL YYYY", RU);
    private static final DateTimeFormatter DATE_FORMATTER = new DateTimeFormatterBuilder()
            .appendValue(DAY_OF_MONTH, 2)
            .appendLiteral('.')
            .appendValue(MONTH_OF_YEAR, 2)
            .appendLiteral('.')
            .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
            .toFormatter();

    @BindingConversion
    public static String convertBillingPeriodToString(LocalDateTime period) {
        String formatterPeriod = BILLING_PERIOD_FORMATTER.format(period);
        return formatterPeriod.substring(0, 1).toUpperCase() + formatterPeriod.substring(1);
    }

    public static String convertToStringDate(LocalDateTime period) {
        return DATE_FORMATTER.format(period);
    }

    @BindingConversion
    public static String convertAmountToString(double amount) {
        return String.format(RU, "%1$,.2f \u20BD", amount);
    }
}
