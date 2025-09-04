package com.xiaoyumc.chinesename.config;

import com.xiaoyumc.chinesename.ChineseName;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public final class ConfigManager {
    private static YamlConfiguration config;
    private static YamlConfiguration settings;
    private static YamlConfiguration messages;

    public static void load(ChineseName plugin) {
        plugin.saveDefaultConfig();
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try { configFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        } //只有config.yml才有可能未被创建且未预定义
        plugin.saveResource("settings.yml", false);
        plugin.saveResource("messages.yml", false); //此处两个文件都已经在resource中被定义
        // nick.yml此处不进行处理
        config     = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml"));
        settings   = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "settings.yml"));
        messages   = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "messages.yml"));
    }

    public static YamlConfiguration getConfig()    { return config; }
    public static YamlConfiguration getSettings()  { return settings; }
    public static YamlConfiguration getMessages()  { return messages; }
}