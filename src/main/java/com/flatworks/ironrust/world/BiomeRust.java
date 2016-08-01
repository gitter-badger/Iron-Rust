package com.flatworks.ironrust.world;

import java.util.Random;

import com.flatworks.ironrust.IronRustMod;
import com.flatworks.ironrust.entity.EntityRustyCow;
import com.flatworks.ironrust.world.gen.WorldGenAbandonedWells;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenBlockBlob;
import net.minecraft.world.gen.feature.WorldGenFossils;

/**
 * @author sjx233
 */
public class BiomeRust extends Biome {
    private static final WorldGenBlockBlob RUST_GENERATOR =
            new WorldGenBlockBlob(IronRustMod.RUST_BLOCK, 0);
    private static final WorldGenAbandonedWells WELLS_GENERATOR = new WorldGenAbandonedWells();
    private static final WorldGenFossils FOSSILS_GENERATOR = new WorldGenFossils();
    
    public BiomeRust() {
        super(new BiomeProperties("Rust").setBaseHeight(0.125F).setHeightVariation(0.05F)
                .setTemperature(0.8F).setRainDisabled().setWaterColor(0x6d5aff));
        this.topBlock = IronRustMod.RUST_GRASS.getDefaultState();
        this.spawnableMonsterList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableWaterCreatureList.clear();
        this.spawnableMonsterList.add(new Biome.SpawnListEntry(EntityRustyCow.class, 400, 4, 4));
        this.theBiomeDecorator.treesPerChunk = 0;
        this.theBiomeDecorator.flowersPerChunk = 0;
        this.theBiomeDecorator.grassPerChunk = 0;
        this.theBiomeDecorator.deadBushPerChunk = 20;
    }
    
    @Override
    public void decorate(World worldIn, Random rand, BlockPos pos) {
        super.decorate(worldIn, rand, pos);
        
        if (rand.nextInt(20) == 0) {
            int x = rand.nextInt(16) + 8;
            int y = rand.nextInt(16) + 8;
            BlockPos blockpos = worldIn.getHeight(pos.add(x, 0, y));
            RUST_GENERATOR.generate(worldIn, rand, blockpos);
        }
        
        if (rand.nextInt(1000) == 0) {
            int x = rand.nextInt(16) + 8;
            int y = rand.nextInt(16) + 8;
            BlockPos blockpos = worldIn.getHeight(pos.add(x, 0, y)).up();
            WELLS_GENERATOR.generate(worldIn, rand, blockpos);
        }
        
        if (rand.nextInt(64) == 0) {
            FOSSILS_GENERATOR.generate(worldIn, rand, pos);
        }
    }
}
