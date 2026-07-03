package com.quest.command;

import com.quest.QuestCategory;
import com.quest.QuestDefinition;
import com.quest.QuestManager;
import com.quest.progress.PlayerQuestProgress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public final class QuestCommand implements CommandExecutor, TabCompleter {
    private static final int PAGE_SIZE = 10;

    private final QuestManager questManager;

    public QuestCommand(QuestManager questManager) {
        this.questManager = questManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (args.length == 0) {
            sendSummary(player);
            return true;
        }

        QuestCategory category = QuestCategory.fromArgument(args[0]);
        if (category == null) {
            player.sendMessage(ChatColor.RED + "Usage: /" + label + " [mobs|flowers|trees] [page]");
            return true;
        }

        int page = 1;
        if (args.length >= 2) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException exception) {
                player.sendMessage(ChatColor.RED + "Page must be a number.");
                return true;
            }
        }

        sendCategory(player, category, page);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            String prefix = args[0].toLowerCase(Locale.ROOT);

            for (QuestCategory category : QuestCategory.values()) {
                if (category.name().toLowerCase(Locale.ROOT).startsWith(prefix)) {
                    completions.add(category.name().toLowerCase(Locale.ROOT));
                }
            }

            return completions;
        }

        return List.of();
    }

    private void sendSummary(Player player) {
        PlayerQuestProgress progress = questManager.getProgress(player.getUniqueId());

        player.sendMessage(ChatColor.GOLD + "=== Quest Progress ===");
        for (QuestCategory category : QuestCategory.values()) {
            int completed = progress.getCompletedCount(category);
            int total = questManager.getTotal(category);
            player.sendMessage(
                ChatColor.YELLOW + category.displayName() + ": "
                    + ChatColor.GREEN + completed
                    + ChatColor.GRAY + "/"
                    + ChatColor.AQUA + total
            );
        }

        player.sendMessage(ChatColor.GRAY + "Use /quest <category> [page] to view the checklist.");
    }

    private void sendCategory(Player player, QuestCategory category, int page) {
        Collection<QuestDefinition> definitions = questManager.getDefinitions(category);
        if (definitions.isEmpty()) {
            player.sendMessage(ChatColor.RED + "No quest definitions are available for " + category.displayName().toLowerCase(Locale.ROOT) + ".");
            return;
        }

        List<QuestDefinition> definitionList = new ArrayList<>(definitions);
        PlayerQuestProgress progress = questManager.getProgress(player.getUniqueId());
        int completedCount = progress.getCompletedCount(category);
        int totalPages = Math.max(1, (int) Math.ceil(definitionList.size() / (double) PAGE_SIZE));
        int safePage = Math.min(Math.max(page, 1), totalPages);
        int startIndex = (safePage - 1) * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, definitionList.size());

        player.sendMessage(
            ChatColor.GOLD + "=== " + category.displayName() + " Progress === "
                + ChatColor.GREEN + completedCount
                + ChatColor.GRAY + "/"
                + ChatColor.AQUA + definitionList.size()
                + ChatColor.GOLD + " | Page "
                + ChatColor.YELLOW + safePage
                + ChatColor.GRAY + "/"
                + ChatColor.YELLOW + totalPages
        );
        for (int index = startIndex; index < endIndex; index++) {
            QuestDefinition definition = definitionList.get(index);
            boolean completed = progress.isCompleted(category, definition.key());
            player.sendMessage((completed ? ChatColor.GREEN + "[x] " : ChatColor.RED + "[ ] ") + ChatColor.WHITE + definition.displayName());
        }

        if (totalPages > 1) {
            player.sendMessage(
                ChatColor.GRAY + "Use /quest "
                    + category.name().toLowerCase(Locale.ROOT)
                    + " <page> to view more entries."
            );
        }
    }
}
