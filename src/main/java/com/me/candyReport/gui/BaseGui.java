package com.me.candyReport.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class BaseGui {

    protected final Player player;
    protected final String title;
    protected final int size;
    protected final Inventory inventory;
    protected final Map<Integer, Consumer<InventoryClickEvent>> clickActions = new HashMap<>();

    public BaseGui(Player player, String title, int size) {
        this.player = player;
        this.title = title;
        this.size = size;
        this.inventory = Bukkit.createInventory(null, size, title);
    }

    public void open() {
        setupItems();
        player.openInventory(inventory);
    }

    public void setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> clickAction) {
        inventory.setItem(slot, item);
        if (clickAction != null) {
            clickActions.put(slot, clickAction);
        }
    }

    public void setItem(int slot, ItemStack item) {
        setItem(slot, item, null);
    }

    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);

        Consumer<InventoryClickEvent> action = clickActions.get(event.getSlot());
        if (action != null) {
            action.accept(event);
        }
    }

    protected abstract void setupItems();

    public Inventory getInventory() {
        return inventory;
    }
}