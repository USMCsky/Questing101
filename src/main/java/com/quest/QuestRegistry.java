package com.quest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public final class QuestRegistry {
    private final Map<QuestCategory, List<QuestDefinition>> definitionsByCategory = new EnumMap<>(QuestCategory.class);
    private final Map<EntityType, QuestDefinition> mobsByEntity = new HashMap<>();
    private final Map<Material, QuestDefinition> flowersByMaterial = new HashMap<>();
    private final Map<Material, QuestDefinition> treesByMaterial = new HashMap<>();

    public QuestRegistry(Logger logger) {
        definitionsByCategory.put(QuestCategory.MOBS, buildMobDefinitions());
        definitionsByCategory.put(QuestCategory.FLOWERS, buildMaterialDefinitions(logger, QuestCategory.FLOWERS, buildFlowerSources(), flowersByMaterial));
        definitionsByCategory.put(QuestCategory.TREES, buildMaterialDefinitions(logger, QuestCategory.TREES, buildTreeSources(), treesByMaterial));
    }

    public Collection<QuestDefinition> getDefinitions(QuestCategory category) {
        return definitionsByCategory.getOrDefault(category, List.of());
    }

    public int getTotal(QuestCategory category) {
        return getDefinitions(category).size();
    }

    public QuestDefinition getMobDefinition(EntityType entityType) {
        return mobsByEntity.get(entityType);
    }

    public QuestDefinition getFlowerDefinition(Material material) {
        return flowersByMaterial.get(material);
    }

    public QuestDefinition getTreeDefinition(Material material) {
        return treesByMaterial.get(material);
    }

    private List<QuestDefinition> buildMobDefinitions() {
        List<QuestDefinition> definitions = new ArrayList<>();

        for (EntityType entityType : EntityType.values()) {
            Class<?> entityClass = entityType.getEntityClass();
            if (
                !entityType.isAlive()
                    || !entityType.isSpawnable()
                    || entityClass == null
                    || !LivingEntity.class.isAssignableFrom(entityClass)
                    || entityType == EntityType.PLAYER
            ) {
                continue;
            }

            QuestDefinition definition = new QuestDefinition(entityType.getKey().getKey(), toDisplayName(entityType.getKey().getKey()), QuestCategory.MOBS);
            definitions.add(definition);
            mobsByEntity.put(entityType, definition);
        }

        definitions.sort(Comparator.comparing(QuestDefinition::displayName));
        return Collections.unmodifiableList(definitions);
    }

    private List<QuestDefinition> buildMaterialDefinitions(
        Logger logger,
        QuestCategory category,
        Map<String, List<String>> materialSources,
        Map<Material, QuestDefinition> lookup
    ) {
        List<QuestDefinition> definitions = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : materialSources.entrySet()) {
            String key = entry.getKey().toLowerCase(Locale.ROOT).replace(' ', '_');
            QuestDefinition definition = new QuestDefinition(key, entry.getKey(), category);

            boolean foundMatch = false;
            for (String materialName : entry.getValue()) {
                Material material = Material.matchMaterial(materialName);
                if (material == null) {
                    continue;
                }

                foundMatch = true;
                lookup.put(material, definition);
            }

            if (foundMatch) {
                definitions.add(definition);
            } else {
                logger.warning("Skipping " + category.name().toLowerCase(Locale.ROOT) + " quest entry with no matching materials: " + entry.getKey());
            }
        }

        return Collections.unmodifiableList(definitions);
    }

    private Map<String, List<String>> buildFlowerSources() {
        Map<String, List<String>> definitions = new LinkedHashMap<>();
        definitions.put("Dandelion", List.of("DANDELION"));
        definitions.put("Poppy", List.of("POPPY"));
        definitions.put("Blue Orchid", List.of("BLUE_ORCHID"));
        definitions.put("Allium", List.of("ALLIUM"));
        definitions.put("Azure Bluet", List.of("AZURE_BLUET"));
        definitions.put("Red Tulip", List.of("RED_TULIP"));
        definitions.put("Orange Tulip", List.of("ORANGE_TULIP"));
        definitions.put("White Tulip", List.of("WHITE_TULIP"));
        definitions.put("Pink Tulip", List.of("PINK_TULIP"));
        definitions.put("Oxeye Daisy", List.of("OXEYE_DAISY"));
        definitions.put("Cornflower", List.of("CORNFLOWER"));
        definitions.put("Lily of the Valley", List.of("LILY_OF_THE_VALLEY"));
        definitions.put("Sunflower", List.of("SUNFLOWER"));
        definitions.put("Lilac", List.of("LILAC"));
        definitions.put("Rose Bush", List.of("ROSE_BUSH"));
        definitions.put("Peony", List.of("PEONY"));
        definitions.put("Wither Rose", List.of("WITHER_ROSE"));
        definitions.put("Torchflower", List.of("TORCHFLOWER"));
        definitions.put("Spore Blossom", List.of("SPORE_BLOSSOM"));
        definitions.put("Pink Petals", List.of("PINK_PETALS"));
        definitions.put("Open Eyeblossom", List.of("OPEN_EYEBLOSSOM"));
        definitions.put("Closed Eyeblossom", List.of("CLOSED_EYEBLOSSOM"));
        definitions.put("Wildflowers", List.of("WILDFLOWERS"));
        definitions.put("Cactus Flower", List.of("CACTUS_FLOWER"));
        return definitions;
    }

    private Map<String, List<String>> buildTreeSources() {
        Map<String, List<String>> definitions = new LinkedHashMap<>();
        definitions.put("Oak", List.of("OAK_LOG", "OAK_WOOD", "STRIPPED_OAK_LOG", "STRIPPED_OAK_WOOD", "OAK_SAPLING"));
        definitions.put("Spruce", List.of("SPRUCE_LOG", "SPRUCE_WOOD", "STRIPPED_SPRUCE_LOG", "STRIPPED_SPRUCE_WOOD", "SPRUCE_SAPLING"));
        definitions.put("Birch", List.of("BIRCH_LOG", "BIRCH_WOOD", "STRIPPED_BIRCH_LOG", "STRIPPED_BIRCH_WOOD", "BIRCH_SAPLING"));
        definitions.put("Jungle", List.of("JUNGLE_LOG", "JUNGLE_WOOD", "STRIPPED_JUNGLE_LOG", "STRIPPED_JUNGLE_WOOD", "JUNGLE_SAPLING"));
        definitions.put("Acacia", List.of("ACACIA_LOG", "ACACIA_WOOD", "STRIPPED_ACACIA_LOG", "STRIPPED_ACACIA_WOOD", "ACACIA_SAPLING"));
        definitions.put("Dark Oak", List.of("DARK_OAK_LOG", "DARK_OAK_WOOD", "STRIPPED_DARK_OAK_LOG", "STRIPPED_DARK_OAK_WOOD", "DARK_OAK_SAPLING"));
        definitions.put("Mangrove", List.of("MANGROVE_LOG", "MANGROVE_WOOD", "STRIPPED_MANGROVE_LOG", "STRIPPED_MANGROVE_WOOD", "MANGROVE_PROPAGULE"));
        definitions.put("Cherry", List.of("CHERRY_LOG", "CHERRY_WOOD", "STRIPPED_CHERRY_LOG", "STRIPPED_CHERRY_WOOD", "CHERRY_SAPLING"));
        definitions.put("Pale Oak", List.of("PALE_OAK_LOG", "PALE_OAK_WOOD", "STRIPPED_PALE_OAK_LOG", "STRIPPED_PALE_OAK_WOOD", "PALE_OAK_SAPLING"));
        definitions.put("Bamboo", List.of("BAMBOO", "BAMBOO_BLOCK", "STRIPPED_BAMBOO_BLOCK"));
        definitions.put("Crimson", List.of("CRIMSON_STEM", "CRIMSON_HYPHAE", "STRIPPED_CRIMSON_STEM", "STRIPPED_CRIMSON_HYPHAE", "CRIMSON_FUNGUS"));
        definitions.put("Warped", List.of("WARPED_STEM", "WARPED_HYPHAE", "STRIPPED_WARPED_STEM", "STRIPPED_WARPED_HYPHAE", "WARPED_FUNGUS"));
        return definitions;
    }

    private String toDisplayName(String key) {
        String[] parts = key.split("_");
        StringBuilder builder = new StringBuilder();

        for (String part : parts) {
            if (builder.length() > 0) {
                builder.append(' ');
            }

            builder.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                builder.append(part.substring(1).toLowerCase(Locale.ROOT));
            }
        }

        return builder.toString();
    }
}
