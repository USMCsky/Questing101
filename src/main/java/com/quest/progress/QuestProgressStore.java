package com.quest.progress;

import com.quest.QuestCategory;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class QuestProgressStore {
    private final JavaPlugin plugin;
    private final File file;
    private final Map<UUID, PlayerQuestProgress> progressByPlayer = new ConcurrentHashMap<>();
    private YamlConfiguration configuration;

    public QuestProgressStore(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "progress.yml");

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        load();
    }

    public PlayerQuestProgress getProgress(UUID playerId) {
        return progressByPlayer.computeIfAbsent(playerId, ignored -> new PlayerQuestProgress());
    }

    public void save() {
        YamlConfiguration output = new YamlConfiguration();

        for (Map.Entry<UUID, PlayerQuestProgress> entry : progressByPlayer.entrySet()) {
            String basePath = "players." + entry.getKey();
            PlayerQuestProgress progress = entry.getValue();

            for (QuestCategory category : QuestCategory.values()) {
                String categoryKey = category.name().toLowerCase();
                output.set(basePath + ".completed." + categoryKey, progress.getCompletedKeys(category).stream().sorted().toList());
                output.set(basePath + ".claimed-milestones." + categoryKey, progress.getClaimedMilestones(category).stream().sorted().toList());
            }

            output.set(
                basePath + ".claimed-final-rewards",
                progress.getClaimedFinalRewards().stream().map(category -> category.name().toLowerCase()).sorted().toList()
            );
        }

        try {
            output.save(file);
            this.configuration = output;
        } catch (IOException exception) {
            plugin.getLogger().severe("Failed to save quest progress: " + exception.getMessage());
        }
    }

    private void load() {
        this.configuration = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection playersSection = configuration.getConfigurationSection("players");
        if (playersSection == null) {
            return;
        }

        for (String playerKey : playersSection.getKeys(false)) {
            UUID playerId;
            try {
                playerId = UUID.fromString(playerKey);
            } catch (IllegalArgumentException exception) {
                plugin.getLogger().warning("Skipping invalid player progress entry: " + playerKey);
                continue;
            }

            PlayerQuestProgress progress = new PlayerQuestProgress();
            for (QuestCategory category : QuestCategory.values()) {
                String categoryKey = category.name().toLowerCase();
                for (String completedKey : configuration.getStringList("players." + playerKey + ".completed." + categoryKey)) {
                    progress.markCompleted(category, completedKey);
                }

                for (int milestone : configuration.getIntegerList("players." + playerKey + ".claimed-milestones." + categoryKey)) {
                    progress.markMilestoneClaimed(category, milestone);
                }
            }

            for (String categoryKey : configuration.getStringList("players." + playerKey + ".claimed-final-rewards")) {
                try {
                    progress.markFinalRewardClaimed(QuestCategory.valueOf(categoryKey.toUpperCase()));
                } catch (IllegalArgumentException exception) {
                    plugin.getLogger().warning("Skipping invalid final reward category for " + playerKey + ": " + categoryKey);
                }
            }

            progressByPlayer.put(playerId, progress);
        }
    }
}
