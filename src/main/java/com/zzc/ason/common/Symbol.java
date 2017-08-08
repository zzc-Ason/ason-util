package com.zzc.ason.common;

import org.apache.commons.lang3.StringUtils;

/**
 * author : Ason
 * createTime : 2017 年 08 月 07 日
 */
public class Symbol {

    private static final String MASK = "*";

    public static String mask(Integer count) {
        StringBuilder sb = new StringBuilder(StringUtils.EMPTY);
        for (int i = 0; i < count; i++) {
            sb.append(MASK);
        }
        return sb.toString();
    }

    public static String maskPid(String pid) {
        Integer length = pid.length();
        if (length == 18) return pid.substring(0, 6) + mask(8) + pid.substring(14);
        if (length == 15) return pid.substring(0, 6) + mask(6) + pid.substring(12);
        return null;
    }

    public static String maskMobile(String mobile) {
        return mobile.substring(0, 3) + mask(4) + mobile.substring(7);
    }
}
