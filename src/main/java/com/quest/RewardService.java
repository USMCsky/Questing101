package com.quest;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public final class RewardService {
    private final JavaPlugin plugin;
    private final Map<QuestCategory, Integer> completionXp = new EnumMap<>(QuestCategory.class);
    private final Map<QuestCategory, List<Integer>> milestones = new EnumMap<>(QuestCategory.class);
    private final List<RewardDefinition> rewardPool = new ArrayList<>();
    private final int finalRewardRolls;

    public RewardService(JavaPlugin plugin) {
        this.plugin = plugin;

        completionXp.put(QuestCategory.MOBS, plugin.getConfig().getInt("first-completion-xp.mobs", 25));
        completionXp.put(QuestCategory.FLOWERS, plugin.getConfig().getInt("first-completion-xp.flowers", 15));
        completionXp.put(QuestCategory.TREES, plugin.getConfig().getInt("first-completion-xp.trees", 15));

        milestones.put(QuestCategory.MOBS, plugin.getConfig().getIntegerList("milestones.mobs"));
        milestones.put(QuestCategory.FLOWERS, plugin.getConfig().getIntegerList("milestones.flowers"));
        milestones.put(QuestCategory.TREES, plugin.getConfig().getIntegerList("milestones.trees"));
        this.finalRewardRolls = Math.max(1, plugin.getConfig().getInt("milestones.final-reward-rolls", 2));

        loadRewardPool();
    }

    public int getCompletionXp(QuestCategory category) {
        return completionXp.getOrDefault(category, 0);
    }

    public List<Integer> getMilestones(QuestCategory category) {
        return milestones.getOrDefault(category, List.of());
    }

    public void giveMilestoneReward(Player player, QuestCategory category, int milestone) {
        ItemStack reward = createReward();
        deliverReward(player, reward);
        player.sendMessage(ChatColor.GOLD + "[Quest] " + ChatColor.YELLOW + "Milestone reached: " + milestone + " " + category.displayName().toLowerCase(Locale.ROOT) + " completed.");
    }

    public void giveFinalReward(Player player, QuestCategory category) {
        for (int index = 0; index < finalRewardRolls; index++) {
            deliverReward(player, createReward());
        }

        player.sendMessage(ChatColor.GOLD + "[Quest] " + ChatColor.GREEN + "Category complete: " + category.displayName() + ". Final rewards granted.");
    }

    private void deliverReward(Player player, ItemStack reward) {
        PlayerInventory inventory = player.getInventory();
        Map<Integer, ItemStack> leftovers = inventory.addItem(reward);
        leftovers.values().forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));
    }

    private ItemStack createReward() {
        if (rewardPool.isEmpty()) {
            return new ItemStack(Material.NETHERITE_INGOT, 1);
        }

        int totalWeight = rewardPool.stream().mapToInt(RewardDefinition::weight).sum();
        int roll = ThreadLocalRandom.current().nextInt(totalWeight);
        int runningWeight = 0;

        for (RewardDefinition rewardDefinition : rewardPool) {
            runningWeight += rewardDefinition.weight();
            if (roll < runningWeight) {
                return rewardDefinition.createItem();
            }
        }

        return rewardPool.get(rewardPool.size() - 1).createItem();
    }

    private void loadRewardPool() {
        rewardPool.clear();

        List<Map<?, ?>> rewardSections = plugin.getConfig().getMapList("reward-pool");
        for (Map<?, ?> rewardSection : rewardSections) {
            String materialName = Objects.toString(rewardSection.get("material"), "");
            Material material = Material.matchMaterial(materialName);
            if (material == null) {
                plugin.getLogger().warning("Skipping reward with unknown material: " + materialName);
                continue;
            }

            int amount = Math.max(1, parseInteger(rewardSection.get("amount"), 1));
            int weight = Math.max(1, parseInteger(rewardSection.get("weight"), 1));
            RewardDefinition rewardDefinition = new RewardDefinition(material, amount, weight);

            Object enchantments = rewardSection.get("enchantments");
            if (enchantments instanceof ConfigurationSection enchantmentSection) {
                loadEnchantments(rewardDefinition, enchantmentSection.getValues(false));
            } else if (enchantments instanceof Map<?, ?> enchantmentMap) {
                loadEnchantments(rewardDefinition, enchantmentMap);
            }

            rewardPool.add(rewardDefinition);
        }
    }

    private void loadEnchantments(RewardDefinition rewardDefinition, Map<?, ?> enchantmentMap) {
        for (Map.Entry<?, ?> enchantmentEntry : enchantmentMap.entrySet()) {
            String enchantmentName = Objects.toString(enchantmentEntry.getKey(), "");
            Enchantment enchantment = Registry.ENCHANTMENT.get(NamespacedKey.minecraft(enchantmentName.toLowerCase(Locale.ROOT)));
            if (enchantment == null) {
                plugin.getLogger().warning("Skipping unknown enchantment: " + enchantmentName);
                continue;
            }

            rewardDefinition.enchantments().put(enchantment, Math.max(1, parseInteger(enchantmentEntry.getValue(), 1)));
        }
    }

    private int parseInteger(Object value, int fallback) {
        if (value instanceof Number number) {
            return number.intValue();
        }

        if (value instanceof String stringValue) {
            try {
                return Integer.parseInt(stringValue);
            } catch (NumberFormatException ignored) {
                return fallback;
            }
        }

        return fallback;
    }

    private static final class RewardDefinition {
        private final Material material;
        private final int amount;
        private final int weight;
        private final Map<Enchantment, Integer> enchantments = new java.util.HashMap<>();

        private RewardDefinition(Material material, int amount, int weight) {
            this.material = material;
            this.amount = amount;
            this.weight = weight;
        }

        public int weight() {
            return weight;
        }

        public Map<Enchantment, Integer> enchantments() {
            return enchantments;
        }

        public ItemStack createItem() {
            ItemStack itemStack = new ItemStack(material, amount);
            if (!enchantments.isEmpty()) {
                ItemMeta itemMeta = itemStack.getItemMeta();
                enchantments.forEach((enchantment, level) -> itemMeta.addEnchant(enchantment, level, true));
                itemStack.setItemMeta(itemMeta);
            }
            return itemStack;
        }
    }
}
