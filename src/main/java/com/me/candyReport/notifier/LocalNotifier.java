package com.me.candyReport.notifier;

import com.me.candyReport.managers.ConfigManager;
import com.me.candyReport.managers.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class LocalNotifier {

    private final MessageManager messageManager;
    private final ConfigManager configManager;
    private final Plugin plugin;

    public LocalNotifier(MessageManager messageManager, ConfigManager configManager, Plugin plugin) {
        this.messageManager = messageManager;
        this.configManager = configManager;
        this.plugin = plugin;
    }

    public void notify(String reportedPlayer, String reporter, String reason) {
        String message = messageManager.getMessage("new-report-notification",
                "{reported}", reportedPlayer,
                "{reporter}", reporter,
                "{reason}", reason != null ? reason : "No reason provided");

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(configManager.getViewPermission())) {
                player.sendMessage(message);

                if (configManager.isNotificationSoundEnabled()) {
                    try {
                        Sound sound = Sound.valueOf(configManager.getNotificationSound());
                        player.playSound(player.getLocation(), sound,
                                configManager.getNotificationVolume(),
                                configManager.getNotificationPitch());
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Invalid sound: " + configManager.getNotificationSound());
                    }
                }
            }
        }
    }
}
