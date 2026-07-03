package com.quest;

public enum QuestCategory {
    MOBS("Mobs", "mob"),
    FLOWERS("Flowers", "flower"),
    TREES("Trees", "tree");

    private final String displayName;
    private final String singularName;

    QuestCategory(String displayName, String singularName) {
        this.displayName = displayName;
        this.singularName = singularName;
    }

    public String displayName() {
        return displayName;
    }

    public String singularName() {
        return singularName;
    }

    public static QuestCategory fromArgument(String argument) {
        for (QuestCategory category : values()) {
            if (category.name().equalsIgnoreCase(argument) || category.displayName.equalsIgnoreCase(argument)) {
                return category;
            }
        }

        return null;
    }
}
