package com.github.skyraah.randomjs.custom;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.block.BlockBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;


@SuppressWarnings("unused")
public class SuperBlockBuilder extends BlockBuilder {
    public boolean isCustomSurviveType;
    /*记录着方块与Survive类型*/
    public static HashMap<ResourceLocation, String> surviveType = new HashMap<>();
    /*记录着方块与自定义规则*/
    public static HashMap<ResourceLocation, JsonObject> ruleJson = new HashMap<>();
    public static HashMap<ResourceLocation, Boolean> isCatchAllDirection = new HashMap<>();
    public SuperBlockBuilder(ResourceLocation i) {
        super(i);
        isCustomSurviveType = false;
    }

    @Override
    public Block createObject() {
        return new SuperBlockBase(createProperties());
    }

    public BlockBuilder customSurviveRule(JsonObject json){
        if (json != null){
            isCustomSurviveType = true;
            ruleJson.put(id, json);
        }
        return this;
    }

    public BlockBuilder surviveType(String t){
        if (isCustomSurviveType){
            surviveType.put(id, "custom");
        } else surviveType.put(id, t);
        return this;
    }

    public BlockBuilder catchAllDirection(boolean b){
        isCatchAllDirection.put(id, b);
        return this;
    }
}
