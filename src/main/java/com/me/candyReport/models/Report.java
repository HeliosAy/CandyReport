package com.me.candyReport.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Report {

    // Mevcut field'lar...
    private int id;
    private UUID reportedPlayer;
    private String reportedPlayerName;
    private UUID reporterPlayer;
    private String reporterPlayerName;
    private String reason;
    private String serverName;
    private Status status;
    private LocalDateTime createdAt;
    private UUID handledBy;
    private LocalDateTime handledAt;

    // Rapor anındaki mesajlar
    private List<StoredMessage> reportTimeMessages;

    public enum Status {
        PENDING, APPROVED, REJECTED
    }

    public Report(int id, UUID reportedPlayer, String reportedPlayerName,
                  UUID reporterPlayer, String reporterPlayerName, String reason,
                  String serverName, Status status, LocalDateTime createdAt,
                  UUID handledBy, LocalDateTime handledAt) {
        this.id = id;
        this.reportedPlayer = reportedPlayer;
        this.reportedPlayerName = reportedPlayerName;
        this.reporterPlayer = reporterPlayer;
        this.reporterPlayerName = reporterPlayerName;
        this.reason = reason;
        this.serverName = serverName;
        this.status = status;
        this.createdAt = createdAt;
        this.handledBy = handledBy;
        this.handledAt = handledAt;
    }

    // Rapor mesajları için
    public List<StoredMessage> getReportTimeMessages() {
        return reportTimeMessages;
    }

    public void setReportTimeMessages(List<StoredMessage> reportTimeMessages) {
        this.reportTimeMessages = reportTimeMessages;
    }

    // Mevcut getter metodları...
    public int getId() {
        return id;
    }

    public UUID getReportedPlayer() {
        return reportedPlayer;
    }

    public String getReportedPlayerName() {
        return reportedPlayerName;
    }

    public UUID getReporterPlayer() {
        return reporterPlayer;
    }

    public String getReporterPlayerName() {
        return reporterPlayerName;
    }

    public String getReason() {
        return reason;
    }

    public String getServerName() {
        return serverName;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public UUID getHandledBy() {
        return handledBy;
    }

    public LocalDateTime getHandledAt() {
        return handledAt;
    }

    // Setter metodları (gerekirse şu anlık dursun)
    public void setStatus(Status status) {
        this.status = status;
    }

    public void setHandledBy(UUID handledBy) {
        this.handledBy = handledBy;
    }

    public void setHandledAt(LocalDateTime handledAt) {
        this.handledAt = handledAt;
    }
}