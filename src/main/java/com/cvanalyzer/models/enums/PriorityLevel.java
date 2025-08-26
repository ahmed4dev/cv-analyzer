package com.cvanalyzer.models.enums;


public enum PriorityLevel {
    CRITICAL(4, "Critique"),
    HIGH(3, "Élevée"),
    MEDIUM(2, "Moyenne"),
    LOW(1, "Basse");

    private final int level;
    private final String displayName;

    PriorityLevel(int level, String displayName) {
        this.level = level;
        this.displayName = displayName;
    }

    // Getters
    public int getLevel() {
        return level;
    }

    public String getDisplayName() {
        return displayName;
    }
}

