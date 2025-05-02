package com.tomato.naraclub.application.original.code;

public enum OriginalCategory {
    ECONOMY("경제"),
    BUSINESS("비즈니스"),
    SOCIETY("사회"),
    FUN("엔터테인먼트");

    private final String displayName;

    OriginalCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
