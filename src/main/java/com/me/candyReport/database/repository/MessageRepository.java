package com.me.candyReport.database.repository;

import com.me.candyReport.CandyReport;
import com.me.candyReport.models.StoredMessage;

import java.util.List;
import java.util.UUID;

public class MessageRepository {

    private final CandyReport plugin;

    public MessageRepository(CandyReport plugin) {
        this.plugin = plugin;
    }

    public List<StoredMessage> getPlayerMessages(UUID playerUUID) {
        // Sadece cache'den al
        return plugin.getMessageCache().getPlayerMessages(playerUUID);
    }

    public void addMessageToCache(UUID playerUUID, String playerName, String message, String serverName) {
        plugin.getMessageCache().addMessage(playerUUID, playerName, message, serverName);
    }

    public void removePlayerFromCache(UUID playerUUID) {
        plugin.getMessageCache().removePlayerFromCache(playerUUID);
    }

}