package com.gmail.tomasatrat.ui.utils.converters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateConverter {
    static String pattern = " dd MMM yyyy";
    static Locale locale = new Locale("es", "UY");

    public static String convert(Date date) {
        String dateStr = "";
        if (date != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, locale);
            dateStr = simpleDateFormat.format(date);
        }
        return dateStr;
    }
}
