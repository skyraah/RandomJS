package com.github.skyraah.randomjs;

import com.github.skyraah.randomjs.custom.SuperBlockBuilder;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.registry.RegistryInfo;

public class RandomJSPlugin extends KubeJSPlugin {
    @Override
    public void init() {
        RegistryInfo.BLOCK.addType("super_block", SuperBlockBuilder.class, SuperBlockBuilder:: new);
    }
}
