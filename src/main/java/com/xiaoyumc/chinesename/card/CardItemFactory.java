package com.xiaoyumc.chinesename.card;

import com.xiaoyumc.chinesename.config.ConfigManager;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

public final class CardItemFactory {

    private static final String NBT_KEY = "cn-rename-card";

    /**
     * 生成一张带有 NBT 标记的改名卡
     */
    public static ItemStack createCard() {
        ItemStack card = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = card.getItemMeta();
        if (meta != null) {
            String displayName = ConfigManager.getSettings()
                    .getString("name", "&d&l改名卡");
            List<String> lores = ConfigManager.getSettings()
                    .getStringList("lores");

            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
            meta.setLore(lores.stream()
                    .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                    .collect(Collectors.toList()));
            card.setItemMeta(meta);
        }

        NBTItem nbt = new NBTItem(card);
        nbt.setString(NBT_KEY, "true");
        return nbt.getItem();
    }

    /**
     * 判断物品是否为改名卡（NBT 校验）
     */
    public static boolean isCard(ItemStack item) {
        if (item == null || item.getType() != Material.NAME_TAG) return false;
        NBTItem nbt = new NBTItem(item);
        return nbt.hasKey(NBT_KEY);
    }
}