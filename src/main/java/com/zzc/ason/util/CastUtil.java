package com.zzc.ason.util;


import com.zzc.ason.common.Symbol;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;

/**
 * author : Ason
 * createTime : 2017 年 07 月 27 日
 * className : CastUtil
 * remark: 类型转换助手
 */
@Slf4j
public final class CastUtil {

    public static String rate(Integer c, Integer t) {
        Double h = 0.0;
        if (t.intValue() != 0) h = Double.valueOf(Math.round(c * 10000 / t.intValue()));
        return h / 100 + Symbol.PER;
    }

    public static Timestamp castTimestamp(Object obj) {
        return castTimestamp(obj, null);
    }

    public static Timestamp castTimestamp(Object obj, Timestamp defaultValue) {
        Timestamp timestampValue = defaultValue;
        if (obj != null) {
            String strValue = castString(obj);
            if (StringUtils.isNotEmpty(strValue)) {
                try {
                    timestampValue = Timestamp.valueOf(strValue);
                } catch (NumberFormatException e) {
                    timestampValue = defaultValue;
                }
            }
        }
        return timestampValue;
    }

    public static String castString(Object obj) {
        return CastUtil.castString(obj, "");
    }

    private static String castString(Object obj, String defaultValue) {
        return obj != null ? String.valueOf(obj) : defaultValue;
    }

    public static double castDouble(Object obj) {
        return CastUtil.castDouble(obj, 0);
    }

    private static double castDouble(Object obj, double defaultValue) {
        double doubleValue = defaultValue;
        if (obj != null) {
            String strValue = castString(obj);
            if (StringUtils.isNotEmpty(strValue)) {
                try {
                    doubleValue = Double.parseDouble(strValue);
                } catch (NumberFormatException e) {
                    doubleValue = defaultValue;
                }
            }
        }
        return doubleValue;
    }

    public static long castLong(Object obj) {
        return CastUtil.castLong(obj, 0);
    }

    private static long castLong(Object obj, long defaultValue) {
        long longValue = defaultValue;
        if (obj != null) {
            String strValue = castString(obj);
            if (StringUtils.isNotEmpty(strValue)) {
                try {
                    longValue = Long.parseLong(strValue);
                } catch (NumberFormatException e) {
                    longValue = defaultValue;
                }
            }
        }
        return longValue;
    }

    public static int castInt(Object obj) {
        return CastUtil.castInt(obj, 0);
    }

    private static int castInt(Object obj, int defaultValue) {
        int intValue = defaultValue;
        if (obj != null) {
            String strValue = castString(obj);
            if (StringUtils.isNotEmpty(strValue)) {
                try {
                    intValue = Integer.parseInt(strValue);
                } catch (NumberFormatException e) {
                    intValue = defaultValue;
                }
            }
        }
        return intValue;
    }

    public static boolean castBoolean(Object obj) {
        return CastUtil.castBoolean(obj, false);
    }

    private static boolean castBoolean(Object obj, boolean defaultValue) {
        boolean booleanValue = defaultValue;
        if (obj != null) {
            String strValue = castString(obj);
            if (StringUtils.isNotEmpty(strValue)) {
                try {
                    booleanValue = Boolean.parseBoolean(strValue);
                } catch (NumberFormatException e) {
                    booleanValue = defaultValue;
                }
            }
        }
        return booleanValue;
    }
}
