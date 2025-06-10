package com.me.candyReport.managers;

import com.me.candyReport.CandyReport;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class ReportManager {

    private final CandyReport plugin;

    public ReportManager(CandyReport plugin) {
        this.plugin = plugin;
    }

    public void createReport(Player reporter, Player reported, String reason) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            // Raporu databaseye kaydet
            int reportId = plugin.getDatabaseManager().createReport(
                    reported.getUniqueId(),
                    reported.getName(),
                    reporter.getUniqueId(),
                    reporter.getName(),
                    reason,
                    getServerName()
            );

            if (reportId != -1) {
                Bukkit.getScheduler().runTask(plugin, () -> {


                    reporter.sendMessage(plugin.getMessageManager().getMessage("report-sent",
                            "{player}", reported.getName()));

                    // Sunucu genelindeki yetkiliyi bilgilendir
                    plugin.getNotificationManager().notifyStaff(reported.getName(), reporter.getName(), reason);
                });
            } else {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    reporter.sendMessage(plugin.getMessageManager().getMessage("report-failed"));
                });
            }
        });
    }

    private String getServerName() {
        return plugin.getConfig().getString("server-name", "unknown");
    }
}