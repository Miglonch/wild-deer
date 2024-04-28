package com.Miglon.Entities.custom;

import java.util.Arrays;
import java.util.Comparator;


public enum DeerVariant {
    DEFAULT(0),
    ICE(1),
    COLD(2);


    private static final DeerVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.
            comparingInt(DeerVariant::getId)).toArray(DeerVariant[]::new);
    private final int id;

    DeerVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static DeerVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}

