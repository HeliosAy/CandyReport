package com.me.candyReport.notifier;

import com.me.candyReport.managers.ConfigManager;

public class NotificationManager {

    private final LocalNotifier localNotifier;
    private final NetworkNotifier networkNotifier;
    private final ConfigManager configManager;

    public NotificationManager(LocalNotifier localNotifier, NetworkNotifier networkNotifier, ConfigManager configManager) {
        this.localNotifier = localNotifier;
        this.networkNotifier = networkNotifier;
        this.configManager = configManager;
    }

    /**
     * Rapor oluşturulduğunda çağrılır
     * Config'e göre BungeeCord veya Local bildirim gönderir
     */
    public void notifyStaff(String reportedPlayer, String reporter, String reason) {
        if (configManager.isBungeeCordEnabled()) {
            // BungeeCord aktifse sadece network'e gönder
            networkNotifier.notify(reportedPlayer, reporter, reason);
        } else {
            // BungeeCord kapalıysa sadece local bildirim
            localNotifier.notify(reportedPlayer, reporter, reason);
        }
    }

    /**
     * BungeeCord'dan gelen mesajlar için - SADECE local bildirim gönderir
     */
    public void notifyLocalStaff(String reportedPlayer, String reporter, String reason) {
        localNotifier.notify(reportedPlayer, reporter, reason);
    }
}