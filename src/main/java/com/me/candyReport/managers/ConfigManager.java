package com.me.candyReport.managers;

import com.me.candyReport.CandyReport;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

public class ConfigManager {

    private final CandyReport plugin;
    private FileConfiguration config;
    private FileConfiguration messages;

    public ConfigManager(CandyReport plugin) {
        this.plugin = plugin;
        loadConfigs();
    }

    private void loadConfigs() {
        // Plugin yolunu oluştur
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        // config.yml dosyasını yükle
        loadConfig("config.yml");
        this.config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml"));

        // messages.yml dosyasını yükle
        loadConfig("messages.yml");
        this.messages = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.yml"));
    }

    private void loadConfig(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            try (InputStream inputStream = plugin.getResource(fileName)) {
                if (inputStream != null) {
                    Files.copy(inputStream, file.toPath());
                }
            } catch (IOException e) {
                plugin.getLogger().severe("Could not save " + fileName + ": " + e.getMessage());
            }
        }
    }


    public void reloadConfigs() {
        this.config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml"));
        this.messages = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.yml"));
    }

    // Config Database ayarları
    public String getDatabaseHost() {
        return config.getString("database.host", "localhost");
    }

    public int getDatabasePort() {
        return config.getInt("database.port", 3306);
    }

    public String getDatabaseName() {
        return config.getString("database.name", "candyreport");
    }

    public String getDatabaseUsername() {
        return config.getString("database.username", "root");
    }

    public String getDatabasePassword() {
        return config.getString("database.password", "");
    }

    public boolean isDatabaseSSL() {
        return config.getBoolean("database.ssl", false);
    }

    // Config Report Ayarları
    public int getMaxMessagesToStore() {
        return config.getInt("report.max-messages-to-store", 4);
    }

    public List<String> getReportReasons() {
        return config.getStringList("report.reasons");
    }

    public String getDefaultMuteCommand() {
        return config.getString("report.default-mute-command", "mute {player} 1d Inappropriate behavior");
    }

    public boolean isNotificationSoundEnabled() {
        return config.getBoolean("notification.sound.enabled", true);
    }

    public String getNotificationSound() {
        return config.getString("notification.sound.type", "BLOCK_NOTE_BLOCK_PLING");
    }

    public float getNotificationVolume() {
        return (float) config.getDouble("notification.sound.volume", 1.0);
    }

    public float getNotificationPitch() {
        return (float) config.getDouble("notification.sound.pitch", 1.0);
    }

    // GUI settings
    public String getGUITitle() {
        return config.getString("gui.title", "&cActive Reports");
    }

    public int getGUISize() {
        return config.getInt("gui.size", 54);
    }

    // Permission ayarları
    public String getReportPermission() {
        return config.getString("permissions.report", "candyreport.report");
    }

    public String getViewPermission() {
        return config.getString("permissions.view", "candyreport.view");
    }

    public String getManagePermission() {
        return config.getString("permissions.manage", "candyreport.manage");
    }

    // mesajları al
    public String getMessage(String key) {
        return messages.getString("messages." + key, "&cMessage not found: " + key);
    }

    public String getMessage(String key, String... placeholders) {
        String message = getMessage(key);
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                message = message.replace(placeholders[i], placeholders[i + 1]);
            }
        }
        return message;
    }


}