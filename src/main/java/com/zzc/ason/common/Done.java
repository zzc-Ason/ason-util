package com.zzc.ason.common;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author : Ason
 * createTime : 2017 年 07 月 28 日
 */
public class Done {
    private static final Logger LOGGER = Logger.getLogger(Done.class);

    public static String acquireValueByFieldName(Object obj, String fieldName) throws Exception {
        if (obj == null) return null;
        if (StringUtils.isBlank(fieldName)) return null;

        PropertyDescriptor pd = new PropertyDescriptor(fieldName, obj.getClass());
        Method getMethod = pd.getReadMethod();
        Object value = getMethod.invoke(obj);

        return value == null ? null : value.toString();
    }

    public static String handlerTimeArgs(String arg) {
        if (StringUtils.isNotBlank(arg)) {
            try {
                DateUtils.parseDate(arg, DateFormat.DATE_FORMAT_1);
                return arg;
            } catch (ParseException e) {
                LOGGER.error("startTime or endTime is invalid.");
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

    public static String parseAttr(Pattern PATTERN, String line, int index) {
        Matcher matcher = PATTERN.matcher(line);
        if (matcher.find()) {
            return matcher.group(index);
        }
        return null;
    }

}
