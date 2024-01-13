package com.github.skyraah.randomjs.custom;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

enum SuperType implements StringRepresentable {
    CARPET("carpet"),
    REDSTONE("redstone_wire"),
    NONE("none"),
    CUSTOM("custom");

    private final String name;

    SuperType(String p_61680_) {
        this.name = p_61680_;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}
