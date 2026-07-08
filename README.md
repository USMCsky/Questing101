# 🏆 Questing101

<p align="center">
  <img src="https://img.shields.io/badge/Minecraft-1.21.x-5E7C16?style=for-the-badge&logo=minecraft&logoColor=white" alt="Minecraft 1.21.x" />
  <img src="https://img.shields.io/badge/Spigot-API-F27B29?style=for-the-badge" alt="Spigot API" />
  <img src="https://img.shields.io/badge/Java-21-E76F00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21" />
  <img src="https://img.shields.io/badge/Author-USMCsky-6A4C93?style=for-the-badge&logo=github&logoColor=white" alt="Author USMCsky" />
</p>

A lightweight Spigot plugin that introduces a structured quest progression system, helping players follow objectives, track progress, and complete rewarding gameplay milestones.

## Features
- **Quest progression system** for structured player advancement.
- **Objective tracking** to keep players focused on active goals.
- **Quest completion flow** for smooth milestone-based gameplay.
- **Lightweight design** built for performance on Spigot servers.
- **Expandable framework** for adding additional quest content over time.

## Built For
- **Platform:** Spigot-compatible servers
- **Minecraft version:** 1.21.x
- **Language level:** Java 21

## How It Works
Once enabled, the plugin provides a quest-centered gameplay loop:

- **Track overall progress:** players can view completion totals by category.
- **Browse quest checklists:** players can open category-specific pages.
- **Advance naturally:** completed objectives move players through progression milestones.
- **Stay organized:** command-driven flow keeps quest tracking simple and clear.

## Command Usage
Use these commands in-game (**player-only command**):

- **`/quest`** — shows summary progress across all categories.
- **`/quest <category> [page]`** — shows checklist entries for a category, with optional page number.

### Categories
- `mobs`
- `flowers`
- `trees`

### Examples
- `/quest`
- `/quest mobs`
- `/quest flowers`
- `/quest trees`

### Notes
- If an invalid category is used, the plugin responds with:
  - `/quest [mobs|flowers|trees] [page]`
- The `page` argument must be a number.

## Usage
After setup, usage is simple:
- Run `/quest` to view summary progress.
- Use `/quest <category>` to view a checklist.
- Use `/quest <category> <page>` to view additional pages.
- Complete objectives while playing to progress.

## Compatibility
- Spigot API (project-defined)
- Minecraft 1.21.x servers
- Java 21

<img width="1025" height="340" alt="image" src="https://github.com/user-attachments/assets/8ef48a6b-4407-47c3-b3be-ca8ddfc6f775" />
