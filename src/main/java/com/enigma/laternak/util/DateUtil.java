package com.enigma.laternak.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
    public static Date parseDate(String date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
        Date tempDate;
        try {
            tempDate = sdf.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return tempDate;
    }
}
