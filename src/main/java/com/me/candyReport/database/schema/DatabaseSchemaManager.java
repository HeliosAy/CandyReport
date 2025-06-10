package com.me.candyReport.database.schema;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseSchemaManager {

    public void createTables(Connection connection) throws SQLException {
        createReportsTable(connection);
    }

    private void createReportsTable(Connection connection) throws SQLException {
        String reportsTable = """
CREATE TABLE IF NOT EXISTS candyreport_reports (
    id INT AUTO_INCREMENT PRIMARY KEY,
    reported_player VARCHAR(36) NOT NULL,
    reported_player_name VARCHAR(64) NOT NULL,
    reporter_player VARCHAR(36) NOT NULL,
    reporter_player_name VARCHAR(64) NOT NULL,
    reason TEXT NOT NULL,
    server_name VARCHAR(64) NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    report_messages TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    handled_by VARCHAR(36),
    handled_at TIMESTAMP NULL,
    INDEX idx_reported_player (reported_player),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci
""";

        try (PreparedStatement stmt = connection.prepareStatement(reportsTable)) {
            stmt.executeUpdate();
        }
    }


}