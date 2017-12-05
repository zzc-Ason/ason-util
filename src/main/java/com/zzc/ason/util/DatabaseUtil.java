package com.zzc.ason.util;

import com.google.common.collect.Lists;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * author : Ason
 * createTime : 2017 年 07 月 25 日
 * className : DatabaseUtil
 * remark: 数据库连接助手
 */
@Slf4j
public final class DatabaseUtil {
    private static final QueryRunner QUERY_RUNNER = new QueryRunner();
    private static ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<Connection>();
    private static HikariDataSource dataSource;
    private static String _driverClassName = "com.mysql.jdbc.Driver";
    private static Integer _idleTimeout = 600000;
    private static Integer _maxLifetime = 1800000;
    private static Integer _maximumPoolSize = 2;
    private static Integer _minIdle = 1;

    private static ThreadLocal<Boolean> isTransactionThreadLocal = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {      // 是否需要事务，默认不需要
            return false;
        }
    };

    public static void initialDataSource(String host, String user, String password) {
        initialDataSource(host, user, password, _maximumPoolSize, _minIdle);
    }

    public static void initialDataSource(String host, String user, String password, String driverClassName) {
        initialDataSource(host, user, password, driverClassName, _idleTimeout, _maxLifetime, _maximumPoolSize, _minIdle);
    }

    public static void initialDataSource(String host, String user, String password, Integer maximumPoolSize, Integer minIdle) {
        initialDataSource(host, user, password, _idleTimeout, _maxLifetime, maximumPoolSize, minIdle);
    }

    public static void initialDataSource(String host, String user, String password, Integer idleTimeout, Integer maxLifetime, Integer maximumPoolSize, Integer minIdle) {
        initialDataSource(host, user, password, _driverClassName, idleTimeout, maxLifetime, maximumPoolSize, minIdle);
    }

    public static void initialDataSource(String host, String user, String password, String driverClassName, Integer idleTimeout, Integer maxLifetime, Integer maximumPoolSize, Integer minIdle) {
        try {
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(host);
            hikariConfig.setUsername(user);
            hikariConfig.setPassword(password);
            hikariConfig.setDriverClassName(driverClassName);
            hikariConfig.setIdleTimeout(idleTimeout);
            hikariConfig.setMaxLifetime(maxLifetime);
            hikariConfig.setMaximumPoolSize(maximumPoolSize);
            hikariConfig.setMinimumIdle(minIdle);
            hikariConfig.setConnectionTestQuery("select 1");
            hikariConfig.setConnectionTimeout(30000);
            dataSource = acquireDataSource(hikariConfig);
            log.debug("[initial data source over] [connection url is " + hikariConfig.getJdbcUrl() + "]");
        } catch (Exception e) {
            log.error("[initial database connection failure]", e);
            throw new RuntimeException(e);
        }
    }

    private static HikariDataSource acquireDataSource(HikariConfig hikariConfig) {
        if (dataSource == null || dataSource.isClosed()) dataSource = new HikariDataSource(hikariConfig);
        return dataSource;
    }

    private static Connection acquireConnection() {
        Connection conn = connectionThreadLocal.get();
        if (conn == null) {
            try {
                conn = dataSource.getConnection();
            } catch (SQLException e) {
                log.error("[acquire connection failure]", e);
                throw new RuntimeException(e);
            } finally {
                connectionThreadLocal.set(conn);
            }
        }
        return conn;
    }

    public static List<Map<String, Object>> executeQuery(String sql, Object... params) throws SQLException {
        Boolean isTransaction = isTransactionThreadLocal.get();
        if (isTransaction) return executeQueryTransaction(sql, params);
        return executeQueryNoTransaction(sql, params);
    }

    private static List<Map<String, Object>> executeQueryTransaction(String sql, Object... params) throws SQLException {
        Connection conn = connectionThreadLocal.get();
        if (conn == null) return Lists.newArrayList();
        return QUERY_RUNNER.query(conn, sql, new MapListHandler(), params);
    }

    private static List<Map<String, Object>> executeQueryNoTransaction(String sql, Object... params) throws SQLException {
        try {
            Connection conn = acquireConnection();
            return QUERY_RUNNER.query(conn, sql, new MapListHandler(), params);
        } finally {
            removeTransaction();
            closeConnection();
        }
    }

    public static int executeUpdate(String sql, Object... params) throws SQLException {
        Connection conn = connectionThreadLocal.get();
        if (conn == null) {
            log.error("[execute udpate failure] [conn is null]");
            return -1;
        }
        return QUERY_RUNNER.update(conn, sql, params);
    }

    public static <T> List<T> queryEntityList(String sql, Class<T> entityClass, Object... params) throws SQLException {
        List<T> entityList;
        try {
            Connection conn = acquireConnection();
            entityList = QUERY_RUNNER.query(conn, sql, new BeanListHandler<T>(entityClass), params);
        } finally {
            closeConnection();
        }
        return entityList;
    }

    public static <T> T queryEntity(Class<T> entityClass, String sql, Object... params) throws SQLException {
        T entity;
        try {
            Connection conn = acquireConnection();
            entity = QUERY_RUNNER.query(conn, sql, new BeanHandler<T>(entityClass), params);
        } finally {
            closeConnection();
        }
        return entity;
    }

    public static void beginTransaction() throws SQLException {
        configTransaction();
        try {
            Connection conn = acquireConnection();
            if (conn != null) {
                conn.setAutoCommit(false);
            }
        } finally {
            isTransactionThreadLocal.set(true);
        }
    }

    private static void configTransaction() {
        if (isTransactionThreadLocal.get() == null) isTransactionThreadLocal.set(false);
    }

    public static void commitTransaction() {
        Connection conn = connectionThreadLocal.get();
        if (conn != null) {
            try {
                conn.commit();
            } catch (SQLException e) {
                log.error("[commit transaction failure]", e);
                throw new RuntimeException(e);
            } finally {
                removeTransaction();
            }
        }
    }

    public static void rollbackTransaction() {
        Connection conn = connectionThreadLocal.get();
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                log.error("[rollback transaction failure]", e);
                throw new RuntimeException(e);
            } finally {
                removeTransaction();
            }
        }
    }

    private static void removeTransaction() {
        Boolean isTransaction = isTransactionThreadLocal.get();
        if (isTransaction != null) {
            isTransactionThreadLocal.remove();
        }
    }

    private static void closeConnection() {
        Connection conn = connectionThreadLocal.get();
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                log.error("[close connection failure]", e);
                throw new RuntimeException(e);
            } finally {
                connectionThreadLocal.remove();
            }
            conn = null;
        }
    }

    public static void closeDataSource() {
        closeConnection();
        if (dataSource != null) {
            try {
                dataSource.close();
                log.debug("[close data source]");
            } catch (Exception e) {
                log.error("[close data source failure]", e);
                throw new RuntimeException(e);
            }
            dataSource = null;
        }
    }
}
