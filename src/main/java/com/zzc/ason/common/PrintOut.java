package com.zzc.ason.common;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * author : Ason
 * createTime : 2017 年 07 月 25 日
 * className : PrintOut
 * remark: 日志操作助手
 */
public final class PrintOut {
    private static final Logger LOGGER = Logger.getLogger(PrintOut.class);

    public static void debug(String info, Throwable... throwable) {
        if (throwable.length == 0) LOGGER.debug(info);
        for (Throwable e : throwable) {
            LOGGER.debug(info, e);
        }
    }

    public static void info(String info, Throwable... throwable) {
        if (throwable.length == 0) LOGGER.info(info);
        for (Throwable e : throwable) LOGGER.info(info, e);
    }

    public static void warn(String info, Throwable... throwable) {
        if (throwable.length == 0) LOGGER.warn(info);
        for (Throwable e : throwable) LOGGER.warn(info, e);

    }

    public static void error(String info, Throwable... throwable) {
        if (throwable.length == 0) LOGGER.error(info);
        for (Throwable e : throwable) LOGGER.error(info, e);
    }

    public static void object(Object object) {
        if (object == null) LOGGER.info(null);
        else LOGGER.info(object.toString());
    }

    public static void str(String str) {
        LOGGER.info(str);
    }

    public static <T> void list(List<T> list) {
        for (T t : list) {
            if (t == null)
                LOGGER.info(null);
            else
                LOGGER.info(t.toString());
        }
    }

    public static void map(Map<?, ?> map) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (entry == null)
                LOGGER.info(null + Symbol.GROUP_SEPARATOR + null);
            else
                LOGGER.info(entry.getKey() + Symbol.GROUP_SEPARATOR + entry.getValue());
        }
    }

    public static <T> void set(Set<T> set) {
        for (T t : set) {
            if (t == null)
                LOGGER.info(null);
            else
                LOGGER.info(t.toString());
        }
    }

    public static void main(String[] args) throws Exception {

    }
}
