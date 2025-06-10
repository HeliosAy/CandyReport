package com.me.candyReport.listeners;

import com.me.candyReport.CandyReport;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ChatListener implements Listener {

    private final CandyReport plugin;

    public ChatListener(CandyReport plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        // Mesajı önce cache'e kaydet
        plugin.getMessageCache().addMessage(
                player.getUniqueId(),
                player.getName(),
                message,
                getServerName()
        );
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Cache'den temizle (5 dakika sonra - eğer hemen geri gelirse cache'de olsun)
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            if (!player.isOnline()) {
                plugin.getMessageCache().removePlayerFromCache(player.getUniqueId());
            }
        }, 6000L); // 5 dakika
    }

    private String getServerName() {
        return plugin.getConfig().getString("server-name", "unknown");
    }
}