package com.me.candyReport.database.repository;

import com.me.candyReport.CandyReport;
import com.me.candyReport.database.connection.DatabaseConnectionManager;
import com.me.candyReport.models.Report;
import com.me.candyReport.models.StoredMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReportRepository {

    private final CandyReport plugin;
    private final DatabaseConnectionManager connectionManager;
    private final Gson gson;

    public ReportRepository(CandyReport plugin, DatabaseConnectionManager connectionManager) {
        this.plugin = plugin;
        this.connectionManager = connectionManager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    public int createReport(UUID reportedPlayer, String reportedPlayerName, UUID reporterPlayer,
                            String reporterPlayerName, String reason, String serverName,
                            List<StoredMessage> reportTimeMessages) {

        try (Connection connection = connectionManager.getConnection()) {
            String messagesJson = gson.toJson(reportTimeMessages);

            String sql = "INSERT INTO candyreport_reports (reported_player, reported_player_name, reporter_player, reporter_player_name, reason, server_name, report_messages) VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, reportedPlayer.toString());
                stmt.setString(2, reportedPlayerName);
                stmt.setString(3, reporterPlayer.toString());
                stmt.setString(4, reporterPlayerName);
                stmt.setString(5, reason);
                stmt.setString(6, serverName);
                stmt.setString(7, messagesJson);

                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to create report: " + e.getMessage());
            return -1;
        }
        return -1;
    }

    public List<Report> getPendingReports() {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM candyreport_reports WHERE status = 'PENDING' ORDER BY created_at DESC";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Report report = createReportFromResultSet(rs);
                reports.add(report);
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to get pending reports: " + e.getMessage());
        }

        return reports;
    }

    // YENİ METOD - ID'ye göre rapor getir
    public Report getReportById(int reportId) {
        String sql = "SELECT * FROM candyreport_reports WHERE id = ?";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, reportId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createReportFromResultSet(rs);
                }
                return null;
            }

        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to get report by ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // GÜNCELLENDİ 10-06-2025 - boolean döndürüyor
    public boolean updateReportStatus(int reportId, Report.Status status, UUID handledBy) {
        String sql = "UPDATE candyreport_reports SET status = ?, handled_by = ?, handled_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            stmt.setString(2, handledBy.toString());
            stmt.setInt(3, reportId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to update report status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // GÜNCELLENDİ 10-06-2025 - boolean döndürüyor
    public boolean deleteReport(int reportId) {
        String sql = "DELETE FROM candyreport_reports WHERE id = ?";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, reportId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to delete report: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Report createReportFromResultSet(ResultSet rs) throws SQLException {
        // JSON'dan mesajları parse et
        List<StoredMessage> messages = new ArrayList<>();
        String messagesJson = rs.getString("report_messages");
        if (messagesJson != null && !messagesJson.isEmpty()) {
            try {
                Type listType = new TypeToken<List<StoredMessage>>(){}.getType();
                messages = gson.fromJson(messagesJson, listType);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to parse report messages JSON: " + e.getMessage());
            }
        }

        Report report = new Report(
                rs.getInt("id"),
                UUID.fromString(rs.getString("reported_player")),
                rs.getString("reported_player_name"),
                UUID.fromString(rs.getString("reporter_player")),
                rs.getString("reporter_player_name"),
                rs.getString("reason"),
                rs.getString("server_name"),
                Report.Status.valueOf(rs.getString("status")),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getString("handled_by") != null ? UUID.fromString(rs.getString("handled_by")) : null,
                rs.getTimestamp("handled_at") != null ? rs.getTimestamp("handled_at").toLocalDateTime() : null
        );

        report.setReportTimeMessages(messages);
        return report;
    }

    // LocalDateTime için custom TypeAdapter
    private static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public void write(JsonWriter out, LocalDateTime value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.format(formatter));
            }
        }

        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return LocalDateTime.parse(in.nextString(), formatter);
        }
    }
}