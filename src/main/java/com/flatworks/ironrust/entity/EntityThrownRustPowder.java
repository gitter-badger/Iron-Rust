package com.flatworks.ironrust.entity;

import com.flatworks.ironrust.IronRustMod;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

/**
 * @author sjx233
 */
public class EntityThrownRustPowder extends EntityThrowable {
    public EntityThrownRustPowder(World worldIn) {
        super(worldIn);
    }
    
    public EntityThrownRustPowder(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn, throwerIn);
    }
    
    public EntityThrownRustPowder(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }
    
    @Override
    protected void onImpact(RayTraceResult result) {
        if (result.entityHit != null) {
            result.entityHit
                    .attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 8f);
        }
        
        for (int j = 0; j < 8; ++j) {
            this.getEntityWorld().spawnParticle(EnumParticleTypes.ITEM_CRACK, this.posX, this.posY,
                    this.posZ, 0.0D, 0.0D, 0.0D,
                    new int[] { Item.getIdFromItem(IronRustMod.RUST_POWDER) });
        }
        
        if (!this.getEntityWorld().isRemote) {
            this.setDead();
        }
    }
}
