package com.zzc.ason.common;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * author : Ason
 * createTime : 2017 年 07 月 25 日
 * className : PrintOut
 * remark: 日志操作助手
 */
@Slf4j
public final class PrintOut {

    public static void debug(String info, Throwable... throwable) {
        if (throwable.length == 0) log.debug(info);
        for (Throwable e : throwable) {
            log.debug(info, e);
        }
    }

    public static void info(String info, Throwable... throwable) {
        if (throwable.length == 0) log.info(info);
        for (Throwable e : throwable) log.info(info, e);
    }

    public static void warn(String info, Throwable... throwable) {
        if (throwable.length == 0) log.warn(info);
        for (Throwable e : throwable) log.warn(info, e);

    }

    public static void error(String info, Throwable... throwable) {
        if (throwable.length == 0) log.error(info);
        for (Throwable e : throwable) log.error(info, e);
    }

    public static void object(Object object) {
        if (object == null) log.info(null);
        else log.info(object.toString());
    }

    public static void str(String str) {
        log.info(str);
    }

    public static <T> void list(List<T> list) {
        for (T t : list) {
            if (t == null)
                log.info(null);
            else
                log.info(t.toString());
        }
    }

    public static void map(Map<?, ?> map) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (entry == null)
                log.info(null + Symbol.GROUP_SEPARATOR + null);
            else
                log.info(entry.getKey() + Symbol.GROUP_SEPARATOR + entry.getValue());
        }
    }

    public static <T> void set(Set<T> set) {
        for (T t : set) {
            if (t == null)
                log.info(null);
            else
                log.info(t.toString());
        }
    }

    public static void main(String[] args) throws Exception {

    }
}
