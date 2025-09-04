package com.xiaoyumc.chinesename.config;

import org.bukkit.ChatColor;

public final class Messages {
    public static String get(String path, Object... placeholders) {
        String msg = ConfigManager.getMessages().getString(path, path);
        for (int i = 0; i < placeholders.length; i += 2)
            msg = msg.replace(String.valueOf(placeholders[i]), String.valueOf(placeholders[i + 1]));
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}