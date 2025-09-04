package com.xiaoyumc.chinesename;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PapiExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "chinesename";          // 前缀 %chinesename_xxx%
    }

    @Override
    public @NotNull String getAuthor() {
        return "xiaoyu";
    }

    @Override
    public @NotNull String getVersion() {
        return "2.0";
    }

    @Override
    public boolean persist() {
        return true;   // 不随 /papi reload 掉线
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player p, @NotNull String params) {
        if (p == null) return "";
        if ("name".equals(params)) {          // %chinesename_name%
            String cn = main.getInstance().getConfig().getString(p.getName());
            return cn == null ? p.getName() : cn;
        }
        return null;  // 未知占位符返回 null
    }
}