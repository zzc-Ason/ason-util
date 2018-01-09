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

    public static void object(Object object) {
        if (object == null) log.info(null);
        else log.info(object.toString());
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
                log.info("{" + null + Symbol.COLON + null + "}");
            else
                log.info("{" + entry.getKey() + Symbol.COLON + entry.getValue() + "}");
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
}
