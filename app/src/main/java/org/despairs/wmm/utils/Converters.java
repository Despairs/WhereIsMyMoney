package org.despairs.wmm.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import androidx.databinding.BindingConversion;

/**
 * Created by EKovtunenko on 27.11.2019.
 */
public class Converters {

    private static final Locale RU = Locale.forLanguageTag("ru");
    private static final DateTimeFormatter BILLING_PERIOD_FORMATTER = DateTimeFormatter.ofPattern("LLLL YYYY", RU);

    @BindingConversion
    public static String convertBillingPeriodToString(LocalDateTime period) {
        String formatterPeriod = BILLING_PERIOD_FORMATTER.format(period);
        return formatterPeriod.substring(0, 1).toUpperCase() + formatterPeriod.substring(1);
    }

    public static String convertToString(LocalDateTime period) {
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(period);
    }

    @BindingConversion
    public static String convertAmountToString(double amount) {
        return String.format(RU, "%1$,.2f \u20BD", amount);
    }
}
