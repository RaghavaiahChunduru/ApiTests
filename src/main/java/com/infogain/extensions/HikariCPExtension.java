package com.infogain.extensions;

import static com.infogain.utils.ConfigUtil.CONFIG;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

@Slf4j
public class HikariCPExtension implements BeforeAllCallback, AfterAllCallback {

    private static HikariDataSource dataSource;

    public static DataSource getDataSource() {
        if (dataSource == null || dataSource.isClosed()) {
            throw new IllegalStateException("HikariCP DataSource is not initialized.");
        }
        return dataSource;
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        log.info("Initializing HikariCP DataSource.");

        // Load HikariCP configurations
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(CONFIG.getString("MYSQL_DB_URL"));
        hikariConfig.setUsername(CONFIG.getString("MYSQL_DB_USERNAME"));
        hikariConfig.setPassword(CONFIG.getString("MYSQL_DB_PASSWORD"));
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setIdleTimeout(30000);
        hikariConfig.setConnectionTimeout(30000);
        hikariConfig.setMaxLifetime(1800000);

        // Initialize the DataSource
        dataSource = new HikariDataSource(hikariConfig);

        log.info("HikariCP DataSource initialized successfully.");
    }

    @Override
    public void afterAll(ExtensionContext context) {
        if (dataSource != null && !dataSource.isClosed()) {
            log.info("Closing HikariCP DataSource.");
            dataSource.close();
            log.info("HikariCP DataSource closed successfully.");
        }
    }
}
