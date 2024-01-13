package com.github.skyraah.randomjs.block.custom;

import com.github.skyraah.randomjs.block.api.RevelationBlock;
import dev.latvian.mods.kubejs.block.BlockBuilder;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Hashtable;
import java.util.Map;

public class RevelationBlockBuilder extends BlockBuilder {
    private ResourceLocation adv;

    public RevelationBlockBuilder(ResourceLocation i) {
        super(i);
    }

    @Override
    public Block createObject() {
        return new RevelationBlock(createProperties(), this.adv);
    }

    public RevelationBlockBuilder setClock(BlockState clockState, Item clockItem, MutableComponent blockText, MutableComponent itemText) {
        Map<BlockState, BlockState> blockCloaks = new Hashtable<>();
        blockCloaks.put(this.get().defaultBlockState(), clockState);
        return this;
    }

    public RevelationBlockBuilder setClock(BlockState clockState, Item clockItem, MutableComponent text) {
        Map<BlockState, BlockState> blockCloaks = new Hashtable<>();
        blockCloaks.put(this.get().defaultBlockState(), clockState);
        return this;
    }
}
