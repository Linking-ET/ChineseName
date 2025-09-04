package com.xiaoyumc.chinesename.card;

import com.xiaoyumc.chinesename.ChineseName;
import com.xiaoyumc.chinesename.config.ConfigManager;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

public final class CardItemFactory {

    private static final String NBT_KEY = "cn-rename-card";
    private static final String FALLBACK_NAME = "§d§l改名卡";
    private static final List<String> FALLBACK_LORE = List.of("§e/cn usecard <名字>");

    private static boolean nbtLoaded = true;

    /* 生成卡片：先尝试 NBT，失败则回退 */
    public static ItemStack createCard() {
        try {
            if (nbtLoaded) {
                ItemStack card = new ItemStack(Material.NAME_TAG);
                ItemMeta meta = card.getItemMeta();
                if (meta != null) {
                    String displayName = ConfigManager.getSettings().getString("name", "&d&l改名卡");
                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
                    meta.setLore(ConfigManager.getSettings()
                            .getStringList("lores")
                            .stream()
                            .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                            .collect(Collectors.toList()));
                    card.setItemMeta(meta);
                }
                new NBTItem(card).setString(NBT_KEY, "true");
                return card;
            }
        } catch (Throwable ignored) {
            nbtLoaded = false;
            Bukkit.getLogger().warning("[ChineseName] NBT-API 不可用，已回退到 Bukkit 识别方式！");
        }

        // 回退：无 NBT，仅用 displayName + lore
        ItemStack card = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = card.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(FALLBACK_NAME);
            meta.setLore(FALLBACK_LORE);
            card.setItemMeta(meta);
        }
        return card;
    }

    /* 判断：先尝试 NBT，失败则回退 */
    public static boolean isCard(ItemStack item) {
        if (item == null || item.getType() != Material.NAME_TAG) return false;
        try {
            if (nbtLoaded) {
                return new NBTItem(item).hasKey(NBT_KEY);
            }
        } catch (Throwable ignored) {
            nbtLoaded = false;
        }

        // 回退：displayName + lore 首行
        ItemMeta meta = item.getItemMeta();
        return meta != null &&
                FALLBACK_NAME.equals(meta.getDisplayName()) &&
                meta.hasLore() &&
                FALLBACK_LORE.get(0).equals(meta.getLore().get(0));
    }
}