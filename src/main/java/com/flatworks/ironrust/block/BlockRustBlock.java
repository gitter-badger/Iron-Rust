package com.flatworks.ironrust.block;

import java.util.Random;

import com.flatworks.ironrust.IronRustMod;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

/**
 * @author sjx233
 */
public class BlockRustBlock extends Block {
    public BlockRustBlock() {
        super(Material.IRON, MapColor.BROWN);
        this.setHardness(1.0F);
        this.setResistance(7.0F);
        this.setHarvestLevel("pickaxe", 0);
        this.setUnlocalizedName("ironrust.blockIronRust");
        this.setSoundType(SoundType.METAL);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }
    
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return IronRustMod.RUST_POWDER;
    }
    
    @Override
    public int quantityDropped(Random random) {
        return 2 + random.nextInt(4);
    }
    
    @Override
    public int quantityDroppedWithBonus(int fortune, Random random) {
        if (fortune > 0) {
            int i = this.quantityDropped(random) + random.nextInt(fortune + 2);
            return i < 9 ? i : 9;
        }
        return this.quantityDropped(random);
    }
}
