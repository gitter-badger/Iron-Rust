package com.flatworks.ironrust.item;

import com.flatworks.ironrust.IronRustMod;
import com.flatworks.ironrust.entity.EntityThrownRustPowder;

import net.minecraft.block.BlockDispenser;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

/**
 * @author sjx233
 */
public class ItemRustPowder extends Item {
    public ItemRustPowder() {
        super();
        this.setUnlocalizedName("ironrust.powderRust");
        this.setCreativeTab(CreativeTabs.MATERIALS);
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, new BehaviorProjectileDispense() {
            @Override
            protected IProjectile getProjectileEntity(World worldIn, IPosition position,
                    ItemStack stackIn) {
                return new EntityThrownRustPowder(worldIn, position.getX(), position.getY(),
                        position.getZ());
            }
        });
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn,
            EntityPlayer playerIn, EnumHand hand) {
        if (!playerIn.capabilities.isCreativeMode) {
            --itemStackIn.stackSize;
        }
        
        worldIn.playSound((EntityPlayer) null, playerIn.posX, playerIn.posY, playerIn.posZ,
                SoundEvent.REGISTRY.getObject(IronRustMod.SOUND_ENTITY_RUSTPOWDER_THROW),
                SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        
        if (!worldIn.isRemote) {
            EntityThrownRustPowder entity = new EntityThrownRustPowder(worldIn, playerIn);
            entity.setHeadingFromThrower(playerIn, playerIn.rotationPitch, playerIn.rotationYaw,
                    0.0F, 1.5F, 1.0F);
            worldIn.spawnEntityInWorld(entity);
        }
        
        playerIn.addStat(StatList.getObjectUseStats(this));
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
    }
}
