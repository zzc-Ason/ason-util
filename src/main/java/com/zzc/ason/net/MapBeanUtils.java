package com.zzc.ason.net;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zzc.ason.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * author : Ason
 * createTime : 2017 年 07 月 25 日
 * className : MapBeanUtils
 * remark: map与javaBean转换助手
 */
public final class MapBeanUtils {

    public static <T> T mapToObject(Map<String, Object> map, Class<T> beanClass) throws Exception {
        if (map == null || map.size() <= 0) return null;
        Map<String, Type> classType = ReflectionUtil.acquireClassType(beanClass);
        T obj = beanClass.newInstance();


        List<Class> cls = Lists.newArrayList();
        Class oo = obj.getClass();
        while (oo != null && oo != Object.class) {      // 获取关联的所有类，本类以及所有父类
            cls.add(oo);
            oo = oo.getSuperclass();
        }
        for (int i = 0; i < cls.size(); i++) {
            Field[] fields = cls.get(i).getDeclaredFields();
            for (Field field : fields) {
                int mod = field.getModifiers();

                if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) continue;  // 过滤不需要的类型如 static final

                if (field != null) {    // 由字符串转换回对象对应的类型
                    field.setAccessible(true);
                    field.set(obj, map.get(field.getName()));
                }
            }
        }
        return obj;
    }

    public static Map<String, Object> objectToMap(Object obj) throws Exception {
        if (obj == null) return null;
        List<Class> cls = Lists.newArrayList();
        Class oo = obj.getClass();
        while (oo != null && oo != Object.class) {       // 获取关联的所有类，本类以及所有父类
            cls.add(oo);
            oo = oo.getSuperclass();
        }
        Map<String, Object> map = Maps.newHashMap();
        for (int i = 0; i < cls.size(); i++) {
            Field[] declaredFields = cls.get(i).getDeclaredFields();
            for (Field field : declaredFields) {
                int mod = field.getModifiers();

                if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) continue;  // 过滤 static 和 final 类型

                field.setAccessible(true);
                map.put(field.getName(), field.get(obj));
            }
        }
        return map;
    }
}