package com.xiaoyumc.chinesename.config;

import com.xiaoyumc.chinesename.ChineseName;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public final class ConfigManager {
    private static YamlConfiguration config;
    private static YamlConfiguration settings;
    private static YamlConfiguration messages;   // +1

    public static void load(ChineseName plugin) {
        plugin.saveDefaultConfig();
        plugin.saveResource("settings.yml", false);
        plugin.saveResource("messages.yml", false);  // 保证文件存在

        config     = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml"));
        settings   = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "settings.yml"));
        messages   = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.yml")); // +1
    }

    public static YamlConfiguration getConfig()    { return config; }
    public static YamlConfiguration getSettings()  { return settings; }
    public static YamlConfiguration getMessages()  { return messages; } // +1
}