package com.zzc.ason.common;

/**
 * author : Ason
 * createTime : 2017 年 07 月 25 日
 * className : Constant
 */
public final class Constant {

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

    public static final String DATE_FORMAT_1 = "yyyyMMdd";                 // 时间格式1
    public static final String DATE_FORMAT_2 = "yyyy-MM-dd HH:mm:ss";     // 时间格式2
    public static final String DATE_FORMAT_3 = "yyyy-MM-dd/HH:mm:ss";     // 时间格式3
    public static final String DATE_FORMAT_4 = "yyyy-MM-dd HH:mm:ss.S";   // 时间格式4
    public static final String DATE_FORMAT_5 = "yyyy/MM/dd HH:mm:ss";     // 时间格式5
    public static final String DATE_FORMAT_6 = "yyyy-MM-dd";     // 时间格式6
    public static final String DATE_FORMAT_7 = "yyyy/MM/dd";     // 时间格式7

    public static final String[] DATE_FORMAT_PATTERN = new String[]{DATE_FORMAT_1, DATE_FORMAT_2, DATE_FORMAT_3, DATE_FORMAT_4, DATE_FORMAT_5, DATE_FORMAT_6, DATE_FORMAT_7};    // 时间解析格式

}
