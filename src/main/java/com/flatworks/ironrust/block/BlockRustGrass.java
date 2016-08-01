package com.flatworks.ironrust.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.flatworks.ironrust.IronRustMod;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

/**
 * @author sjx233
 */
public class BlockRustGrass extends Block {
    public static final PropertyBool SNOWY = PropertyBool.create("snowy");
    
    public BlockRustGrass() {
        super(Material.GROUND);
        this.setHardness(0.6f);
        this.setUnlocalizedName("ironrust.blockGrassRust");
        this.setSoundType(SoundType.GROUND);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }
    
    @Override
    @Deprecated
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        Block block = worldIn.getBlockState(pos.up()).getBlock();
        return state.withProperty(SNOWY, block == Blocks.SNOW || block == Blocks.SNOW_LAYER);
    }
    
    @Override
    @Deprecated
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState();
    }
    
    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }
    
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, SNOWY);
    }
    
    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state,
            int fortune) {
        List<ItemStack> ret = new ArrayList<ItemStack>();
        Random rand = world instanceof World ? ((World) world).rand : new Random();
        ret.add(new ItemStack(this.getItemDropped(state, rand, fortune),
                this.quantityDropped(state, fortune, rand), this.damageDropped(state)));
        if (rand.nextInt(fortune > 0 ? Math.max(40 - (3 << fortune), 10) : 40) == 0) {
            ret.add(new ItemStack(IronRustMod.RUST_POWDER));
        } else if (rand.nextInt(fortune > 0 ? Math.max(400 - (15 << fortune), 80) : 400) == 0) {
            ret.add(new ItemStack(IronRustMod.RUST_APPLE));
        }
        return ret;
    }
    
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(Blocks.DIRT);
    }
    
    @Override
    public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos,
            EnumFacing direction, IPlantable plantable) {
        return plantable instanceof BlockBush;
    }
}
