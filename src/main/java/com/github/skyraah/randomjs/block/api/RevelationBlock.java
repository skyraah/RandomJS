package com.github.skyraah.randomjs.block.api;

import de.dafuqs.revelationary.api.revelations.RevelationAware;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class RevelationBlock extends Block implements RevelationAware {
    public Map<BlockState, BlockState> blockStateMap;
    private ResourceLocation adv;

    public RevelationBlock(
            Properties properties,
            ResourceLocation adv
    ) {
        super(properties);
        RevelationAware.register(this);
        this.adv = adv;
    }

    @Override
    public ResourceLocation getCloakAdvancementIdentifier() {
        return this.adv;
    }

    @Override
    public Map<BlockState, BlockState> getBlockStateCloaks() {
        return blockStateMap;
    }

    @Override
    public @Nullable Tuple<Item, Item> getItemCloak() {
        return null;
    }

    @Override
    public @Nullable Tuple<Item, MutableComponent> getCloakedItemTranslation() {
        return RevelationAware.super.getCloakedItemTranslation();
    }

    @Override
    public @Nullable Tuple<Block, MutableComponent> getCloakedBlockTranslation() {
        return RevelationAware.super.getCloakedBlockTranslation();
    }
}
