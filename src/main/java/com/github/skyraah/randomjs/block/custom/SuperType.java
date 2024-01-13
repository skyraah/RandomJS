package com.github.skyraah.randomjs.block.custom;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

enum SuperType implements StringRepresentable {
    CARPET("carpet"),
    REDSTONE("redstone_wire"),
    NONE("none"),
    CUSTOM("custom");

    private final String name;

    SuperType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}
