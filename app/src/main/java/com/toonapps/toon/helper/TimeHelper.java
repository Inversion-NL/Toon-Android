package com.toonapps.toon.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeHelper {

    public static long getLastPeriod(){
        long time = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, -10);

        return calendar.getTimeInMillis();
    }

    public static Calendar getMidnight() {
        Calendar midnight = Calendar.getInstance();
        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 59);
        midnight.add(Calendar.HOUR_OF_DAY, -2);
        return midnight;
    }

    public static String getHumanReadableTime(Locale locale, long timeInSeconds) {
        Calendar dateTime = getNow();
        dateTime.setTimeInMillis(timeInSeconds * 1000);

        return SimpleDateFormat
                .getTimeInstance(
                        SimpleDateFormat.SHORT,
                        locale)
                .format(dateTime.getTime());
    }

    public static Calendar getNow() {
        return Calendar.getInstance();
    }
}