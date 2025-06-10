package com.me.candyReport.database;

import com.me.candyReport.CandyReport;
import com.me.candyReport.database.connection.DatabaseConnectionManager;
import com.me.candyReport.database.repository.MessageRepository;
import com.me.candyReport.database.repository.ReportRepository;
import com.me.candyReport.database.schema.DatabaseSchemaManager;
import com.me.candyReport.models.Report;
import com.me.candyReport.models.StoredMessage;

import java.sql.Connection;
import java.util.List;
import java.util.UUID;

public class DatabaseManager {

    private final CandyReport plugin;
    private final DatabaseConnectionManager connectionManager;
    private final DatabaseSchemaManager schemaManager;
    private final MessageRepository messageRepository;
    private final ReportRepository reportRepository;

    public DatabaseManager(CandyReport plugin) {
        this.plugin = plugin;
        this.connectionManager = new DatabaseConnectionManager(plugin);
        this.schemaManager = new DatabaseSchemaManager();
        this.messageRepository = new MessageRepository(plugin);
        this.reportRepository = new ReportRepository(plugin, connectionManager);
    }

    public boolean initialize() {
        try {
            if (!connectionManager.initialize()) {
                return false;
            }

            try (Connection connection = connectionManager.getConnection()) {
                schemaManager.createTables(connection);
            }

            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // CACHE İŞLEMLERİ
    /*
    public void addMessageToCache(UUID playerUUID, String playerName, String message, String serverName) {
        messageRepository.addMessageToCache(playerUUID, playerName, message, serverName);
    }

    public List<StoredMessage> getPlayerMessages(UUID playerUUID) {
        return messageRepository.getPlayerMessages(playerUUID);
    }

    public void removePlayerFromCache(UUID playerUUID) {
        messageRepository.removePlayerFromCache(playerUUID);
    }
    */

    // Report operations
    public int createReport(UUID reportedPlayer, String reportedPlayerName, UUID reporterPlayer,
                            String reporterPlayerName, String reason, String serverName) {
        // Cache'den o anki mesajları al
        List<StoredMessage> currentMessages = plugin.getMessageCache().getPlayerMessages(reportedPlayer);

        // Raporu mesajlarla birlikte oluştur
        return reportRepository.createReport(reportedPlayer, reportedPlayerName, reporterPlayer,
                reporterPlayerName, reason, serverName, currentMessages);
    }

    public List<Report> getPendingReports() {
        return reportRepository.getPendingReports();
    }

    // ID'ye göre rapor getir
    public Report getReportById(int reportId) {
        return reportRepository.getReportById(reportId);
    }



    // GÜNCELLENDİ 10-06-2025 - boolean döndürüyor
    public boolean updateReportStatus(int reportId, Report.Status status, UUID handledBy) {
        return reportRepository.updateReportStatus(reportId, status, handledBy);
    }

    // GÜNCELLENDİ 10-06-2025 - boolean döndürüyor
    public boolean deleteReport(int reportId) {
        return reportRepository.deleteReport(reportId);
    }

    public void closeDatabase() {
        connectionManager.close();
    }

    // Getter methods
    public DatabaseConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public MessageRepository getMessageRepository() {
        return messageRepository;
    }

    public ReportRepository getReportRepository() {
        return reportRepository;
    }
}