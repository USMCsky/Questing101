package com.quest;

import com.quest.command.QuestCommand;
import com.quest.progress.QuestProgressStore;
import org.bukkit.plugin.java.JavaPlugin;

public final class QuestPlugin extends JavaPlugin {
    private QuestManager questManager;
    private QuestProgressStore progressStore;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.progressStore = new QuestProgressStore(this);
        QuestRegistry registry = new QuestRegistry(getLogger());
        RewardService rewardService = new RewardService(this);
        this.questManager = new QuestManager(registry, progressStore, rewardService);

        getServer().getPluginManager().registerEvents(new QuestListener(this, questManager), this);

        QuestCommand questCommand = new QuestCommand(questManager);
        if (getCommand("quest") != null) {
            getCommand("quest").setExecutor(questCommand);
            getCommand("quest").setTabCompleter(questCommand);
        }
    }

    @Override
    public void onDisable() {
        if (progressStore != null) {
            progressStore.save();
        }
    }
}
