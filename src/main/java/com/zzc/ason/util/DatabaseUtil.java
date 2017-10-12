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
        initialDataSource(host, user, password, _idleTimeout, _maxLifetime, _maximumPoolSize, _minIdle);
    }

    public static void initialDataSource(String host, String user, String password, String driverClassName) {
        initialDataSource(host, user, password, driverClassName, _idleTimeout, _maxLifetime, _maximumPoolSize, _minIdle);
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
            log.info("initial data source over. connection url: " + hikariConfig.getJdbcUrl());
        } catch (Exception e) {
            log.error("initial database connection failure", e);
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
                log.error("get connection failure", e);
                throw new RuntimeException(e);
            } finally {
                connectionThreadLocal.set(conn);
            }
        }
        return conn;
    }

    public static List<Map<String, Object>> executeQuery(String sql, Object... params) {
        Boolean isTransaction = isTransactionThreadLocal.get();
        try {
            if (isTransaction) return executeQueryTransaction(sql, params);
        } catch (Exception e) {
            log.error("execute query failure", e);
            throw new RuntimeException(e);
        } finally {
            isTransactionThreadLocal.remove();
        }
        return executeQueryNoTransaction(sql, params);
    }

    private static List<Map<String, Object>> executeQueryTransaction(String sql, Object... params) {
        List<Map<String, Object>> result = Lists.newArrayList();
        try {
            Connection conn = connectionThreadLocal.get();
            if (conn == null) return null;
            result = QUERY_RUNNER.query(conn, sql, new MapListHandler(), params);
        } catch (SQLException e) {
            log.error("execute query failure", e);
            throw new RuntimeException(e);
        }
        return result;
    }

    private static List<Map<String, Object>> executeQueryNoTransaction(String sql, Object... params) {
        List<Map<String, Object>> result = Lists.newArrayList();
        try {
            Connection conn = acquireConnection();
            result = QUERY_RUNNER.query(conn, sql, new MapListHandler(), params);
        } catch (SQLException e) {
            log.error("execute query failure", e);
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
        return result;
    }

    public static int executeUpdate(String sql, Object... params) {
        int rows = 0;
        try {
            Connection conn = connectionThreadLocal.get();
            if (conn == null) return -1;
            rows = QUERY_RUNNER.update(conn, sql, params);
        } catch (SQLException e) {
            log.error("execute update failure", e);
            throw new RuntimeException(e);
        }
        return rows;
    }

    public static <T> List<T> queryEntityList(Class<T> entityClass, String sql, Object... params) {
        List<T> entityList;
        try {
            Connection conn = acquireConnection();
            entityList = QUERY_RUNNER.query(conn, sql, new BeanListHandler<T>(entityClass), params);
        } catch (SQLException e) {
            log.error("query entity list failure", e);
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
        return entityList;
    }

    public static <T> T queryEntity(Class<T> entityClass, String sql, Object... params) {
        T entity;
        try {
            Connection conn = acquireConnection();
            entity = QUERY_RUNNER.query(conn, sql, new BeanHandler<T>(entityClass), params);
        } catch (SQLException e) {
            log.error("query entity failure", e);
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
        return entity;
    }

    public static void beginTransaction() {
        try {
            Boolean isTransaction = acquireTransaction();
            Connection conn = acquireConnection();
            if (conn != null) {
                try {
                    conn.setAutoCommit(false);
                } catch (SQLException e) {
                    log.error("begin transaction failure", e);
                    throw new RuntimeException(e);
                }
            }
        } catch (RuntimeException e) {
            log.error("begin transaction failure", e);
            throw new RuntimeException(e);
        } finally {
            isTransactionThreadLocal.set(true);
        }
    }

    private static Boolean acquireTransaction() {
        Boolean isTransaction = isTransactionThreadLocal.get();
        try {
            if (isTransaction == null) {
                isTransaction = false;
            }
        } catch (Exception e) {
            log.error("acquire transaction failure", e);
            throw new RuntimeException(e);
        } finally {
            isTransactionThreadLocal.set(isTransaction);
        }
        return isTransaction;
    }

    public static void commitTransaction() {
        Connection conn = connectionThreadLocal.get();
        if (conn != null) {
            try {
                conn.commit();
            } catch (SQLException e) {
                log.error("commit transaction failure", e);
                throw new RuntimeException(e);
            } finally {
                removeTransaction();
                closeConnection();
            }
        }
    }

    public static void rollbackTransaction() {
        Connection conn = connectionThreadLocal.get();
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                log.error("rollback transaction failure", e);
                throw new RuntimeException(e);
            } finally {
                removeTransaction();
                closeConnection();
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
                log.error("close connection failure", e);
                throw new RuntimeException(e);
            } finally {
                connectionThreadLocal.remove();
            }
            conn = null;
        }
    }

    public static void closeDataSource() {
        if (dataSource != null) {
            try {
                dataSource.close();
                log.info("close data source.");
            } catch (Exception e) {
                log.error("close data source failure", e);
                throw new RuntimeException(e);
            }
            dataSource = null;
        }
    }
}
