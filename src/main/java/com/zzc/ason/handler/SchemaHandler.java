package com.zzc.ason.handler;

import com.google.common.collect.Lists;
import com.zzc.ason.bean.DynamicBean;
import com.zzc.ason.common.Done;
import com.zzc.ason.net.MapBeanUtils;
import com.zzc.ason.util.DatabaseUtil;
import org.apache.log4j.Logger;

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

    public static List<DynamicBean> acquireDataFromDB(String sql, String startTime, String endTime, String mysqlUrl, String mysqlUser, String mysqlPassword) {
        List<DynamicBean> schemaBeanList = Lists.newCopyOnWriteArrayList();
        DatabaseUtil.initialDataSource(mysqlUrl, mysqlUser, mysqlPassword);
        try {
            List<Map<String, Object>> mysqlReturnMap = DatabaseUtil.executeQuery(sql, startTime, endTime);
            for (Map<String, Object> returnMap : mysqlReturnMap) {
                DynamicBean dynamicBean = new DynamicBean(returnMap);
                schemaBeanList.add(dynamicBean);
            }
        } catch (Exception e) {
            LOGGER.error("search database failure", e);
            throw new RuntimeException(e);
        } finally {
            DatabaseUtil.closeDataSource();
        }
        LOGGER.info("acquire data from database over. size: " + schemaBeanList.size());
        return schemaBeanList;
    }

    public static <T> List<T> acquireDataFromDB(String sql, String startTime, String endTime, String mysqlUrl, String mysqlUser, String mysqlPassword, Class<T> cls) {
        List<T> schemaBeanList = Lists.newCopyOnWriteArrayList();
        DatabaseUtil.initialDataSource(mysqlUrl, mysqlUser, mysqlPassword);
        try {
            List<Map<String, Object>> mysqlReturnMap = DatabaseUtil.executeQuery(sql, startTime, endTime);
            for (Map<String, Object> returnMap : mysqlReturnMap) {
                T t = MapBeanUtils.mapToObject(returnMap, cls);
                schemaBeanList.add(t);
            }
        } catch (Exception e) {
            LOGGER.error("search database failure", e);
            throw new RuntimeException(e);
        } finally {
            DatabaseUtil.closeDataSource();
        }
        LOGGER.info("acquire data from database over. size: " + schemaBeanList.size());
        return schemaBeanList;
    }

    public static String acquireDataFromDB(String sql, Object... params) {
        try {
            List<Map<String, Object>> mysqlReturnMap = DatabaseUtil.executeQuery(sql, params);
            for (Map<String, Object> returnMap : mysqlReturnMap) {
                Object result = returnMap.get("plaintext");
                return Done.parseStr(result);
            }
        } catch (Exception e) {
            LOGGER.error("search database failure", e);
            throw new RuntimeException(e);
        }
        LOGGER.info("acquire data from database over.");
        return null;
    }
}
