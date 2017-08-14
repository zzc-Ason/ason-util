package com.zzc.ason.util;

import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * author : Ason
 * createTime : 2017 年 07 月 22 日 下午 3:05
 */
public final class ReflectionUtil {

    private static final Logger LOGGER = Logger.getLogger(ReflectionUtil.class);

    public static <T> T newInstance(Class<T> cls) {
        T instance;
        try {
            instance = cls.newInstance();
        } catch (Exception e) {
            LOGGER.error("new instance failure", e);
            throw new RuntimeException(e);
        }
        return instance;
    }

    public static Object newInstance(String className) {
        try {
            Class<?> cls = Class.forName(className);
            return newInstance(cls);
        } catch (ClassNotFoundException e) {
            LOGGER.error("new instance failure", e);
            throw new RuntimeException(e);
        }
    }

    public static Object invokeMethod(Object obj, Method method, Object... args) {
        Object result;
        try {
            method.setAccessible(true);
            result = method.invoke(obj, args);
        } catch (Exception e) {
            LOGGER.error("invoke method failure", e);
            throw new RuntimeException(e);
        }
        return result;
    }

    public static void setField(Object obj, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            LOGGER.error("set field failure", e);
            throw new RuntimeException(e);
        }
    }
}
