package com.zzc.ason.handler;

import com.google.common.collect.Lists;
import com.zzc.ason.bean.DynamicBean;
import com.zzc.ason.net.MapBeanUtils;
import com.zzc.ason.util.DatabaseUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * author : Ason
 * createTime : 2017 年 07 月 25 日
 * className : SchemaHandler
 * remark: 数据库查询助手
 */
public final class SchemaHandler {
    private static final Logger LOGGER = Logger.getLogger(SchemaHandler.class);

    public static List<DynamicBean> acquireDataFromDB(String sql, String mysqlUrl, String mysqlUser, String mysqlPassword, Object... params) {
        List<DynamicBean> schemaBeanList = Lists.newCopyOnWriteArrayList();
        DatabaseUtil.initialDataSource(mysqlUrl, mysqlUser, mysqlPassword);
        try {
            List<Map<String, Object>> mysqlReturnMap = DatabaseUtil.executeQuery(sql, params);
            if (CollectionUtils.isEmpty(mysqlReturnMap)) return schemaBeanList;

            Iterator<Map<String, Object>> iterator = mysqlReturnMap.iterator();
            while (iterator.hasNext()) {
                Map<String, Object> returnMap = iterator.next();
                DynamicBean dynamicBean = new DynamicBean(returnMap);
                schemaBeanList.add(dynamicBean);
            }
        } catch (Exception e) {
            LOGGER.error("search database failure", e);
            throw new RuntimeException(e);
        } finally {
            DatabaseUtil.closeDataSource();
        }
        return schemaBeanList;
    }

    public static List<DynamicBean> acquireDataFromDB(String sql, Object... params) {
        List<DynamicBean> schemaBeanList = Lists.newCopyOnWriteArrayList();
        try {
            List<Map<String, Object>> mysqlReturnMap = DatabaseUtil.executeQuery(sql, params);
            if (CollectionUtils.isEmpty(mysqlReturnMap)) return schemaBeanList;

            Iterator<Map<String, Object>> iterator = mysqlReturnMap.iterator();
            while (iterator.hasNext()) {
                Map<String, Object> returnMap = iterator.next();
                DynamicBean dynamicBean = new DynamicBean(returnMap);
                schemaBeanList.add(dynamicBean);
            }
        } catch (Exception e) {
            LOGGER.error("search database failure", e);
            throw new RuntimeException(e);
        }
        return schemaBeanList;
    }

    public static <T> List<T> acquireDataFromDB(String sql, String mysqlUrl, String mysqlUser, String mysqlPassword, Class<T> cls, Object... params) {
        List<T> schemaBeanList = Lists.newCopyOnWriteArrayList();
        try {
            DatabaseUtil.initialDataSource(mysqlUrl, mysqlUser, mysqlPassword);
            List<Map<String, Object>> mysqlReturnMap = DatabaseUtil.executeQuery(sql, params);
            if (CollectionUtils.isEmpty(mysqlReturnMap)) return schemaBeanList;

            Iterator<Map<String, Object>> iterator = mysqlReturnMap.iterator();
            while (iterator.hasNext()) {
                Map<String, Object> returnMap = iterator.next();
                T t = MapBeanUtils.mapToObject(returnMap, cls);
                schemaBeanList.add(t);
            }
        } catch (Exception e) {
            LOGGER.error("search database failure", e);
            throw new RuntimeException(e);
        } finally {
            DatabaseUtil.closeDataSource();
        }
        return schemaBeanList;
    }

    public static <T> List<T> acquireDataFromDB(String sql, Class<T> cls, Object... params) {
        List<T> schemaBeanList = Lists.newCopyOnWriteArrayList();
        try {
            List<Map<String, Object>> mysqlReturnMap = DatabaseUtil.executeQuery(sql, params);
            if (CollectionUtils.isEmpty(mysqlReturnMap)) return schemaBeanList;

            Iterator<Map<String, Object>> iterator = mysqlReturnMap.iterator();
            while (iterator.hasNext()) {
                Map<String, Object> returnMap = iterator.next();
                T t = MapBeanUtils.mapToObject(returnMap, cls);
                schemaBeanList.add(t);
            }
        } catch (Exception e) {
            LOGGER.error("search database failure", e);
            throw new RuntimeException(e);
        }
        return schemaBeanList;
    }
}
