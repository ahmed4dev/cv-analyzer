package com.cvanalyzer.models.enums;

public enum SuggestionType {
    CONTENT("Contenu du CV"),
    STRUCTURE("Structure du CV"),
    KEYWORD("Mots-clés"),
    EXPERIENCE("Expérience professionnelle"),
    EDUCATION("Formation");

    private final String displayName;

    SuggestionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
