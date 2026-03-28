package com.xiaoyumc.chinesename.card;

import com.xiaoyumc.chinesename.ChineseName;
import com.xiaoyumc.chinesename.util.SchedulerUtil;
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
        // 使用实体调度器确保在正确的区域线程中操作物品栏（Folia 兼容性）
        SchedulerUtil.runOnEntity(ChineseName.getInstance(), p, () -> {
            p.getInventory().addItem(card);
        });
    }
    /**
     * 扣除指定数量的改名卡
     */
    public static boolean takeCard(Player p, int amount) {
        final int[] removed = {0};
        // 使用实体调度器确保在正确的区域线程中操作物品栏（Folia 兼容性）
        SchedulerUtil.runOnEntity(ChineseName.getInstance(), p, () -> {
            for (ItemStack item : p.getInventory().getContents()) {
                if (!CardItemFactory.isCard(item)) continue;
                int toRemove = Math.min(amount - removed[0], item.getAmount());
                item.setAmount(item.getAmount() - toRemove);
                removed[0] += toRemove;
                if (removed[0] >= amount) return;
            }
        });
        return removed[0] >= amount;
    }
}