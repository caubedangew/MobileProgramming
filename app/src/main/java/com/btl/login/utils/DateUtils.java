package com.btl.login.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    public static long convertDateToTimestamp(String dateString) {
        try {
            Date date = sdf.parse(dateString);
            return date != null ? date.getTime() : 0;  // Trả về timestamp (Long)
        } catch (Exception e) {
            e.printStackTrace();
            return 0;  // Nếu lỗi, trả về 0 (hoặc bạn có thể sử dụng một giá trị mặc định khác)
        }
    }

    public static String convertTimestampToDate(Long timeStamp) {
        Date date = new Date(timeStamp);
        return sdf.format(date);
    }
}
