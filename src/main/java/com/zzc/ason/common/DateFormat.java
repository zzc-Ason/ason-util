package com.zzc.ason.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;

import java.util.Calendar;
import java.util.Date;

/**
 * author : Ason
 * createTime : 2017 年 07 月 25 日
 * className : DateFormat
 */
@Slf4j
public final class DateFormat {

    public static final String DATE_FORMAT_1 = "yyyyMMdd";                  // 时间格式1
    public static final String DATE_FORMAT_2 = "yyyy-MM-dd HH:mm:ss";      // 时间格式2
    public static final String DATE_FORMAT_3 = "yyyy-MM-dd/HH:mm:ss";      // 时间格式3
    public static final String DATE_FORMAT_4 = "yyyy-MM-dd HH:mm:ss.S";    // 时间格式4
    public static final String DATE_FORMAT_5 = "yyyy/MM/dd HH:mm:ss";      // 时间格式5
    public static final String DATE_FORMAT_6 = "yyyy-MM-dd";                // 时间格式6
    public static final String DATE_FORMAT_7 = "yyyy/MM/dd";                // 时间格式7
    public static final String DATE_FORMAT_8 = "yyyMM";                      // 时间格式8
    public static final String DATE_FORMAT_9 = "yyyy/MM/dd HH:mm:ss";                      // 时间格式9
    public static final String DATE_FORMAT_10 = "yyyy-MM-dd'T'HH:mm:ss.SSS";                      // 时间格式9
    public static final String DATE_FORMAT_11 = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";                      // 时间格式9

    public static final String YEAR = "yyyy";         // 年份
    public static final String MONTH = "MM";          // 月份
    public static final String DATE = "dd";           // 日期

    public static final String[] DATE_FORMAT_PATTERN = new String[]{DATE_FORMAT_1, DATE_FORMAT_2, DATE_FORMAT_3, DATE_FORMAT_4, DATE_FORMAT_5, DATE_FORMAT_6, DATE_FORMAT_7, DATE_FORMAT_8, DATE_FORMAT_9, DATE_FORMAT_10, DATE_FORMAT_11};    // 时间解析格式

    public static int daysBetween(Date startTime, Date endTime) {
        String startDateTime = DateFormatUtils.format(startTime, DATE_FORMAT_6);
        String endDateTime = DateFormatUtils.format(endTime, DATE_FORMAT_6);
        return daysBetween(startDateTime, endDateTime);
    }

    public static int daysBetween(String startTime, String endTime) {
        String defaultDateFromat = DATE_FORMAT_6;
        return daysBetween(startTime, endTime, defaultDateFromat);
    }

    public static int daysBetween(String startTime, String endTime, String pattern) {
        DateTime startDateTime = DateTimeFormat.forPattern(pattern).parseDateTime(startTime);
        DateTime endDateTime = DateTimeFormat.forPattern(pattern).parseDateTime(endTime);
        return Days.daysBetween(startDateTime, endDateTime).getDays();
    }

    public static int yearsBetween(Date startTime, Date endTime) {
        String startDateTime = DateFormatUtils.format(startTime, DATE_FORMAT_6);
        String endDateTime = DateFormatUtils.format(endTime, DATE_FORMAT_6);
        return yearsBetween(startDateTime, endDateTime);
    }

    public static int yearsBetween(String startTime, String endTime) {
        DateTime startDateTime = DateTimeFormat.forPattern(DATE_FORMAT_6).parseDateTime(startTime);
        DateTime endDateTime = DateTimeFormat.forPattern(DATE_FORMAT_6).parseDateTime(endTime);
        return Years.yearsBetween(startDateTime, endDateTime).getYears();
    }

    public static int minutesBetween(Date startTime, Date endTime) {
        String startDateTime = DateFormatUtils.format(startTime, DATE_FORMAT_2);
        String endDateTime = DateFormatUtils.format(endTime, DATE_FORMAT_2);
        return minutesBetween(startDateTime, endDateTime);
    }

    public static int minutesBetween(String startTime, String endTime) {
        DateTime startDateTime = DateTimeFormat.forPattern(DATE_FORMAT_2).parseDateTime(startTime);
        DateTime endDateTime = DateTimeFormat.forPattern(DATE_FORMAT_2).parseDateTime(endTime);
        return Minutes.minutesBetween(startDateTime, endDateTime).getMinutes();
    }

    public static int secondsBetween(Date startTime, Date endTime) {
        String startDateTime = DateFormatUtils.format(startTime, DATE_FORMAT_2);
        String endDateTime = DateFormatUtils.format(endTime, DATE_FORMAT_2);
        return secondsBetween(startDateTime, endDateTime);
    }

    public static int secondsBetween(String startTime, String endTime) {
        DateTime startDateTime = DateTimeFormat.forPattern(DATE_FORMAT_2).parseDateTime(startTime);
        DateTime endDateTime = DateTimeFormat.forPattern(DATE_FORMAT_2).parseDateTime(endTime);
        return Seconds.secondsBetween(startDateTime, endDateTime).getSeconds();
    }

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
            log.error("\"" + startTime + "\" must before \"" + endTime + "\"");
            return -1;
        }
        for (int i = 1; ; i++) {
            Date nextDate = DateUtils.addYears(startTime, i);
            if (DateUtils.truncatedCompareTo(nextDate, endTime, Calendar.DATE) > 0) {
                return i - 1;
            }
        }
    }

    public static int beforePresentYears(Date compareTime, Integer days) {
        Date time = DateUtils.addDays(compareTime, days);
        int rs = DateUtils.truncatedCompareTo(compareTime, time, Calendar.DATE);
        return rs >= 0 ? differentYears(time, compareTime) : differentYears(compareTime, time);
    }
}
