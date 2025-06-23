package com.me.candyReport;

import com.me.candyReport.cache.MessageCache;
import com.me.candyReport.commands.ReportCommand;
import com.me.candyReport.database.DatabaseManager;
import com.me.candyReport.gui.GuiManager;
import com.me.candyReport.listeners.ChatListener;
import com.me.candyReport.listeners.NetworkListener;
import com.me.candyReport.managers.ConfigManager;
import com.me.candyReport.managers.MessageManager;
import com.me.candyReport.notifier.NotificationManager;
import com.me.candyReport.managers.ReportManager;
import com.me.candyReport.notifier.LocalNotifier;
import com.me.candyReport.notifier.NetworkNotifier;
import org.bukkit.plugin.java.JavaPlugin;

public class CandyReport extends JavaPlugin {

    private static CandyReport instance;
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private MessageManager messageManager;
    private ReportManager reportManager;
    private NotificationManager notificationManager;
    private LocalNotifier localNotifier;
    private NetworkNotifier networkNotifier;
    private MessageCache messageCache;
    private GuiManager guiManager;
    @Override
    public void onEnable() {
        instance = this;

        // Initialize managers
        this.configManager = new ConfigManager(this);
        this.messageManager = new MessageManager(this);
        this.databaseManager = new DatabaseManager(this);
        this.reportManager = new ReportManager(this);
        this.localNotifier = new LocalNotifier(messageManager, configManager, this);
        this.networkNotifier = new NetworkNotifier(this);
        this.messageCache = new MessageCache(this);
        this.guiManager = new GuiManager(this);
        // BUNGEECORD OR SINGLE SERVER MOD AYARLARI
        if (configManager.isBungeeCordEnabled()) {
            this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            this.getServer().getMessenger().registerIncomingPluginChannel(this, "candyreport:notification",
                    new NetworkListener(notificationManager));
            getLogger().info("BungeeCord integration enabled!");
        } else {
            getLogger().info("Running in single-server mode.");
        }

        // Initialize Database
        if (!databaseManager.initialize()) {
            getLogger().severe("Failed to initialize database! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Komutları Kaydet
        getCommand("rapor").setExecutor(new ReportCommand(this));

        // Register listeners
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);

        String ASCIIYELLOW = "\u001B[33m";
        String ASCIIGREEN = "\u001B[32m";
        String ASCIIRESET = "\u001B[0m";


        getLogger().info(ASCIIYELLOW + "   _____                _       _____                       _   "        + ASCIIRESET);
        getLogger().info(ASCIIYELLOW + "  / ____|              | |     |  __ \\                     | |  "       + ASCIIRESET);
        getLogger().info(ASCIIYELLOW + " | |     __ _ _ __   __| |_   _| |__) |___ _ __   ___  _ __| |_ "        + ASCIIRESET);
        getLogger().info(ASCIIYELLOW + " | |    / _` | '_ \\ / _` | | | |  _  // _ \\ '_ \\ / _ \\| '__| __|"    + ASCIIRESET);
        getLogger().info(ASCIIYELLOW + " | |___| (_| | | | | (_| | |_| | | \\ \\  __/ |_) | (_) | |  | |_ "      + ASCIIRESET);
        getLogger().info(ASCIIYELLOW + "  \\_____\\__,_|_| |_|\\__,_|\\__, |_|  \\_\\___| .__/ \\___/|_|   \\__|"+ ASCIIRESET);
        getLogger().info(ASCIIYELLOW + "                           __/ |          | |                   "        + ASCIIRESET);
        getLogger().info(ASCIIYELLOW + "                          |___/           |_|                   "        + ASCIIRESET);

        getLogger().info(ASCIIGREEN + "CandyReport plugin has been enabled!"      + ASCIIRESET);
        getLogger().info(ASCIIGREEN + "BungeeCord messaging channels registered!" + ASCIIRESET);

    }

    @Override
    public void onDisable() {
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        getServer().getMessenger().unregisterIncomingPluginChannel(this);

        if (databaseManager != null) {
            databaseManager.closeDatabase();
        }
        getLogger().info("CandyReport plugin has been disabled!");
    }

    public static CandyReport getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public ReportManager getReportManager() {
        return reportManager;
    }

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public MessageCache getMessageCache() {
        return messageCache;
    }
    public GuiManager getGuiManager() {
        return guiManager;
    }
}