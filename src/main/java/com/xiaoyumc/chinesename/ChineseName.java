package com.xiaoyumc.chinesename;

import com.xiaoyumc.chinesename.card.CardManager;
import com.xiaoyumc.chinesename.config.ConfigManager;
import com.xiaoyumc.chinesename.db.DatabaseManager;
import com.xiaoyumc.chinesename.db.NameStorage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class ChineseName extends JavaPlugin implements Listener {
    private static ChineseName instance;

    public static ChineseName getInstance() { return instance; }

    @Override
    public void onEnable() {
        instance = this;
        ConfigManager.load(this);
        DatabaseManager.init(this);
        getCommand("cn").setExecutor(new CnCommand());
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new CardManager(), this);
        Bukkit.getConsoleSender().sendMessage("§b[ChineseName] 已加载");
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new ChineseExpansion().register();
            Bukkit.getConsoleSender().sendMessage("§b[ChineseName] 已注册PAPI变量");
        }
    }

    @Override
    public void onDisable() {
        DatabaseManager.shutdown();
        PluginCommand cmd = getCommand("cn");
        if (cmd != null) cmd.setExecutor(null);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        String name = NameStorage.getName(e.getPlayer().getUniqueId().toString());
        if (name != null) {
            e.getPlayer().setDisplayName(name + ChatColor.RESET);
            e.getPlayer().setPlayerListName(name + ChatColor.RESET);
        }
    }
}