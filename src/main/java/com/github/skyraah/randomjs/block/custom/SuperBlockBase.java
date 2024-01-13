package com.github.skyraah.randomjs.block.custom;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.block.custom.BasicBlockJS;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings(value = {"deprecation", "unused"})
public class SuperBlockBase extends BasicBlockJS {
    private static final HashMap<ResourceLocation, HashMap<String, List<String>>> CUSTOM_RULE = new HashMap<>();
    private static final BooleanProperty CATCH_ALL_DIR = BooleanProperty.create("catch_all_dir");
    private static final EnumProperty<SuperType> SUPER_TYPE = EnumProperty.create("super_type", SuperType.class);

    public SuperBlockBase(SuperBlockBuilder builder) {
        super(builder);
        registerDefaultState(getStateDefinition().any()
            .setValue(CATCH_ALL_DIR, builder.catchAllDirection)
            .setValue(SUPER_TYPE, builder.surviveType)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CATCH_ALL_DIR).add(SUPER_TYPE);
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader reader, @NotNull BlockPos pos) {
        BlockPos belowPos = pos.below();
        switch (state.getValue(SUPER_TYPE)) {
            case CARPET -> {
                return !reader.isEmptyBlock(belowPos);
            }
            case REDSTONE -> {
                BlockState belowState = reader.getBlockState(belowPos);
                return canSurviveOn(reader, belowPos, belowState);
            }
            case NONE -> {
                return false;
            }
            case CUSTOM -> {
                return ruleChecker(state, reader, pos);
            }
            default -> {
                return true;
            }
        }
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState blockState, @NotNull Direction direction, @NotNull BlockState blockState2, @NotNull LevelAccessor levelAccessor, @NotNull BlockPos blockPos, @NotNull BlockPos blockPos2) {
        if (!blockState.canSurvive(levelAccessor, blockPos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
    }

    private boolean ruleChecker(BlockState state, LevelReader reader, BlockPos pos) {
        boolean isBlowBlock = true;
        boolean isFaceSturdy = true;
        for (String t : customType(state.getBlock())) {
            switch (t) {
                case "setBelowBlock" -> isBlowBlock = this.isSetBelowBlock(state, reader, pos);
                case "setFaceSturdy" -> isFaceSturdy = this.isSetFaceSturdy(state, reader, pos);
            }
        }
        return isBlowBlock && isFaceSturdy;
    }

    private boolean isSetFaceSturdy(BlockState state, BlockGetter getter, BlockPos pos) {
        boolean setFaceSturdy = true;
        ResourceLocation b = RegistryInfo.BLOCK.getId(state.getBlock());
        for (Direction d : this.directionList(b)) {
            switch (d) {
                case UP ->
                    setFaceSturdy = getter.getBlockState(pos.below()).isFaceSturdy(getter, pos.below(), Direction.UP);
                case DOWN ->
                    setFaceSturdy = getter.getBlockState(pos.above()).isFaceSturdy(getter, pos.above(), Direction.DOWN);
                case EAST ->
                    setFaceSturdy = getter.getBlockState(pos.west()).isFaceSturdy(getter, pos.west(), Direction.EAST);
                case WEST ->
                    setFaceSturdy = getter.getBlockState(pos.east()).isFaceSturdy(getter, pos.east(), Direction.WEST);
                case SOUTH ->
                    setFaceSturdy = getter.getBlockState(pos.north()).isFaceSturdy(getter, pos.north(), Direction.SOUTH);
                case NORTH ->
                    setFaceSturdy = getter.getBlockState(pos.south()).isFaceSturdy(getter, pos.south(), Direction.NORTH);
            }
            if (setFaceSturdy) {
                if (!state.getValue(CATCH_ALL_DIR)) {
                    break;
                }
            }
        }
        return setFaceSturdy;
    }

    private boolean isSetBelowBlock(BlockState state, LevelReader reader, BlockPos pos) {
        boolean setBelowBlock = true;
        for (Block block : this.blockList(state.getBlock())) {
            if (reader.getBlockState(pos.below()).is(block)) {
                setBelowBlock = true;
                break;
            } else setBelowBlock = false;
        }
        return setBelowBlock;
    }

    private List<String> customType(Block block) {
        ResourceLocation blockId = RegistryInfo.BLOCK.getId(block);
        return new ArrayList<>(CUSTOM_RULE.get(blockId).keySet());
    }

    private List<Block> blockList(Block block) {
        List<Block> blocks = new ArrayList<>();
        ResourceLocation blockId = RegistryInfo.BLOCK.getId(block);
        List<String> belowBlock = CUSTOM_RULE.get(blockId).get("setBelowBlock");
        for (String s : belowBlock) {
            blocks.add(RegistryInfo.BLOCK.getValue(ResourceLocation.tryParse(s)));
        }
        return blocks;
    }

    private List<Direction> directionList(ResourceLocation blockId) {
        List<Direction> directions = new ArrayList<>();
        List<String> faceSturdy = CUSTOM_RULE.get(blockId).get("setFaceSturdy");
        faceSturdy.forEach(face -> directions.add(Direction.byName(face)));
        return directions;
    }

    private boolean canSurviveOn(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
        return blockState.isFaceSturdy(blockGetter, blockPos, Direction.UP) || blockState.is(Blocks.HOPPER);
    }

    public static class SuperBlockBuilder extends BlockBuilder {
        public SuperType surviveType;
        public boolean catchAllDirection;

        public SuperBlockBuilder(ResourceLocation blockId) {
            super(blockId);
            this.surviveType = SuperType.NONE;
            this.catchAllDirection = false;
        }

        @Override
        public Block createObject() {
            return new SuperBlockBase(this);
        }

        public BlockBuilder customSurviveRule(JsonObject json) {
            this.surviveType = SuperType.CUSTOM;
            ruleJsonReader(id, json);
            return this;
        }

        public BlockBuilder surviveType(SuperType type) {
            if (this.surviveType != SuperType.CUSTOM) {
                this.surviveType = type;
            }
            return this;
        }

        public BlockBuilder catchAllDirection(boolean b) {
            this.catchAllDirection = b;
            return this;
        }

        private static void ruleJsonReader(ResourceLocation blockId, JsonObject ruleJson) {
            HashMap<String, List<String>> ruleMap = new HashMap<>();
            JsonElement faceSturdy = ruleJson.get("setFaceSturdy");
            JsonElement belowBlock = ruleJson.get("setBelowBlock");

            if (!faceSturdy.isJsonNull()) {
                JsonArray jsonArray = faceSturdy.getAsJsonArray();
                List<String> faces = new ArrayList<>();
                jsonArray.forEach(element -> faces.add(element.getAsString()));
                ruleMap.put("setFaceSturdy", faces);
            }

            if (!belowBlock.isJsonNull()) {
                JsonArray jsonArray = belowBlock.getAsJsonArray();
                List<String> belowBlocks = new ArrayList<>();
                jsonArray.forEach(element -> belowBlocks.add(element.getAsString()));
                ruleMap.put("setBelowBlock", belowBlocks);
            }

            CUSTOM_RULE.put(blockId, ruleMap);
        }
    }
}
