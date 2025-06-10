package com.me.candyReport.notifier;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class NetworkNotifier {

    private final Plugin plugin;

    public NetworkNotifier(Plugin plugin) {
        this.plugin = plugin;
    }

    public void notify(String reportedPlayer, String reporter, String reason) {
        if (Bukkit.getOnlinePlayers().isEmpty()) return;

        Player anyPlayer = Bukkit.getOnlinePlayers().iterator().next();

        try (ByteArrayOutputStream b = new ByteArrayOutputStream();
             DataOutputStream out = new DataOutputStream(b)) {

            out.writeUTF("Forward");
            out.writeUTF("ALL");
            out.writeUTF("candyreport:notification");

            try (ByteArrayOutputStream msgBytes = new ByteArrayOutputStream();
                 DataOutputStream msgOut = new DataOutputStream(msgBytes)) {

                msgOut.writeUTF(reportedPlayer);
                msgOut.writeUTF(reporter);
                msgOut.writeUTF(reason != null ? reason : "");

                out.writeShort(msgBytes.toByteArray().length);
                out.write(msgBytes.toByteArray());
            }

            anyPlayer.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());

        } catch (IOException e) {
            plugin.getLogger().severe("Failed to send network notification: " + e.getMessage());
        }
    }
}
