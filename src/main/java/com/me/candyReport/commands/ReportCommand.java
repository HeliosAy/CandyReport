package com.me.candyReport.commands;

import com.me.candyReport.CandyReport;
import com.me.candyReport.gui.guis.ReportsGui;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReportCommand implements CommandExecutor, TabCompleter {

    private final CandyReport plugin;

    public ReportCommand(CandyReport plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length > 0 && args[0].equalsIgnoreCase("view")) {
                sender.sendMessage(plugin.getMessageManager().colorize("&cConsole cannot open GUI!"));
                return true;
            }
            sender.sendMessage(plugin.getMessageManager().colorize("&cThis command can only be used by players!"));
            return true;
        }

        Player player = (Player) sender;

        // Farklı altkomutlar
        if (args.length == 0) {
            sendUsage(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "view":
                handleViewCommand(player);
                break;
            case "reload":
                handleReloadCommand(player);
                break;
            default:
                handleReportCommand(player, args);
                break;
        }

        return true;
    }

    private void handleViewCommand(Player player) {
        if (!player.hasPermission(plugin.getConfigManager().getViewPermission())) {
            player.sendMessage(plugin.getMessageManager().getMessage("no-permission"));
            return;
        }

        plugin.getGuiManager().openGui(player, new ReportsGui(player, plugin));
    }

    private void handleReloadCommand(Player player) {
        if (!player.hasPermission("candyreport.admin")) {
            player.sendMessage(plugin.getMessageManager().getMessage("no-permission"));
            return;
        }

        plugin.getConfigManager().reloadConfigs();
        player.sendMessage(plugin.getMessageManager().getMessage("config-reloaded"));
    }

    private void handleReportCommand(Player player, String[] args) {
        if (!player.hasPermission(plugin.getConfigManager().getReportPermission())) {
            player.sendMessage(plugin.getMessageManager().getMessage("no-permission"));
            return;
        }

        if (args.length < 1) {
            sendUsage(player);
            return;
        }

        String targetName = args[0];
        Player targetPlayer = Bukkit.getPlayer(targetName);


        if (targetPlayer.equals(player)) {
            player.sendMessage(plugin.getMessageManager().getMessage("cannot-report-self"));
            return;
        }

        String reason = "-";
        if (args.length > 1) {
            // İkinci argümanın geçerli bir sebep olup olmadığını kontrol edin
            List<String> validReasons = plugin.getConfigManager().getReportReasons();
            if (validReasons.contains(args[1])) {
                reason = args[1];
            } else {
                // Tüm argümanları özel sebep olarak birleştir
                reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            }
        }

        // Rapor oluştur
        plugin.getReportManager().createReport(player, targetPlayer, reason);
    }

    private void sendUsage(Player player) {
        player.sendMessage(plugin.getMessageManager().colorize("&e&lCandyReport Commands:"));
        player.sendMessage(plugin.getMessageManager().colorize("&7/rapor <player> [reason] &f- Report a player"));

        if (player.hasPermission(plugin.getConfigManager().getViewPermission())) {
            player.sendMessage(plugin.getMessageManager().colorize("&7/rapor view &f- View pending reports"));
        }

        if (player.hasPermission("candyreport.admin")) {
            player.sendMessage(plugin.getMessageManager().colorize("&7/rapor reload &f- Reload configuration"));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // Birinci argüman - subcommands || oyuncu adları
            List<String> subCommands = new ArrayList<>();

            if (sender.hasPermission(plugin.getConfigManager().getViewPermission())) {
                subCommands.add("view");
            }

            if (sender.hasPermission("candyreport.admin")) {
                subCommands.add("reload");
            }

            // Aktif oyuncu adlarını ekle
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.equals(sender)) {
                    subCommands.add(player.getName());
                }
            }

            StringUtil.copyPartialMatches(args[0], subCommands, completions);
        } else if (args.length == 2 && !args[0].equalsIgnoreCase("view") && !args[0].equalsIgnoreCase("reload")) {
            // İkinci argüman - Sebepler (sadece ilk argüman bir oyuncu adıysa)
            Player targetPlayer = Bukkit.getPlayer(args[0]);
            if (targetPlayer != null) {
                List<String> reasons = plugin.getConfigManager().getReportReasons();
                StringUtil.copyPartialMatches(args[1], reasons, completions);
            }
        }

        return completions;
    }
}