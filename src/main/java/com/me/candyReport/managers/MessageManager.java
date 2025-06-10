package com.me.candyReport.managers;

import com.me.candyReport.CandyReport;
import org.bukkit.ChatColor;

public class MessageManager {

    private final CandyReport plugin;

    public MessageManager(CandyReport plugin) {
        this.plugin = plugin;
    }

    public String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String getMessage(String key) {
        return colorize(plugin.getConfigManager().getMessage(key));
    }

    public String getMessage(String key, String... placeholders) {
        return colorize(plugin.getConfigManager().getMessage(key, placeholders));
    }
}