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
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public final class CnCommand implements CommandExecutor, TabCompleter {

    /* ---------------- 工具 ---------------- */

    private boolean checkLength(CommandSender sender, String name) {
        int max = ConfigManager.getSettings().getInt("max-length", 16);
        if (name.length() <= max) return true;
        sender.sendMessage("§c名字超过最大长度 " + max + " 字符！");
        return false;
    }

    private boolean checkRegex(CommandSender sender, String name) {
        String pattern = ConfigManager.getSettings().getString("regex", "");
        if (pattern.isEmpty()) return true;
        if (sender.hasPermission("cn.bypass.regex")) return true;
        if (name.matches(pattern)) return true;
        sender.sendMessage("§c名字不符合规则！");
        return false;
    }

    /* ---------------- 主命令 ---------------- */

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
            Player p;
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c只能玩家使用");
                return true;
            } else p = (Player) sender;
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

            if (!p.hasPermission("cn.bypass.maxlength") && !checkLength(p, name)) return true;
            if (!p.hasPermission("cn.bypass.regex") && !checkRegex(p, name)) return true;

            if (!temp) StorageManager.setName(p.getUniqueId().toString(), name);
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

            if (!sender.hasPermission("cn.bypass.maxlength") && !checkLength(sender, name)) return true;
            if (!sender.hasPermission("cn.bypass.regex") && !checkRegex(sender, name)) return true;

            if (!temp) StorageManager.setName(target.getUniqueId().toString(), name);
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

            if (!temp) StorageManager.setName(target.getUniqueId().toString(), real);
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
            String dbName = StorageManager.getName(target.getUniqueId().toString());
            String dispName = target.getDisplayName();
            sender.sendMessage("§b" + target.getName() + " 的中文名：" + (dbName == null ? "无" : dbName));
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
            Player p;
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c只能玩家使用");
                return true;
            } else p = (Player) sender;
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

            if (!p.hasPermission("cn.bypass.maxlength") && !checkLength(p, name)) return true;
            if (!p.hasPermission("cn.bypass.regex") && !checkRegex(p, name)) return true;

            StorageManager.setName(p.getUniqueId().toString(), name);
            p.setDisplayName(name + ChatColor.RESET);
            if (ConfigManager.getSettings().getBoolean("tablist", true)) {
                p.setPlayerListName(name + ChatColor.RESET);
            }
            p.sendMessage("§e使用改名卡，名字设为 " + name);
            return true;
        }

        /* /cn convert <type> */
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

    /* ---------------- 翻页帮助 ---------------- */
    private void sendHelp(CommandSender sender, int page) {
        List<String> lines = Arrays.asList(
                "§a/cn set <名称> [-t]           §7- 设置自己的中文名",
                "§a/cn setother <玩家> <名称> [-t] §7- 设置他人中文名",
                "§a/cn reset <玩家> [-t]         §7- 重置某人的中文名",
                "§a/cn check <玩家>              §7- 查看某人的中文名",
                "§a/cn givecard <玩家> [数量]    §7- 给予改名卡",
                "§a/cn usecard <名称>            §7- 使用改名卡",
                "§a/cn convert <sqlite|mysql|yaml> §7- 转换存储类型",
                "§a/cn help <页码>               §7- 查看帮助"
        );
        int perPage = 5;
        int pages = (int) Math.ceil(lines.size() / (double) perPage);
        if (page < 1 || page > pages) page = 1;
        sender.sendMessage("§b========= 第 " + page + "/" + pages + " 页 =========");
        for (int i = (page - 1) * perPage; i < Math.min(page * perPage, lines.size()); i++) {
            sender.sendMessage(lines.get(i));
        }
    }

    /* ---------------- 命令建议 ---------------- */
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command cmd,
                                                @NotNull String alias,
                                                String[] args) {
        if (args.length == 1) {
            List<String> base = Arrays.asList("set", "setother", "reset", "check", "givecard", "usecard", "convert", "help");
            // 只返回有权限且前缀匹配的
            return base.stream()
                    .filter(s -> s.startsWith(args[0]) && sender.hasPermission("cn." + s))
                    .collect(Collectors.toList());
        }
        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "setother":
                case "reset":
                case "check":
                case "givecard":
                    // 玩家名由 Bukkit 自动补全，返回 null
                    return null;
                case "convert":
                    return Arrays.asList("sqlite", "mysql", "yaml")
                            .stream()
                            .filter(s -> s.startsWith(args[1]))
                            .collect(Collectors.toList());
                default:
                    break;
            }
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("setother")) {
            return Arrays.asList("-t");
        }
        return Collections.emptyList();
    }
}