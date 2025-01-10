package com.infogain.utils;

import java.text.SimpleDateFormat;

public final class DateUtil {

    private DateUtil() {
    }

    public static String formatDate(java.util.Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public static String formatDateTime(java.util.Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }
}
