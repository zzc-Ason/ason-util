package com.zzc.ason.util;

import com.google.common.collect.Lists;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.log4j.Logger;

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
public final class DatabaseUtil {
    private static final Logger LOGGER = Logger.getLogger(DatabaseUtil.class);

    private static final QueryRunner QUERY_RUNNER = new QueryRunner();
    private static ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<Connection>();
    private static BasicDataSource dataSource;
    private static String _driverClassName = "com.mysql.jdbc.Driver";
    private static Integer _initialSize = 3;
    private static Integer _maxActive = 2;
    private static Integer _maxIdle = 2;
    private static Integer _minIdle = 1;
    private static Integer _maxWaitMillis = 60 * 1000;

    private static ThreadLocal<Boolean> isTransactionThreadLocal = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {      // 是否需要事务，默认不需要
            return false;
        }
    };

    private DatabaseUtil() {
    }

    public static void initialDataSource(String host, String user, String password) {
        try {
            dataSource = acquireDataSource();
            // 为数据源实例指定必须的属性
            dataSource.setUrl(host);
            dataSource.setUsername(user);
            dataSource.setPassword(password);
            dataSource.setDriverClassName(_driverClassName);
            // 指定数据库连接池中初始化连接数的个数
            dataSource.setInitialSize(_initialSize);
            // 指定最大连接数：同一时刻可以同时向数据库申请的连接数,默认为8.
            dataSource.setMaxActive(_maxActive);
            // 指定最大连接数:在数据库连接池中保存的最多的空闲连接的数量,默认为8.
            dataSource.setMaxIdle(_maxIdle);
            // 指定最小连接数:在数据库连接池中保存的最少的空闲连接的数量,默认为0.
            dataSource.setMinIdle(_minIdle);
            // 等待数据库连接池分配连接的最长时间。单位为毫秒。超出时间将抛出异常.默认为-1.表示永不超时.
            dataSource.setMaxWait(_maxWaitMillis);
            // 连接被认为是被泄露（超时）时，是否可以被删除
            dataSource.setRemoveAbandoned(true);
            // 泄露的连接可以被删除的超时值，单位秒
            dataSource.setRemoveAbandonedTimeout(180);
            // SQL查询,用来验证从连接池取出的连接,在将连接返回给调用者之前.如果指定,则查询必须是一个SQL SELECT并且必须返回至少一行记录
            dataSource.setValidationQuery("select 1");
            //  向调用者输出“链接”资源时，是否检测有效性，如果无效则从连接池中移除，并尝试获取继续获取。默认为false。注意: 设置为true后如果要生效,validationQuery参数必须设置为非空字符串
            dataSource.setTestOnBorrow(false);
            // 指明连接是否被空闲连接回收器(如果有)进行检验.如果检测失败,则连接将被从池中去除。默认为false。注意: 设置为true后如果要生效,validationQuery参数必须设置为非空字符串
            dataSource.setTestOnReturn(true);
            //  “空闲链接”检测线程，检测的周期，毫秒数。如果为负值，表示不运行“检测线程”。单位毫秒，默认为-1.
            dataSource.setTimeBetweenEvictionRunsMillis(30000);
            // 连接空闲的最小时间，达到此值后空闲连接将可能会被移除。负值(-1)表示不移除。单位毫秒，默认为30分钟.
            dataSource.setMinEvictableIdleTimeMillis(1800000);
            // 对于“空闲链接”检测线程而言，每次检测的链接资源的个数。默认为3.
            dataSource.setNumTestsPerEvictionRun(3);
            // 指明连接是否被空闲连接回收器(如果有)进行检验.如果检测失败,则连接将被从池中去除.
            dataSource.setTestWhileIdle(true);
            LOGGER.info("initial data source over. connection url: " + dataSource.getUrl());
        } catch (Exception e) {
            LOGGER.error("initial database connection failure", e);
            throw new RuntimeException(e);
        }
    }

    public static void initialDataSource(String host, String user, String password, String driverClassName) {
        _driverClassName = driverClassName;
        initialDataSource(host, user, password);
    }

    public static void initialDataSource(String host, String user, String password, Integer initialSize, Integer maxActive, Integer maxIdle, Integer minIdle, Integer maxWaitMillis) {
        _initialSize = initialSize;
        _maxActive = maxActive;
        _maxIdle = maxIdle;
        _minIdle = minIdle;
        _maxWaitMillis = maxWaitMillis;
        initialDataSource(host, user, password);
    }

    public static void initialDataSource(String host, String user, String password, String driverClassName, Integer initialSize, Integer maxActive, Integer maxIdle, Integer minIdle, Integer maxWaitMillis) {
        _driverClassName = driverClassName;
        _initialSize = initialSize;
        _maxActive = maxActive;
        _maxIdle = maxIdle;
        _minIdle = minIdle;
        _maxWaitMillis = maxWaitMillis;
        initialDataSource(host, user, password);
    }

    private static BasicDataSource acquireDataSource() {
        if (dataSource == null || dataSource.isClosed()) dataSource = new BasicDataSource();
        return dataSource;
    }

    private static Connection acquireConnection() {
        Connection conn = connectionThreadLocal.get();
        if (conn == null) {
            try {
                conn = dataSource.getConnection();
            } catch (SQLException e) {
                LOGGER.error("get connection failure", e);
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
            LOGGER.error("execute query failure", e);
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
            LOGGER.error("execute query failure", e);
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
            LOGGER.error("execute query failure", e);
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
            LOGGER.error("execute update failure", e);
            throw new RuntimeException(e);
        }
        return rows;
    }

    public static void beginTransaction() {
        try {
            Boolean isTransaction = acquireTransaction();
            Connection conn = acquireConnection();
            if (conn != null) {
                try {
                    conn.setAutoCommit(false);
                } catch (SQLException e) {
                    LOGGER.error("begin transaction failure", e);
                    throw new RuntimeException(e);
                }
            }
        } catch (RuntimeException e) {
            LOGGER.error("begin transaction failure", e);
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
            LOGGER.error("acquire transaction failure", e);
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
                LOGGER.error("commit transaction failure", e);
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
                LOGGER.error("rollback transaction failure", e);
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
                LOGGER.error("close connection failure", e);
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
            } catch (SQLException e) {
                LOGGER.error("close data source failure", e);
                throw new RuntimeException(e);
            }
            dataSource = null;
        }
    }
}
