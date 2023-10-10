package com.github.lilinsong3.lib;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LocalDatetimeTestCase {
    public static void testLocalDatetime() {
        System.out.println("LocalDateTime today: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("date today: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date()));

        Calendar calendar = Calendar.getInstance();
        System.out.println("calendar today: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(calendar.getTime()));

        calendar.add(Calendar.DATE, 0);
        System.out.println("calendar yesterday: " + new SimpleDateFormat("yyyy-MM-dd 00:00:00", Locale.CHINA).format(calendar.getTime()));
    }

    public static void testCurrentMillis() {
        System.out.println("LocalDateTime current millis: " + LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
        System.out.println("System current millis: " + System.currentTimeMillis());
    }
}
