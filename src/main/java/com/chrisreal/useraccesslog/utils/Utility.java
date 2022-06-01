package com.chrisreal.useraccesslog.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Utility {
    public static LocalDateTime getEndDateTime(String startDate, String duration) {
        return LocalDateTime.parse(startDate, DateTimeFormatter.ofPattern(Constants.DATE_PATTERN)).plusHours(getDurationInHours(duration));
    }
    public static int getDurationInHours(String duration) {
        return (duration == "hourly") ? 1 : 24;
    }

    public static String formatStartDateTime(String startDate) {
        return startDate.replace(".", " ");
    }

}
