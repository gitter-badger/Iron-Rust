package com.flatworks.ironrust.world.gen;

import java.util.Random;

import com.flatworks.ironrust.IronRustMod;

import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockStateMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenAbandonedWells extends WorldGenerator {
    private static final BlockStateMatcher IS_VALID =
            BlockStateMatcher.forBlock(IronRustMod.RUST_GRASS);
    private static final IBlockState RUST_BLOCK = IronRustMod.RUST_BLOCK.getDefaultState();
    private static final IBlockState WATER = Blocks.FLOWING_WATER.getDefaultState();
    
    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        while (worldIn.isAirBlock(position) && position.getY() > 2) {
            position = position.down();
        }
        
        if (!IS_VALID.apply(worldIn.getBlockState(position))) {
            return false;
        }
        
        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                if (worldIn.isAirBlock(position.add(i, -1, j))
                        && worldIn.isAirBlock(position.add(i, -2, j))) {
                    return false;
                }
            }
        }
        
        for (int l = -1; l <= 0; ++l) {
            for (int l1 = -2; l1 <= 2; ++l1) {
                for (int k = -2; k <= 2; ++k) {
                    placeBlock(worldIn, rand, position.add(l1, l, k), RUST_BLOCK);
                }
            }
        }
        
        worldIn.setBlockState(position, WATER, 2);
        
        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            worldIn.setBlockState(position.offset(enumfacing), WATER, 2);
        }
        
        for (int i1 = -2; i1 <= 2; ++i1) {
            for (int i2 = -2; i2 <= 2; ++i2) {
                if (i1 == -2 || i1 == 2 || i2 == -2 || i2 == 2) {
                    placeBlock(worldIn, rand, position.add(i1, 1, i2), RUST_BLOCK);
                }
            }
        }
        
        for (int j1 = -1; j1 <= 1; ++j1) {
            for (int j2 = -1; j2 <= 1; ++j2) {
                if (j1 == 0 && j2 == 0) {
                    placeBlock(worldIn, rand, position.add(j1, 4, j2), RUST_BLOCK);
                }
            }
        }
        
        for (int k1 = 1; k1 <= 3; ++k1) {
            placeBlock(worldIn, rand, position.add(-1, k1, -1), RUST_BLOCK);
            placeBlock(worldIn, rand, position.add(-1, k1, 1), RUST_BLOCK);
            placeBlock(worldIn, rand, position.add(1, k1, -1), RUST_BLOCK);
            placeBlock(worldIn, rand, position.add(1, k1, 1), RUST_BLOCK);
        }
        
        return true;
    }
    
    private static void placeBlock(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        if (rand.nextInt(10) != 0) {
            worldIn.setBlockState(pos, state, 2);
        }
    }
}
