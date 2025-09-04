package com.xiaoyumc.chinesename;

import com.xiaoyumc.chinesename.card.CardManager;
import com.xiaoyumc.chinesename.config.ConfigManager;
import com.xiaoyumc.chinesename.db.NameStorage;
import com.xiaoyumc.chinesename.db.StorageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class CnCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd,
                             @NotNull String label, String[] args) {

        /* 无参数 -> help */
        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
            sendHelp(sender, 1);
            return true;
        }

        /* 帮助分页 */
        if (args.length == 2 && args[0].equalsIgnoreCase("help")) {
            try { sendHelp(sender, Integer.parseInt(args[1])); } catch (NumberFormatException ignored) {}
            return true;
        }

        /* /cn set <name> [-t] */
        if (args[0].equalsIgnoreCase("set")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c只能玩家使用");
                return true;
            }
            Player p = (Player) sender;
            if (!p.hasPermission("cn.set")) {
                sender.sendMessage("§c缺少 cn.set");
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage("§b用法：/cn set <名称> [-t]");
                return true;
            }
            String name = args[1].replace('&', '§');
            boolean temp = args.length > 2 && args[2].equalsIgnoreCase("-t");
            if (!temp) {
                NameStorage.setName(p.getUniqueId().toString(), name);
            }
            p.setDisplayName(name + ChatColor.RESET);
            if (ConfigManager.getSettings().getBoolean("tablist", true)) {
                p.setPlayerListName(name + ChatColor.RESET);
            }
            p.sendMessage("§e名字已设为 " + name);
            return true;
        }

        /* /cn setother <player> <name> [-t] */
        if (args[0].equalsIgnoreCase("setother")) {
            if (!sender.hasPermission("cn.setother")) {
                sender.sendMessage("§c缺少 cn.setother");
                return true;
            }
            if (args.length < 3) {
                sender.sendMessage("§b用法：/cn setother <玩家> <名称> [-t]");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§c玩家不在线");
                return true;
            }
            String name = args[2].replace('&', '§');
            boolean temp = args.length > 3 && args[3].equalsIgnoreCase("-t");
            if (!temp) {
                NameStorage.setName(target.getUniqueId().toString(), name);
            }
            target.setDisplayName(name + ChatColor.RESET);
            if (ConfigManager.getSettings().getBoolean("tablist", true)) {
                target.setPlayerListName(name + ChatColor.RESET);
            }
            sender.sendMessage("§e已给 " + target.getName() + " 设置名字 " + name);
            return true;
        }

        /* /cn reset <player> [-t] */
        if (args[0].equalsIgnoreCase("reset")) {
            if (!sender.hasPermission("cn.reset")) {
                sender.sendMessage("§c缺少 cn.reset");
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage("§b用法：/cn reset <玩家> [-t]");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§c玩家不在线");
                return true;
            }
            boolean temp = args.length > 2 && args[2].equalsIgnoreCase("-t");
            String real = target.getName();
            if (!temp) {
                NameStorage.setName(target.getUniqueId().toString(), real);
            }
            target.setDisplayName(real + ChatColor.RESET);
            if (ConfigManager.getSettings().getBoolean("tablist", true)) {
                target.setPlayerListName(real + ChatColor.RESET);
            }
            sender.sendMessage("§e已重置 " + real);
            return true;
        }

        /* /cn check <player> */
        if (args[0].equalsIgnoreCase("check")) {
            if (!sender.hasPermission("cn.check")) {
                sender.sendMessage("§c缺少 cn.check");
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage("§b用法：/cn check <玩家>");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§c玩家不在线");
                return true;
            }
            String dbName = NameStorage.getName(target.getUniqueId().toString());
            String dispName = target.getDisplayName();
            if (dbName != null) {
                sender.sendMessage("§b" + target.getName() + " 的中文名：" + dbName);
            } else {
                sender.sendMessage("§b" + target.getName() + " 暂无中文名，显示名：" + dispName);
            }
            return true;
        }

        /* /cn givecard <player> [amount] */
        if (args[0].equalsIgnoreCase("givecard")) {
            if (!sender.hasPermission("cn.givecard")) {
                sender.sendMessage("§c缺少 cn.givecard");
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage("§b用法：/cn givecard <玩家> [数量]");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§c玩家不在线");
                return true;
            }
            int amount = 1;
            if (args.length > 2) {
                try { amount = Integer.parseInt(args[2]); } catch (NumberFormatException ignored) {}
            }
            CardManager.giveCard(target, amount);
            sender.sendMessage("§e给予 " + target.getName() + " 改名卡* " + amount);
            return true;
        }

        /* /cn usecard <name> */
        if (args[0].equalsIgnoreCase("usecard")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c只能玩家使用");
                return true;
            }
            Player p = (Player) sender;
            if (!p.hasPermission("cn.usecard")) {
                sender.sendMessage("§c缺少 cn.usecard");
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage("§b用法：/cn usecard <名称>");
                return true;
            }
            if (!CardManager.takeCard(p, 1)) {
                sender.sendMessage("§c你没有改名卡");
                return true;
            }
            String name = args[1].replace('&', '§');
            NameStorage.setName(p.getUniqueId().toString(), name);
            p.setDisplayName(name + ChatColor.RESET);
            if (ConfigManager.getSettings().getBoolean("tablist", true)) {
                p.setPlayerListName(name + ChatColor.RESET);
            }
            p.sendMessage("§e使用改名卡，名字设为 " + name);
            return true;
        }

        /* /cn convert <type> <type> */
        if (args[0].equalsIgnoreCase("convert")) {
            if (!sender.hasPermission("cn.convert")) {
                sender.sendMessage("§c缺少 cn.convert");
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage("§b用法：/cn convert <sqlite|mysql|yaml>");
                return true;
            }
            String dst = args[1].toLowerCase();
            if (!java.util.List.of("sqlite", "mysql", "yaml").contains(dst)) {
                sender.sendMessage("§c类型只能是 sqlite / mysql / yaml");
                return true;
            }
            String src = StorageManager.getCurrent();
            if (src.equals(dst)) {
                sender.sendMessage("§e已经是该类型，无需转换");
                return true;
            }
            StorageManager.convertAsync(src, dst);
            sender.sendMessage("§e正在后台转换存储类型，请稍后…");
            return true;
        }

        sender.sendMessage("§d未知子命令，输入 /cn help");
        return true;
    }

    private void sendHelp(CommandSender sender, int page) {
        if (page == 1) {
            sender.sendMessage("§b========= 第1页 =========");
            sender.sendMessage("§a/cn set <名称> [-t]");
            sender.sendMessage("§a/cn setother <玩家> <名称> [-t]");
            sender.sendMessage("§a/cn reset <玩家> [-t]");
            sender.sendMessage("§a/cn check <玩家>");
            sender.sendMessage("§a/cn givecard <玩家> [数量]");
            sender.sendMessage("§a/cn usecard <名称>");
            sender.sendMessage("§b========= 共2页 =========");
        } else {
            sendHelp(sender, 1);
        }
    }
}