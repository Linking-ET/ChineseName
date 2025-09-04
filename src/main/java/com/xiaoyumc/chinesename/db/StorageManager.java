package com.xiaoyumc.chinesename.db;

import com.xiaoyumc.chinesename.ChineseName;
import com.xiaoyumc.chinesename.config.ConfigManager;
import org.bukkit.Bukkit;

import java.util.Set;
import java.util.UUID;

public final class StorageManager {

    private static String current = ConfigManager.getSettings().getString("storage-type", "yaml").toLowerCase();

    public static String getCurrent() { return current; }

    public static String getName(String uuid) {
        switch (current) {
            case "mysql":
            case "sqlite":
                return NameStorage.getName(uuid);
            case "yaml":
                return YamlStorage.getName(uuid);
            default:
                return null;
        }
    }

    public static void setName(String uuid, String name) {
        // 1. 先写主存储
        switch (current) {
            case "mysql":
            case "sqlite":
                NameStorage.setName(uuid, name);
                break;
            case "yaml":
                YamlStorage.setName(uuid, name);
                break;
        }

        boolean saveToConfig = ConfigManager.getSettings().getBoolean("save-to-config", false);
        if (saveToConfig) {
            ConfigManager.getConfig().set(uuid, name);
        }
        YamlStorage.cfg.set(uuid, name);
    }

    /* 异步转换：src → dst */
    public static void convertAsync(String src, String dst) {
        Bukkit.getScheduler().runTaskAsynchronously(ChineseName.getInstance(), () -> {
            try {
                Set<String> keys = src.equals("yaml") ? YamlStorage.cfg.getKeys(false) : NameStorage.getAllUUIDs();
                for (String uuidStr : keys) {
                    String name = src.equals("yaml") ? YamlStorage.getName(uuidStr) : NameStorage.getName(uuidStr);
                    if (name == null) continue;
                    if (dst.equals("yaml"))
                        YamlStorage.setName(uuidStr, name);
                    else
                        NameStorage.setName(uuidStr, name);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}