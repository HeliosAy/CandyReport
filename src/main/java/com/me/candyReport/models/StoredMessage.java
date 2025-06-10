package com.me.candyReport.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class StoredMessage {

    private final int id;
    private final UUID playerUUID;
    private final String playerName;
    private final String message;
    private final String serverName;
    private final LocalDateTime timestamp;

    public StoredMessage(int id, UUID playerUUID, String playerName, String message,
                         String serverName, LocalDateTime timestamp) {
        this.id = id;
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.message = message;
        this.serverName = serverName;
        this.timestamp = timestamp;
    }



    public int getId() { return id; }
    public UUID getPlayerUUID() { return playerUUID; }
    public String getPlayerName() { return playerName; }
    public String getMessage() { return message; }
    public String getServerName() { return serverName; }
    public LocalDateTime getTimestamp() { return timestamp; }
}