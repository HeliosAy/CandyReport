package com.me.candyReport.gui;

import com.me.candyReport.CandyReport;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuiManager implements Listener {

    private final CandyReport plugin;
    private final Map<UUID, BaseGui> openGuis = new HashMap<>();

    public GuiManager(CandyReport plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void openGui(Player player, BaseGui gui) {
        UUID playerId = player.getUniqueId();

        // Eski GUIyi temizle
        openGuis.remove(playerId);

        // Yeni GUI'yi kaydet
        openGuis.put(playerId, gui);

        // GUI'yi aç
        gui.open();
    }

    // GUI geçişi için özel method - async olarak çalışır
    public void switchGui(Player player, BaseGui newGui) {
        UUID playerId = player.getUniqueId();

        // Mevcut inventory'yi kapat
        player.closeInventory();

        // Kısa bir delay ile yeni GUI'yi aç
        new BukkitRunnable() {
            @Override
            public void run() {
                openGuis.put(playerId, newGui);
                newGui.open();
            }
        }.runTaskLater(plugin, 1L); // 1 tick delay
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        UUID playerId = player.getUniqueId();
        BaseGui gui = openGuis.get(playerId);

        // GUI var mı ve inventory eşleşiyor mu kontrol et
        if (gui != null && event.getInventory().equals(gui.getInventory())) {
            gui.handleClick(event);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            UUID playerId = player.getUniqueId();

            // GUI'yi Map'ten kaldır
            openGuis.remove(playerId);
        }
    }

    public void closeGui(Player player) {
        UUID playerId = player.getUniqueId();
        openGuis.remove(playerId);
        player.closeInventory();
    }
}