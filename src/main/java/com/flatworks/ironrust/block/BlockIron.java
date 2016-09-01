package com.flatworks.ironrust.block;

import java.util.Random;

import com.flatworks.ironrust.IronRustMod;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSponge;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author sjx233
 */
public class BlockIron extends Block {
    public BlockIron(Material blockMaterialIn, MapColor blockMapColorIn) {
        super(blockMaterialIn, blockMapColorIn);
        this.setTickRandomly(true);
    }
    
    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
        int water = 0;
        int air = 0;
        for (EnumFacing side : EnumFacing.VALUES) {
            BlockPos offsetPos = pos.offset(side);
            IBlockState offsetState = worldIn.getBlockState(offsetPos);
            Material material = offsetState.getMaterial();
            if (material == Material.WATER) {
                water++;
            } else if (offsetState.getProperties().containsKey(BlockSponge.WET)) {
                Boolean wet = offsetState.getValue(BlockSponge.WET);
                if (wet != null && wet) {
                    water++;
                }
            } else if (material == Material.AIR) {
                air++;
            }
        }
        int chance = this.getRustChance(water, air);
        if (worldIn.rand.nextInt(chance) == 0) {
            worldIn.setBlockState(pos, IronRustMod.RUST_BLOCK.getDefaultState());
        }
    }
    
    protected int getRustChance(int water, int air) {
        return IronRustMod.randomTicksNeeded - (water * IronRustMod.waterEffect
                + air * (water >= IronRustMod.minWaterToMakeAirEffective
                        ? IronRustMod.airEffectWithWater : IronRustMod.airEffect));
    }
}
