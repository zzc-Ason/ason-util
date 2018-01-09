package com.zzc.ason.common;

import lombok.extern.slf4j.Slf4j;

/**
 * author : Ason
 * createTime : 2017 年 12 月 21 日
 */
@Slf4j
public class Log {

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
}
