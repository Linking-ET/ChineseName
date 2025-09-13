package com.xiaoyumc.chinesename.db;

import com.xiaoyumc.chinesename.ChineseName;
import com.xiaoyumc.chinesename.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.*;

import java.util.Set;

public final class StorageManager {

    private static final String current = ConfigManager.getSettings().getString("storage-type", "yaml").toLowerCase();

    public static String getCurrent() { return current; }

    /* ====== 统一 Player 入口 ====== */
    public static String getName(@NotNull Player player) {
        return getName(player.getUniqueId().toString());
    }

    public static void setName(@NotNull Player player, String name) {
        setName(player.getUniqueId().toString(), name);
    }

    public static void forceSetName(@NotNull Player player, String name) {
        setName(player.getUniqueId().toString(), name);
    }
    /* ============================ */
    public static @Nullable String getName(String uuid) {
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