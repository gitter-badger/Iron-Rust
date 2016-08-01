package com.flatworks.ironrust.world;

import com.flatworks.ironrust.IronRustMod;

import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;

/**
 * @author sjx233
 */
public class WorldProviderRust extends WorldProvider {
    @Override
    public DimensionType getDimensionType() {
        return IronRustMod.DIMENSION_RUST;
    }
    
    @Override
    public boolean canCoordinateBeSpawn(int x, int z) {
        return false;
    }
    
    @Override
    public boolean canRespawnHere() {
        return false;
    }
    
    @Override
    public boolean isSurfaceWorld() {
        return false;
    }
    
    @Override
    public void createBiomeProvider() {
        this.biomeProvider = new BiomeProviderSingle(IronRustMod.BIOME_RUST);
    }
    
    @Override
    public String getWelcomeMessage() {
        return "Entering the Rust World";
    }
    
    @Override
    public String getDepartMessage() {
        return "Leaving the Rust World";
    }
}
