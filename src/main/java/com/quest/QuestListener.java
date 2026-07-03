package com.quest;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class QuestListener implements Listener {
    private final JavaPlugin plugin;
    private final QuestManager questManager;

    public QuestListener(JavaPlugin plugin, QuestManager questManager) {
        this.plugin = plugin;
        this.questManager = questManager;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }

        questManager.handleMobKill(killer, event.getEntityType());
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        ItemStack itemStack = event.getItem().getItemStack();
        questManager.scanItem(player, itemStack);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        plugin.getServer().getScheduler().runTask(plugin, () -> questManager.scanInventory(player));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getServer().getScheduler().runTask(plugin, () -> questManager.scanInventory(player));
    }
}
