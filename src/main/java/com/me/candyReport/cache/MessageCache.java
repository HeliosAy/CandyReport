package com.me.candyReport.cache;

import com.me.candyReport.CandyReport;
import com.me.candyReport.models.StoredMessage;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MessageCache {

    private final CandyReport plugin;
    private final Map<UUID, LinkedList<StoredMessage>> playerMessages = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public MessageCache(CandyReport plugin) {
        this.plugin = plugin;
        startPeriodicCleanup();
    }

    public void addMessage(UUID playerUUID, String playerName, String message, String serverName) {
        StoredMessage storedMessage = new StoredMessage(
                0,
                playerUUID,
                playerName,
                message,
                serverName,
                LocalDateTime.now()
        );

        playerMessages.computeIfAbsent(playerUUID, k -> new LinkedList<>()).add(storedMessage);

        // Maximum mesaj sayısını kontrol et
        LinkedList<StoredMessage> messages = playerMessages.get(playerUUID);
        int maxMessages = plugin.getConfigManager().getMaxMessagesToStore();

        while (messages.size() > maxMessages) {
            messages.removeFirst();
        }
    }

    public List<StoredMessage> getPlayerMessages(UUID playerUUID) {
        LinkedList<StoredMessage> messages = playerMessages.get(playerUUID);
        return messages != null ? new ArrayList<>(messages) : new ArrayList<>();
    }


    public void removePlayerFromCache(UUID playerUUID) {
        playerMessages.remove(playerUUID);
    }

    private void startPeriodicCleanup() {
        // Her 30 dakikada bir eski mesajları temizle
        scheduler.scheduleAtFixedRate(() -> {
            LocalDateTime cutoff = LocalDateTime.now().minusHours(2); // 2 saat önceki mesajları temizle

            playerMessages.entrySet().removeIf(entry -> {
                LinkedList<StoredMessage> messages = entry.getValue();
                messages.removeIf(msg -> msg.getTimestamp().isBefore(cutoff));
                return messages.isEmpty();
            });

        }, 30, 30, TimeUnit.MINUTES);
    }

}