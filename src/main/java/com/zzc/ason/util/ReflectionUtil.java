package com.zzc.ason.util;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * author : Ason
 * createTime : 2017 年 07 月 22 日 下午 3:05
 */
@Slf4j
public final class ReflectionUtil {

    public static <T> T newInstance(Class<T> cls) {
        T instance;
        try {
            instance = cls.newInstance();
        } catch (Exception e) {
            log.error("[new instance failure]");
            throw new RuntimeException(e);
        }
        return instance;
    }

    public static Object newInstance(String className) {
        try {
            Class<?> cls = Class.forName(className);
            return newInstance(cls);
        } catch (ClassNotFoundException e) {
            log.error("[new instance failure]");
            throw new RuntimeException(e);
        }
    }

    public static Object invokeMethod(Object obj, Method method, Object... args) {
        Object result;
        try {
            method.setAccessible(true);
            result = method.invoke(obj, args);
        } catch (Exception e) {
            log.error("[invoke method failure]");
            throw new RuntimeException(e);
        }
        return result;
    }

    public static void setField(Object obj, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            log.error("[set field failure]");
            throw new RuntimeException(e);
        }
    }

    public static void setValueByFieldName(Object obj, String fieldName, Object value) {
        try {
            Field[] declaredFields = obj.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                if (StringUtils.equals(field.getName(), fieldName)) {
                    PropertyDescriptor pd = new PropertyDescriptor(fieldName, obj.getClass());
                    Method writeMethod = pd.getWriteMethod();
                    if (writeMethod == null) return;
                    writeMethod.invoke(obj, value);
                }
            }
        } catch (Exception e) {
            log.error("[set value of field \"" + fieldName + "\" failure]");
            throw new RuntimeException(e);
        }
    }

    public static Object acquireValueByFieldName(Object obj, String fieldName) {
        if (obj == null || StringUtils.isBlank(fieldName)) return null;
        try {
            Field[] declaredFields = obj.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                if (StringUtils.equals(field.getName(), fieldName)) {
                    PropertyDescriptor pd = new PropertyDescriptor(fieldName, obj.getClass());
                    Method getMethod = pd.getReadMethod();
                    return getMethod == null ? null : getMethod.invoke(obj);
                }
            }
            return null;
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> Map<String, Type> acquireClassType(Class<T> cls) {
        Map<String, Type> classType = Maps.newHashMap();
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            Type declaringClass = field.getGenericType();
            if (!classType.containsKey(fieldName)) classType.put(fieldName, declaringClass);
        }
        return classType;
    }
}
