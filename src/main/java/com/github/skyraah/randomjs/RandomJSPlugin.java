package com.github.skyraah.randomjs;

import com.github.skyraah.randomjs.block.custom.RevelationBlockBuilder;
import com.github.skyraah.randomjs.block.custom.SuperBlockBase;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.registry.RegistryInfo;

public class RandomJSPlugin extends KubeJSPlugin {
    @Override
    public void init() {
        RegistryInfo.BLOCK.addType(
                "super_block",
                SuperBlockBase.SuperBlockBuilder.class,
                SuperBlockBase.SuperBlockBuilder:: new
        );
        RegistryInfo.BLOCK.addType(
                "revelation_block",
                RevelationBlockBuilder.class,
                RevelationBlockBuilder:: new
        );
    }
}
