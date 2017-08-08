package com.zzc.ason.common;

import org.apache.commons.lang3.StringUtils;

/**
 * author : Ason
 * createTime : 2017 年 08 月 07 日
 */
public class Symbol {

    public static final String BLANK = " ";         // 空格
    public static final String COMMA = ",";         // 逗号
    public static final String BR = "<br>";         // 换行符
    public static final String RN = "\r\n";         // 换行符
    public static final String TAG = "\t";          // 制表符
    public static final String DOT = ".";           // 点
    public static final String UNDERLINE = "_";    // 下划线
    public static final String GROUP_SEPARATOR = "<->";    // 组分隔符
    public static final String LINUX_SEPARATOR = "/";      // linux分隔符
    public static final String WINDOWS_SEPARATOR = "\\";   // windows分隔符
    public static final String MASK = "****************";  // 掩码
    public static final String SINGLE_MASK = "*";           // 单掩码

    public static String mask(Integer count) {
        StringBuilder sb = new StringBuilder(StringUtils.EMPTY);
        for (int i = 0; i < count; i++) {
            sb.append(SINGLE_MASK);
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
