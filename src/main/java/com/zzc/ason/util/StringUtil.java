package com.zzc.ason.util;

import com.zzc.ason.common.DateFormat;
import com.zzc.ason.common.Symbol;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author : Ason
 * createTime : 2017 年 08 月 14 日
 */
public final class StringUtil {
    private static final Logger LOGGER = Logger.getLogger(StringUtil.class);

    private static final String EMPTY = "";         // empty
    private static final String NULL = "null";      // null
    public static final String BLANK = " ";         // 空格

    public static void jointStr(StringBuilder sb, String s) {
        if (StringUtils.isBlank(sb.toString())) sb.append(s);
        else sb.append(Symbol.COMMA + s);
    }

    public static boolean compareEquals(Object... params) {
        if (ArrayUtils.isEmpty(params)) return true;
        if (params[0] == null) {
            for (Object o : params) {
                if (o != null) return false;
            }
        } else {
            String init = params[0].toString();
            for (Object o : params) {
                if (o == null) return false;
                if (!StringUtils.equals(init, o.toString())) return false;
            }
        }
        return true;
    }

    public static boolean containsNull(Object... params) {
        for (Object o : params) {
            if (o == null) return true;
        }
        return false;
    }

    public static String trim(Object obj) {
        String back_str = StringUtils.EMPTY;
        if (obj != null) {
            if (obj instanceof Date) {
                back_str = DateFormatUtils.format((Date) obj, DateFormat.DATE_FORMAT_3);
            } else {
                String[] ss = obj.toString().trim().split("");
                Pattern p = Pattern.compile("\t|\r|\n|\\s");
                for (String s : ss) {
                    Matcher m = p.matcher(s);
                    if (!m.find()) back_str += s;
                }
            }
        }
        return back_str;
    }

    public static String parseStr(Object source) {
        return source == null ? null : source.toString();
    }

    public static String parseFieldToStr(Object obj, String fieldName) {
        if (obj == null) return null;
        Object value = ReflectionUtil.acquireValueByFieldName(obj, fieldName);
        return trim(parseStr(value));
    }
}
