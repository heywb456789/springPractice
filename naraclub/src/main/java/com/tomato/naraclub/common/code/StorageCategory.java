package com.tomato.naraclub.common.code;

public enum StorageCategory {
    VIDEO("videos"),
    IMAGE("images");

    private final String folder;

    StorageCategory(String folder) {
        this.folder = folder;
    }

    public String getFolder() {
        return folder;
    }
}