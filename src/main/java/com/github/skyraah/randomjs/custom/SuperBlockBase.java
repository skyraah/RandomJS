package com.github.skyraah.randomjs.custom;
import com.google.gson.JsonArray;
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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings(value = {"deprecation", "unused"})
public class SuperBlockBase extends BasicBlockJS {
    public static class SuperBlockBuilder extends BlockBuilder {
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
            return new SuperBlockBase(this);
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

    public SuperBlockBase(BlockBuilder i) {
        super(i);
    }

    @Override
    public boolean canSurvive (@NotNull BlockState state, @NotNull LevelReader reader, @NotNull BlockPos pos) {
        BlockPos pos2 = pos.below();
        BlockState state2 = reader.getBlockState(pos2);
        switch (SuperBlockBuilder.surviveType.get(RegistryInfo.BLOCK.getId(state.getBlock()))) {
            case "carpet" -> {
                return !reader.isEmptyBlock(pos.below());
            }
            case "redstone_wire" -> {
                return this.canSurviveOn(reader, pos2, state2);
            }
            case "none" -> {
                return false;
            }
            case ("custom") -> {
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

    private boolean ruleChecker (BlockState state, LevelReader reader, BlockPos pos) {
        boolean isBlowBlock = true;
        boolean isFaceSturdy = true;
        for (String t : customType(state.getBlock())) {
            switch (t) {
                case "setBelowBlock" -> isBlowBlock = this.isSetBelowBlock(state, reader, pos);
                case "setFaceSturdy" -> isFaceSturdy = this.isSetFaceSturdy(reader, pos, state);
            }
        }
        return isBlowBlock && isFaceSturdy;
    }

    private boolean isSetFaceSturdy(BlockGetter getter, BlockPos pos, BlockState state){
        boolean setFaceSturdy = true;
        HashMap<ResourceLocation, Boolean> catchAllMap = SuperBlockBuilder.isCatchAllDirection;
        ResourceLocation b = RegistryInfo.BLOCK.getId(state.getBlock());
        for (Direction d : this.directionList(b)) {
            switch (d){
                 case UP -> setFaceSturdy = getter.getBlockState(pos.below()).isFaceSturdy(getter, pos.below(), Direction.UP);
                case DOWN -> setFaceSturdy = getter.getBlockState(pos.above()).isFaceSturdy(getter, pos.above(), Direction.DOWN);
                case EAST -> setFaceSturdy = getter.getBlockState(pos.west()).isFaceSturdy(getter, pos.west(), Direction.EAST);
                case WEST -> setFaceSturdy = getter.getBlockState(pos.east()).isFaceSturdy(getter, pos.east(), Direction.WEST);
                case SOUTH -> setFaceSturdy = getter.getBlockState(pos.north()).isFaceSturdy(getter, pos.north(), Direction.SOUTH);
                case NORTH -> setFaceSturdy = getter.getBlockState(pos.south()).isFaceSturdy(getter, pos.south(), Direction.NORTH);
            }
            if (setFaceSturdy){
                if (catchAllMap.size() != 0) {
                    if (!catchAllMap.get(b) && catchAllMap.containsKey(b)) {
                        break;
                    }
                }
                else if (catchAllMap.size() == 0) {
                    break;
                }
            }
        }
        return setFaceSturdy;
    }

    private boolean isSetBelowBlock(BlockState state, LevelReader reader, BlockPos pos) {
        boolean setBelowBlock = true;
        for (Block b : this.blockList(state.getBlock())) {
            if (reader.getBlockState(pos.below()).is(b)) {
                setBelowBlock = true;
                break;
            }else setBelowBlock = false;
        }
        return setBelowBlock ;
    }

    private List<String> customType(Block b){
        ResourceLocation blockId = RegistryInfo.BLOCK.getId(b);
        return new ArrayList<>(this.ruleJsonReader(SuperBlockBuilder.ruleJson).get(blockId).keySet());
    }

    private List<Block> blockList(Block b){
        List<Block> l = new ArrayList<>();
        ResourceLocation blockId = RegistryInfo.BLOCK.getId(b);
        List<String> belowBlock = this.ruleJsonReader(SuperBlockBuilder.ruleJson).get(blockId).get("setBelowBlock");
        for (String s : belowBlock){
            l.add(RegistryInfo.BLOCK.getValue(ResourceLocation.tryParse(s)));
        }
        return l;
    }

    private List<Direction> directionList(ResourceLocation b) {
        List<Direction> l = new ArrayList<>();
        List<String> faceSturdy = this.ruleJsonReader(SuperBlockBuilder.ruleJson).get(b).get("setFaceSturdy");
        for (String f : faceSturdy){
            switch (f){
                case ("up") -> l.add(Direction.UP);
                case ("down") -> l.add(Direction.DOWN);
                case ("east") -> l.add(Direction.EAST);
                case ("west") -> l.add(Direction.WEST);
                case ("south") -> l.add(Direction.SOUTH);
                case ("north") -> l.add(Direction.NORTH);
            }
        }
        return l;
    }

    private HashMap<ResourceLocation, HashMap<String, List<String>>> ruleJsonReader (HashMap<ResourceLocation, JsonObject> map){
        HashMap<ResourceLocation, HashMap<String, List<String>>> r = new HashMap<>();
        HashMap<String, List<String>> m = new HashMap<>();
        for (Map.Entry<ResourceLocation, JsonObject> entry : map.entrySet()) {
            JsonArray faceSturdy = entry.getValue().getAsJsonArray("setFaceSturdy");
            JsonArray belowBlock = entry.getValue().getAsJsonArray("setBelowBlock");
            if (faceSturdy != null) {
                List<String> l = new ArrayList<>();
                for (int i = 0; i < faceSturdy.size(); i++){
                    l.add(faceSturdy.get(i).getAsString());
                }
                m.put("setFaceSturdy", l);
            }
            if (belowBlock != null) {
                List<String> l = new ArrayList<>();
                for (int i = 0; i < belowBlock.size(); i++){
                    l.add(belowBlock.get(i).getAsString());
                }
                m.put("setBelowBlock", l);
            }
            r.put(entry.getKey(), m);
        }
        return  r;
    }

    private boolean canSurviveOn (BlockGetter blockGetter, BlockPos blockPos, BlockState blockState){
        return blockState.isFaceSturdy(blockGetter, blockPos, Direction.UP) || blockState.is(Blocks.HOPPER);
    }
}
