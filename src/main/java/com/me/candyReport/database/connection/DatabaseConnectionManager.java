package com.me.candyReport.database.connection;

import com.me.candyReport.CandyReport;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnectionManager {

    private final CandyReport plugin;
    private HikariDataSource dataSource;

    public DatabaseConnectionManager(CandyReport plugin) {
        this.plugin = plugin;
    }

    public boolean initialize() {
        try {
            HikariConfig config = createHikariConfig();
            this.dataSource = new HikariDataSource(config);
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to initialize database connection: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private HikariConfig createHikariConfig() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s?useSSL=%s&serverTimezone=UTC",
                plugin.getConfigManager().getDatabaseHost(),
                plugin.getConfigManager().getDatabasePort(),
                plugin.getConfigManager().getDatabaseName(),
                plugin.getConfigManager().isDatabaseSSL()));
        config.setUsername(plugin.getConfigManager().getDatabaseUsername());
        config.setPassword(plugin.getConfigManager().getDatabasePassword());
        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        return config;
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new SQLException("Database connection is not initialized or closed");
        }
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public boolean isConnected() {
        return dataSource != null && !dataSource.isClosed();
    }
}