package com.me.candyReport.gui.guis;

import com.me.candyReport.CandyReport;
import com.me.candyReport.gui.BaseGui;
import com.me.candyReport.gui.utils.ItemBuilder;
import com.me.candyReport.models.Report;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ReportsGui extends BaseGui {

    private final CandyReport plugin;
    private final List<Report> reports;

    public ReportsGui(Player player, CandyReport plugin) {
        super(player, plugin.getMessageManager().colorize(plugin.getConfigManager().getGUITitle()),
                plugin.getConfigManager().getGUISize());
        this.plugin = plugin;
        this.reports = plugin.getDatabaseManager().getPendingReports();
    }

    @Override
    protected void setupItems() {
        // Raporları listele
        for (int i = 0; i < Math.min(reports.size(), size - 9); i++) {
            Report report = reports.get(i);
            ItemStack reportItem = createReportItem(report);

            setItem(i, reportItem, event -> {
                // Rapor detaylarını aç - güvenli geçiş için switchGui kullan
                plugin.getGuiManager().switchGui(player, new ReportDetailGui(player, plugin, report));
            });
        }

        // Alt kısma butonlar ekle
        setupBottomButtons();
    }

    private void setupBottomButtons() {
        // Yenile butonu
        ItemStack refreshItem = new ItemBuilder(Material.EMERALD)
                .name("&aYenile")
                .lore("&7Raporları yenile")
                .build();

        setItem(size - 5, refreshItem, event -> {
            // GUI'yi yenile - güvenli geçiş için switchGui kullan
            plugin.getGuiManager().switchGui(player, new ReportsGui(player, plugin));
        });

        // Kapat butonu
        ItemStack closeItem = new ItemBuilder(Material.BARRIER)
                .name("&cKapat")
                .lore("&7GUI'yi kapat")
                .build();

        setItem(size - 1, closeItem, event -> {
            plugin.getGuiManager().closeGui(player);
        });
    }

    private ItemStack createReportItem(Report report) {
        List<String> lore = new ArrayList<>();
        lore.add("&cŞüpheli Kişi: &e" + report.getReportedPlayerName());
        lore.add("&7Raporlayan: &f" + report.getReporterPlayerName());
        lore.add("&7Sebep: &f" + report.getReason());
        lore.add("&7Sunucu: &f" + report.getServerName());
        lore.add("&7Tarih: &f" + report.getCreatedAt());
        lore.add("");
        lore.add("&aDetayları görmek için tıklayın!");

        return new ItemBuilder(Material.PAPER)
                .name("&6Rapor #" + report.getId())
                .lore(lore)
                .build();
    }
}