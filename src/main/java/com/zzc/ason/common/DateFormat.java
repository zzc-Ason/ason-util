package com.zzc.ason.common;

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

}
