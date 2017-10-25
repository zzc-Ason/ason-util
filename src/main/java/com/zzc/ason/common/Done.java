package com.zzc.ason.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;

/**
 * author : Ason
 * createTime : 2017 年 07 月 28 日
 */
@Slf4j
public class Done {

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
