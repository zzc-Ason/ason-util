package com.zzc.ason.handler;

import com.google.common.collect.Lists;
import com.zzc.ason.bean.DynamicBean;
import com.zzc.ason.net.MapBeanUtils;
import com.zzc.ason.util.DatabaseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * author : Ason
 * createTime : 2017 年 07 月 25 日
 * className : SchemaHandler
 * remark: 数据库查询助手
 */
@Slf4j
public final class SchemaHandler {

    /**
     * Describe : 获取数据库信息到动态类集合：List<DynamicBean>
     */
    public static List<DynamicBean> acquireDataByDriver(String sql, String mysqlUrl, String mysqlUser, String mysqlPassword, Object... params) {
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
            log.error("search database failure", e);
            throw new RuntimeException(e);
        } finally {
            DatabaseUtil.closeDataSource();
        }
        return schemaBeanList;
    }

    /**
     * Describe : 未初始化数据库，获取数据库信息到动态类集合：List<DynamicBean>
     */
    public static List<DynamicBean> acquireData(String sql, Object... params) {
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
            log.error("search database failure", e);
            throw new RuntimeException(e);
        }
        return schemaBeanList;
    }

    /**
     * Describe : 获取数据库信息到指定类集合：List<T>
     */
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
            log.error("search database failure", e);
            throw new RuntimeException(e);
        } finally {
            DatabaseUtil.closeDataSource();
        }
        return schemaBeanList;
    }

    /**
     * Describe : 未初始化数据库，获取数据库信息到指定类集合：List<T>
     */
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
            log.error("search database failure", e);
            throw new RuntimeException(e);
        }
        return schemaBeanList;
    }

    /**
     * Describe : 获取指定类信息
     */
    public static <T> T acquireEntity(String sql, Class<T> cls, String... params) {
        return DatabaseUtil.queryEntity(cls, sql, params);
    }

    /**
     * Describe : 获取指定集合信息
     */
    public static <T> List<T> acquireEntityList(String sql, Class<T> cls, String... params) {
        return DatabaseUtil.queryEntityList(cls, sql, params);
    }
}
