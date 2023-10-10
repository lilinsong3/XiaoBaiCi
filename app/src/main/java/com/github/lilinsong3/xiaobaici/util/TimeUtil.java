package com.github.lilinsong3.xiaobaici.util;

import android.os.Build;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public abstract class TimeUtil {
    public static final String EPOCH = "1970-01-01 00:00:00";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATETIME_FORMAT_START = "yyyy-MM-dd 00:00:00";
    public static final String DATETIME_FORMAT_END = "yyyy-MM-dd 23:59:59";

    public static String nowDatetime() {
        return nowDatetime(DATETIME_FORMAT);
    }

    public static Long nowMillis() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        }
        return System.currentTimeMillis();
    }

    public static Boolean nowDateEqualsDate(String datetime) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return nowDatetime(DATETIME_FORMAT_START)
                    .equals(LocalDateTime.parse(datetime).format(DateTimeFormatter.ofPattern(DATETIME_FORMAT_START)));
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATETIME_FORMAT_START, Locale.CHINA);
        try {
            Date parsedDatetime = dateFormat.parse(datetime);
            if (parsedDatetime == null) {
                return false;
            }
            return nowDatetime(DATETIME_FORMAT_START)
                    .equals(dateFormat.format(parsedDatetime));
        } catch (ParseException e) {
            return false;
        }
    }

    public static String nowDatetime(String format) {
        return datetimeFromNow(format, 0);
    }

    public static String datetimeFromNow(String format, int dayAmount) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return LocalDateTime.now().plusDays(dayAmount).format(DateTimeFormatter.ofPattern(format));
        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, dayAmount);
        return new SimpleDateFormat(format, Locale.CHINA).format(calendar.getTime());
    }

    public static String yesterdayStart() {
        return datetimeFromNow(DATETIME_FORMAT_START, -1);
    }

    public static String yesterdayEnd() {
        return datetimeFromNow(DATETIME_FORMAT_END, -1);
    }

    public static String utcMillisecondsToDate(Long utcMilliseconds) {
        return utcMillisecondsToDatetime(utcMilliseconds, DATETIME_FORMAT_START);
    }

    public static String utcMillisecondsToDatetime(Long utcMilliseconds) {
        return utcMillisecondsToDatetime(utcMilliseconds, DATETIME_FORMAT);
    }

    public static String utcMillisecondsToDatetime(Long utcMilliseconds, String format) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Instant.ofEpochMilli(utcMilliseconds)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
                    .format(DateTimeFormatter.ofPattern(format));
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(utcMilliseconds);
        return new SimpleDateFormat(format, Locale.CHINA).format(calendar.getTime());
    }
}
