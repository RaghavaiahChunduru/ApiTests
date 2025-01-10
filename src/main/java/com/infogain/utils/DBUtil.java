package com.infogain.utils;

import static com.infogain.utils.ConfigUtil.CONFIG;

import java.sql.Connection;
import java.sql.DriverManager;

import com.machinezoo.noexception.Exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DBUtil {

    private static final String redshiftDbUrl = CONFIG.getString("REDSHIFT_DB_URL");
    private static final String redshiftUserName = CONFIG.getString("REDSHIFT_DB_USERNAME");
    private static final String redshiftPassword = CONFIG.getString("REDSHIFT_DB_PASSWORD");

    private static final String mysqlDbUrl = CONFIG.getString("MYSQL_DB_URL");
    private static final String mysqlUserName = CONFIG.getString("MYSQL_DB_USERNAME");
    private static final String mysqlPassword = CONFIG.getString("MYSQL_DB_PASSWORD");

    public static Connection createRedshiftConnection() {
        return Exceptions.wrap(e -> new RuntimeException("Failed to connect to Redshift database", e))
                .get(() -> {
                    Connection connection = DriverManager.getConnection(redshiftDbUrl, redshiftUserName,
                            redshiftPassword);
                    log.info("RedShift connection established");
                    return connection;
                });
    }

    public static Connection createMySQLConnection() {
        return Exceptions.wrap(e -> new RuntimeException("Failed to connect to MySQL database", e))
                .get(() -> {
                    Connection connection = DriverManager.getConnection(mysqlDbUrl, mysqlUserName, mysqlPassword);
                    log.info("MySQL connection established");
                    return connection;
                });
    }
}
