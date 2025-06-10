package com.me.candyReport.listeners;

import com.me.candyReport.managers.NotificationManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class NetworkListener implements PluginMessageListener {

    private final NotificationManager notificationManager;

    public NetworkListener(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("candyreport:notification")) {
            return;
        }

        try (ByteArrayInputStream b = new ByteArrayInputStream(message);
             DataInputStream in = new DataInputStream(b)) {

            String reportedPlayer = in.readUTF();
            String reporter = in.readUTF();
            String reason = in.readUTF();

            if (reason.isEmpty()) {
                reason = null;
            }

            notificationManager.notifyStaff(reportedPlayer, reporter, reason);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
