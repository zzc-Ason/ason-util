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

    public static Map<String, String> executeDateByStages(String startDate, String endDate, int days) throws ParseException {
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
            log.warn("date {} must before {}", startDate, endDate);
        }
        log.info("[divide \"{}\" - \"{}\" into {} part]", startDate, endDate, dateMap.size());
        return dateMap;
    }

    private static Map<Integer, Integer> executeCountByStages(Integer count, int size) {
        Map<Integer, Integer> countMap = Maps.newTreeMap();
        if (count <= size) {
            countMap.put(1, count);
        } else {
            for (int i = 1; i <= count; i += size) {
                Integer value = i + size - 1;
                if (i == count) value = count;
                countMap.put(i, value);
            }
        }
        log.info("[divide \"{}\" by \"{}\" into {} part]", count, size, countMap.size());
        return countMap;
    }

    public static String handlerTimeArgs(String arg, String pattern) {
        if (StringUtils.isNotBlank(arg)) {
            try {
                DateUtils.parseDate(arg, pattern);
                return arg;
            } catch (ParseException e) {
                log.error("startTime or endTime is invalid.");
                throw new RuntimeException("args \"" + arg + "\" is invalid", e);
            }
        }
        return null;
    }

    public static String handlerTimeArgs(String arg) {
        if (StringUtils.isNotBlank(arg)) {
            try {
                DateUtils.parseDate(arg, DateFormat.DATE_FORMAT_1);
                return arg;
            } catch (ParseException e) {
                log.error("startTime or endTime is invalid.");
                throw new RuntimeException("args \"" + arg + "\" is invalid", e);
            }
        }
        return null;
    }

    public static String handlerNullArgs(String[] args, int index) {
        String arg;
        try {
            arg = args[index];
        } catch (Exception e) {
            arg = null;
        }
        return arg;
    }
}
