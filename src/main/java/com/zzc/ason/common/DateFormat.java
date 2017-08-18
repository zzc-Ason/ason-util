package com.zzc.ason.common;

import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * author : Ason
 * createTime : 2017 年 07 月 25 日
 * className : DateFormat
 */
public final class DateFormat {

    public static final String DATE_FORMAT_1 = "yyyyMMdd";                 // 时间格式1
    public static final String DATE_FORMAT_2 = "yyyy-MM-dd HH:mm:ss";     // 时间格式2
    public static final String DATE_FORMAT_3 = "yyyy-MM-dd/HH:mm:ss";     // 时间格式3
    public static final String DATE_FORMAT_4 = "yyyy-MM-dd HH:mm:ss.S";   // 时间格式4
    public static final String DATE_FORMAT_5 = "yyyy/MM/dd HH:mm:ss";     // 时间格式5
    public static final String DATE_FORMAT_6 = "yyyy-MM-dd";     // 时间格式6
    public static final String DATE_FORMAT_7 = "yyyy/MM/dd";     // 时间格式7

    public static final String[] DATE_FORMAT_PATTERN = new String[]{DATE_FORMAT_1, DATE_FORMAT_2, DATE_FORMAT_3, DATE_FORMAT_4, DATE_FORMAT_5, DATE_FORMAT_6, DATE_FORMAT_7};    // 时间解析格式

    public static int differentDays(Calendar startTimeCalendar, Calendar endTimeCalendar) {
        int day1 = startTimeCalendar.get(Calendar.DAY_OF_YEAR);
        int day2 = endTimeCalendar.get(Calendar.DAY_OF_YEAR);

        int year1 = startTimeCalendar.get(Calendar.YEAR);
        int year2 = endTimeCalendar.get(Calendar.YEAR);

        if (year1 != year2) {       // 不同年
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) {       // 闰年
                    timeDistance += 366;
                } else {       // 不是闰年
                    timeDistance += 365;
                }
            }
            return timeDistance + (day2 - day1);
        } else {            // 同一年
            return day2 - day1;
        }
    }

    public static int differentYears(Date startTime, Date endTime) {
        if (DateUtils.truncatedCompareTo(startTime, endTime, Calendar.DATE) > 0) {
            throw new RuntimeException("\"" + startTime + "\" must before \"" + endTime + "\"");
        }
        for (int i = 1; ; i++) {
            Date nextDate = DateUtils.addYears(startTime, i);
            if (DateUtils.truncatedCompareTo(nextDate, endTime, Calendar.DATE) > 0) {
                return i - 1;
            }
        }
    }

}
