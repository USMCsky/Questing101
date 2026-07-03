package com.quest;

import com.quest.progress.PlayerQuestProgress;
import com.quest.progress.QuestProgressStore;
import java.util.Collection;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class QuestManager {
    private final QuestRegistry registry;
    private final QuestProgressStore progressStore;
    private final RewardService rewardService;

    public QuestManager(QuestRegistry registry, QuestProgressStore progressStore, RewardService rewardService) {
        this.registry = registry;
        this.progressStore = progressStore;
        this.rewardService = rewardService;
    }

    public void handleMobKill(Player player, EntityType entityType) {
        QuestDefinition definition = registry.getMobDefinition(entityType);
        if (definition != null) {
            handleCompletion(player, definition);
        }
    }

    public void scanInventory(Player player) {
        for (ItemStack itemStack : player.getInventory().getContents()) {
            scanItem(player, itemStack);
        }
    }

    public void scanItem(Player player, ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return;
        }

        QuestDefinition flowerDefinition = registry.getFlowerDefinition(itemStack.getType());
        if (flowerDefinition != null) {
            handleCompletion(player, flowerDefinition);
        }

        QuestDefinition treeDefinition = registry.getTreeDefinition(itemStack.getType());
        if (treeDefinition != null) {
            handleCompletion(player, treeDefinition);
        }
    }

    public PlayerQuestProgress getProgress(UUID playerId) {
        return progressStore.getProgress(playerId);
    }

    public Collection<QuestDefinition> getDefinitions(QuestCategory category) {
        return registry.getDefinitions(category);
    }

    public int getTotal(QuestCategory category) {
        return registry.getTotal(category);
    }

    private void handleCompletion(Player player, QuestDefinition definition) {
        PlayerQuestProgress progress = progressStore.getProgress(player.getUniqueId());
        if (!progress.markCompleted(definition.category(), definition.key())) {
            return;
        }

        int xp = rewardService.getCompletionXp(definition.category());
        if (xp > 0) {
            player.giveExp(xp);
        }

        int completedCount = progress.getCompletedCount(definition.category());
        int totalCount = registry.getTotal(definition.category());

        player.sendMessage(
            ChatColor.GOLD + "[Quest] "
                + ChatColor.GREEN + "Completed "
                + definition.category().singularName()
                + ": "
                + ChatColor.AQUA + definition.displayName()
                + ChatColor.GRAY + " (" + completedCount + "/" + totalCount + ")"
        );

        for (int milestone : rewardService.getMilestones(definition.category())) {
            if (completedCount >= milestone && !progress.hasClaimedMilestone(definition.category(), milestone)) {
                progress.markMilestoneClaimed(definition.category(), milestone);
                rewardService.giveMilestoneReward(player, definition.category(), milestone);
            }
        }

        if (completedCount == totalCount && !progress.hasClaimedFinalReward(definition.category())) {
            progress.markFinalRewardClaimed(definition.category());
            rewardService.giveFinalReward(player, definition.category());
        }

        progressStore.save();
    }
}
