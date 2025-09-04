package com.xiaoyumc.chinesename.card;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public final class CardManager implements Listener {

    /**
     * 给予指定数量的改名卡
     */
    public static void giveCard(Player p, int amount) {
        ItemStack card = CardItemFactory.createCard();
        card.setAmount(amount);
        p.getInventory().addItem(card);
    }
    /**
     * 扣除指定数量的改名卡
     */
    public static boolean takeCard(Player p, int amount) {
        int removed = 0;
        for (ItemStack item : p.getInventory().getContents()) {
            if (!CardItemFactory.isCard(item)) continue;
            int toRemove = Math.min(amount - removed, item.getAmount());
            item.setAmount(item.getAmount() - toRemove);
            removed += toRemove;
            if (removed >= amount) return true;
        }
        return removed >= amount;
    }
}