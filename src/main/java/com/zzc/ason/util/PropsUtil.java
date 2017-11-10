package com.zzc.ason.util;


import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * author : Ason
 * createTime : 2017 年 07 月 27 日
 * className : PropsUtil
 * remark: 属性文件读取类
 */
@Slf4j
public final class PropsUtil {

    public static Properties loadProps(String fileName) {
        Properties props = null;
        InputStream is = null;
        try {
            is = ClassUtil.getClassLoader().getResourceAsStream(fileName);
            if (is == null) {
                throw new FileNotFoundException("[file is not found] [file name is: " + fileName + "]");
            }
            props = new Properties();
            props.load(is);
        } catch (IOException e) {
            log.error("[load properties file failure]", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error("[close input stream failure]", e);
                }
            }
        }
        return props;
    }

    public static String getString(Properties props, String key) {
        return getString(props, key, "");
    }

    private static String getString(Properties props, String key, String defaultValue) {
        String value = defaultValue;
        if (props.contains(key)) {
            value = props.getProperty(key);
        }
        return value;
    }

    public static int getInt(Properties props, String key) {
        return getInt(props, key, 0);
    }

    private static int getInt(Properties props, String key, int defaultValue) {
        int value = defaultValue;
        if (props.contains(key)) {
            value = CastUtil.castInt(props.getProperty(key));
        }
        return value;
    }

    public static boolean getBoolean(Properties props, String key) {
        return getBoolean(props, key, false);
    }

    private static boolean getBoolean(Properties props, String key, Boolean defaultValue) {
        boolean value = defaultValue;
        if (props.contains(key)) {
            value = CastUtil.castBoolean(props.getProperty(key));
        }
        return value;
    }

    public static double getDouble(Properties props, String key) {
        return getDouble(props, key, 0.0);
    }

    private static double getDouble(Properties props, String key, Double defaultValue) {
        double value = defaultValue;
        if (props.contains(key)) {
            value = CastUtil.castDouble(props.getProperty(key));
        }
        return value;
    }
}
