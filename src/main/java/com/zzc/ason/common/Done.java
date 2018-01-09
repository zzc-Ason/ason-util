package com.zzc.ason.common;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * author : Ason
 * createTime : 2017 年 07 月 28 日
 */
@Slf4j
public class Done {

    public static Map<String, String> divideDate(String startDate, String endDate, int days) throws ParseException {
        Date start = DateUtils.parseDate(startDate, DateFormat.DATE_FORMAT_PATTERN);
        Date end = DateUtils.parseDate(endDate, DateFormat.DATE_FORMAT_PATTERN);
        Map<String, String> dateMap = Maps.newTreeMap();
        if (DateUtils.truncatedCompareTo(start, end, Calendar.DATE) <= 0) {
            while (DateUtils.truncatedCompareTo(start, end, Calendar.DATE) <= 0) {
                String _start = DateFormatUtils.format(start, DateFormat.DATE_FORMAT_6);
                start = DateUtils.addDays(start, days - 1);
                if (DateUtils.truncatedCompareTo(start, end, Calendar.DATE) <= 0) {
                    dateMap.put(_start, DateFormatUtils.format(start, DateFormat.DATE_FORMAT_6));
                }
                if (DateUtils.truncatedCompareTo(start, end, Calendar.DATE) > 0) {
                    dateMap.put(_start, DateFormatUtils.format(end, DateFormat.DATE_FORMAT_6));
                }
                start = DateUtils.addDays(start, 1);
            }
        } else {
            log.warn("[date {} must before {}]", startDate, endDate);
        }
        log.debug("[divide \"{}\" - \"{}\" into {} part]", startDate, endDate, dateMap.size());
        return dateMap;
    }

    public static Map<Integer, Integer> divideCount(Integer count, int size) {
        Map<Integer, Integer> countMap = Maps.newTreeMap();
        if (count <= size) {
            countMap.put(1, count);
        } else {
            for (int i = 1; i <= count; i += size) {
                Integer value = i + size - 1;
                if (value >= count) value = count;
                countMap.put(i, value);
            }
        }
        log.debug("[divide \"{}\" by \"{}\" into {} part]", count, size, countMap.size());
        return countMap;
    }

    public static String handlerTimeArgs(String arg, String pattern) {
        if (StringUtils.isNotBlank(arg)) {
            try {
                DateUtils.parseDate(arg, pattern);
                return arg;
            } catch (ParseException e) {
                log.error("[handler time args] [args \"{}\" is invalid]", arg);
                throw new RuntimeException("[args \"" + arg + "\" is invalid]", e);
            }
        }
        return arg;
    }

    public static String handlerTimeArgs(String arg) {
        String defaultPattern = DateFormat.DATE_FORMAT_6;
        return handlerTimeArgs(arg, defaultPattern);
    }

    public static String handlerNullArgs(String[] args, int index) {
        try {
            return args[index];
        } catch (Exception e) {
            return null;
        }
    }

    public static Integer handlerIntegerArgs(String[] args, int index) {
        try {
            return Integer.valueOf(args[index]);
        } catch (Exception e) {
            return null;
        }
    }

    public static Boolean handlerBooleanArgs(String[] args, int index) {
        try {
            return Boolean.valueOf(args[index]);
        } catch (Exception e) {
            return null;
        }
    }
}
