package com.quest.progress;

import com.quest.QuestCategory;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class PlayerQuestProgress {
    private final Map<QuestCategory, Set<String>> completedKeys = new EnumMap<>(QuestCategory.class);
    private final Map<QuestCategory, Set<Integer>> claimedMilestones = new EnumMap<>(QuestCategory.class);
    private final Set<QuestCategory> claimedFinalRewards = new HashSet<>();

    public PlayerQuestProgress() {
        for (QuestCategory category : QuestCategory.values()) {
            completedKeys.put(category, new HashSet<>());
            claimedMilestones.put(category, new HashSet<>());
        }
    }

    public boolean markCompleted(QuestCategory category, String key) {
        return completedKeys.get(category).add(key);
    }

    public boolean isCompleted(QuestCategory category, String key) {
        return completedKeys.get(category).contains(key);
    }

    public Set<String> getCompletedKeys(QuestCategory category) {
        return Collections.unmodifiableSet(completedKeys.get(category));
    }

    public int getCompletedCount(QuestCategory category) {
        return completedKeys.get(category).size();
    }

    public boolean hasClaimedMilestone(QuestCategory category, int milestone) {
        return claimedMilestones.get(category).contains(milestone);
    }

    public void markMilestoneClaimed(QuestCategory category, int milestone) {
        claimedMilestones.get(category).add(milestone);
    }

    public Set<Integer> getClaimedMilestones(QuestCategory category) {
        return Collections.unmodifiableSet(claimedMilestones.get(category));
    }

    public boolean hasClaimedFinalReward(QuestCategory category) {
        return claimedFinalRewards.contains(category);
    }

    public void markFinalRewardClaimed(QuestCategory category) {
        claimedFinalRewards.add(category);
    }

    public Set<QuestCategory> getClaimedFinalRewards() {
        return Collections.unmodifiableSet(claimedFinalRewards);
    }
}
