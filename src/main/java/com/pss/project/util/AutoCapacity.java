package com.pss.project.util;

public enum AutoCapacity {
    LOW    (1000),
    MEDIUM (1500),
    HIGH   (2000);

    private final int capacity;

    AutoCapacity(int capacity){
        this.capacity = capacity;
    }
}
