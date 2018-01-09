package com.zzc.ason.bean;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.beans.BeanMap;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * author : Ason
 * createTime : 2017 年 07 月 25 日
 * className : DynamicBean
 * remark: beanMap构建的动态类
 */
@Slf4j
public class DynamicBean {

    private Object object = null;
    private BeanMap beanMap = null;

    private HashMap<String, Object> returnMap = Maps.newHashMap();
    private HashMap<String, Object> typeMap = Maps.newHashMap();

    public DynamicBean(Map<String, Object> map) {
        acquireType(map);           // 提取map属性
        initDynamicBean();          // 初始化bean
    }

    public DynamicBean(Object object, Map<String, Object> map) {
        acquireType(object);        // 提取object对象属性
        acquireType(map);           // 提取map属性
        initDynamicBean();          // 初始化bean
    }

    private void initDynamicBean() {
        this.object = generateBean(typeMap);
        this.beanMap = BeanMap.create(this.object);
        assignValue();              // map转换成实体对象，并为实体对象赋值
    }

    private Object generateBean(Map typeMap) {
        BeanGenerator generator = new BeanGenerator();
        Set set = typeMap.keySet();
        for (Iterator i = set.iterator(); i.hasNext(); ) {
            String k = (String) i.next();
            generator.addProperty(k, (Class) typeMap.get(k));
        }
        return generator.create();
    }

    private void acquireType(Object object) {
        try {
            Class type = object.getClass();
            BeanInfo beanInfo = Introspector.getBeanInfo(type);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (int i = 0; i < propertyDescriptors.length; i++) {
                PropertyDescriptor descriptor = propertyDescriptors[i];
                String propertyName = descriptor.getName();
                if (!propertyName.equals("class")) {
                    Method readMethod = descriptor.getReadMethod();
                    Object result = readMethod.invoke(object, new Object[0]);
                    if (result != null) {
                        returnMap.put(propertyName, result);
                    } else {
                        returnMap.put(propertyName, "");
                    }
                    typeMap.put(propertyName, descriptor.getPropertyType());
                }
            }
        } catch (Exception e) {
            log.error("get object field failure", e);
            throw new RuntimeException(e);
        }
    }

    private void assignValue() {
        for (Iterator<String> it = typeMap.keySet().iterator(); it.hasNext(); ) {
            String k = it.next();
            beanMap.put(k, returnMap.get(k));
        }
    }

    private void acquireType(Map<String, Object> map) {
        for (Iterator<String> it = map.keySet().iterator(); it.hasNext(); ) {
            String k = it.next();
            Object v = map.get(k);
            if (v != null) {
                typeMap.put(k, v.getClass());
            } else {
                typeMap.put(k, Object.class);
            }
            returnMap.put(k, v);
        }
    }

    public void setValue(String k, Object v) {
        if (typeMap.containsKey(k)) {
            beanMap.put(k, v);
            return;
        }
        assignTypeAgain(k, v);
        initDynamicBean();
    }

    public void assignTypeAgain(String k, Object v) {
        if (v != null) {
            typeMap.put(k, v.getClass());
        } else {
            typeMap.put(k, Object.class);
        }
        returnMap.put(k, v);
    }

    public Object getObject() {
        return this.object;
    }

    public Object getValue(String k) {
        return beanMap.get(k);
    }

    @Override
    public String toString() {
        return "{object=" + object + ", beanMap=" + beanMap + '}';
    }
}
