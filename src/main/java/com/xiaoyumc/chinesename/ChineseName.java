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
        try {
            Plugin nbt = getServer().getPluginManager().getPlugin("NBTAPI");
            if (nbt != null) {
                File jar = new File(nbt.getDataFolder().getParentFile(), "item-nbt-api-plugin-2.15.2.jar");
                if (jar.exists()) {
                    java.net.URLClassLoader cl = (java.net.URLClassLoader) getClassLoader();
                    java.lang.reflect.Method add = java.net.URLClassLoader.class
                            .getDeclaredMethod("addURL", java.net.URL.class);
                    add.setAccessible(true);
                    add.invoke(cl, jar.toURI().toURL());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ConfigManager.load(this);
        DatabaseManager.init(this);
        getCommand("cn").setExecutor(new CnCommand());
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new CardManager(), this);
        Bukkit.getConsoleSender().sendMessage("§b[ChineseName] 已加载");
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