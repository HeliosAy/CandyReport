package com.me.candyReport.gui.guis;

import com.me.candyReport.CandyReport;
import com.me.candyReport.gui.BaseGui;
import com.me.candyReport.gui.utils.ItemBuilder;
import com.me.candyReport.models.Report;
import com.me.candyReport.models.StoredMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class ReportDetailGui extends BaseGui {

    private final CandyReport plugin;
    private final Report report;

    public ReportDetailGui(Player player, CandyReport plugin, Report report) {
        super(player, "Rapor Detayları #" + report.getId(), 27);
        this.plugin = plugin;
        this.report = report;
    }

    @Override
    protected void setupItems() {
        // Rapor bilgileri
        setupReportInfo();

        // Mesajları göster
        setupMessages();

        // Aksiyon butonları
        setupActionButtons();

        // Geri dön butonu
        setupBackButton();
    }

    private void setupReportInfo() {
        List<String> lore = new ArrayList<>();
        lore.add("§7ID: §f" + report.getId());
        lore.add("§7Raporlayan: §f" + report.getReporterPlayerName());
        lore.add("§7Sebep: §f" + report.getReason());
        lore.add("§7Sunucu: §f" + report.getServerName());
        lore.add("§7Durum: §f" + report.getStatus());
        lore.add("§7Tarih: §f" + report.getCreatedAt());

        // Oyuncu kafası oluşturuluyor
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§cŞüpheli: §e"+report.getReportedPlayerName());
            meta.setLore(lore);
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(report.getReportedPlayerName()));
            skull.setItemMeta(meta);
        }

        setItem(4, skull);
    }


    private void setupMessages() {
        if (report.getReportTimeMessages() != null && !report.getReportTimeMessages().isEmpty()) {
            List<String> messageLore = new ArrayList<>();
            messageLore.add("&7Rapor anında " + report.getReportTimeMessages().size() + " mesaj kaydedildi");
            messageLore.add("");
            messageLore.add("&e&lTıkla: &7Mesajları sohbette görüntüle");

            ItemStack messagesItem = new ItemBuilder(Material.WRITABLE_BOOK)
                    .name("&eMesajlar (" + report.getReportTimeMessages().size() + ")")
                    .lore(messageLore)
                    .build();

            setItem(13, messagesItem, event -> {
                // Mesajları chatte göster
                showMessagesInChat();
            });
        } else {
            // Mesaj yoksa
            ItemStack noMessagesItem = new ItemBuilder(Material.BARRIER)
                    .name("&cMesaj Yok")
                    .lore("&7Bu rapor için kaydedilmiş mesaj bulunmuyor.")
                    .build();

            setItem(13, noMessagesItem);
        }
    }

    private void showMessagesInChat() {
        // Mesajları chatte göster
        player.sendMessage("");
        player.sendMessage("§6§lRAPOR MESAJLARI");
        player.sendMessage("§7Rapor ID: §f#" + report.getId());
        player.sendMessage("§7Raporlanan: §c" + report.getReportedPlayerName());
        player.sendMessage("");

        if (report.getReportTimeMessages() != null && !report.getReportTimeMessages().isEmpty()) {
            for (int i = 0; i < report.getReportTimeMessages().size(); i++) {
                StoredMessage msg = report.getReportTimeMessages().get(i);
                player.sendMessage("§f" + (i + 1) + ". §e" + msg.getPlayerName() + "§7: §f" + msg.getMessage());
            }
        } else {
            player.sendMessage("§cKaydedilmiş mesaj bulunmuyor.");
        }

        player.sendMessage("");
        player.sendMessage("§8§l=====================================");
        player.sendMessage("");
    }

    private void setupActionButtons() {
        // Onayla butonu
        ItemStack approveItem = new ItemBuilder(Material.LIME_WOOL)
                .name("&aOnayla")
                .lore("&7Bu raporu onayla ve işlem yap")
                .build();

        setItem(11, approveItem, event -> {

            Report statusValue = plugin.getDatabaseManager().getReportById(report.getId());
            if (statusValue == null){
                plugin.getGuiManager().switchGui(player, new ReportsGui(player, plugin));
                player.sendMessage("§cBu raporu başka bir yetkili sizden önce onaylamış!");
                return;
            }


            Boolean success = plugin.getDatabaseManager().deleteReport(report.getId());

            if (success) {
                player.sendMessage(plugin.getMessageManager().getMessage("report-approved","{player}",report.getReportedPlayerName()));

                // Mute komutunu çalıştır
                String muteCommand = plugin.getConfigManager().getDefaultMuteCommand()
                        .replace("{player}", report.getReportedPlayerName());
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), muteCommand);

                // GUI'yi kapat ve ana listeye dön
                plugin.getGuiManager().switchGui(player, new ReportsGui(player, plugin));



            } else {
                player.sendMessage(plugin.getMessageManager().getMessage("database-error"));
            }

        });

        // Reddet butonu
        ItemStack rejectItem = new ItemBuilder(Material.RED_WOOL)
                .name("&cReddet")
                .lore("&7Bu raporu reddet")
                .build();

        setItem(15, rejectItem, event -> {

            Report statusValue = plugin.getDatabaseManager().getReportById(report.getId());
            if (statusValue == null){
                plugin.getGuiManager().switchGui(player, new ReportsGui(player, plugin));
                player.sendMessage("§cBu raporu başka bir yetkili sizden önce onaylamış!");
                return;
            }

            Boolean success = plugin.getDatabaseManager().deleteReport(report.getId());

            if (success) {
                player.sendMessage(plugin.getMessageManager().getMessage("report-rejected",
                        report.getReportedPlayerName()));

                // GUI'yi kapat ve ana listeye dön
                plugin.getGuiManager().switchGui(player, new ReportsGui(player, plugin));
            } else {
                player.sendMessage(plugin.getMessageManager().getMessage("database-error"));
            }
        });
    }

    private void setupBackButton() {
        ItemStack backItem = new ItemBuilder(Material.ARROW)
                .name("&7← Geri Dön")
                .lore("&7Rapor listesine dön")
                .build();

        setItem(18, backItem, event -> {
            // GUI geçişi için switchGui kullan
            plugin.getGuiManager().switchGui(player, new ReportsGui(player, plugin));
        });
    }
}