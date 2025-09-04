package com.xiaoyumc.chinesename;

import com.xiaoyumc.chinesename.db.StorageManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChineseExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() { return "chinesename"; }

    @Override
    public @NotNull String getAuthor() { return "xiaoyu"; }

    @Override
    public @NotNull String getVersion() { return "2.0"; }

    @Override
    public boolean persist() { return true; }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) return "";
        if ("name".equalsIgnoreCase(identifier)) {
            return StorageManager.getName(player.getUniqueId().toString());
        }
        return null;
    }
}