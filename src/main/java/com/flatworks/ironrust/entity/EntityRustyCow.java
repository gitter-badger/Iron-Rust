package com.flatworks.ironrust.entity;

import java.util.Calendar;

import javax.annotation.Nullable;

import com.flatworks.ironrust.IronRustMod;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

/**
 * @author sjx233
 */
public class EntityRustyCow extends EntityMob implements IRangedAttackMob {
    public EntityRustyCow(World worldIn) {
        super(worldIn);
        this.setSize(0.9F, 1.4F);
    }
    
    @Override
    protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackRanged(this, 1.25d, 100, 16f));
        this.tasks.addTask(3, new EntityAIWander(this, 1d));
        this.tasks.addTask(4, new EntityAIWatchClosest(this, EntityPlayer.class, 6f));
        this.tasks.addTask(4, new EntityAILookIdle(this));
        this.targetTasks.addTask(1,
                new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true));
    }
    
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(50d);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2d);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5d);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(2d);
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ZOMBIE_AMBIENT;
    }
    
    @Override
    protected SoundEvent getHurtSound() {
        return SoundEvents.ENTITY_ZOMBIE_HURT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ZOMBIE_DEATH;
    }
    
    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(SoundEvents.ENTITY_ZOMBIE_STEP, 0.15F, 1.0F);
    }
    
    @Override
    @Nullable
    protected ResourceLocation getLootTable() {
        return LootTableList.CHESTS_END_CITY_TREASURE;
    }
    
    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distance) {
        EntityThrownRustPowder entity = new EntityThrownRustPowder(this.worldObj, this);
        double dx = target.posX - this.posX;
        double dy = target.posY + target.getEyeHeight() - 1.1d - entity.posY;
        double dz = target.posZ - this.posZ;
        float f = MathHelper.sqrt_double(dx * dx + dz * dz) * 0.2f;
        entity.setThrowableHeading(dx, dy + f, dz, 1.6f, 12f);
        this.playSound(SoundEvent.REGISTRY.getObject(IronRustMod.SOUND_ENTITY_RUSTPOWDER_THROW), 1f,
                1f / (this.getRNG().nextFloat() * 0.4f + 0.8f));
        this.worldObj.spawnEntityInWorld(entity);
    }
    
    @Override
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
        super.setEquipmentBasedOnDifficulty(difficulty);
        
        if (this.rand.nextFloat() < (this.worldObj.getDifficulty() == EnumDifficulty.HARD ? 0.1F
                : 0.02F)) {
            int i = this.rand.nextInt(3);
            
            if (i == 0) {
                this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND,
                        new ItemStack(Items.DIAMOND_SWORD));
            } else {
                this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND,
                        new ItemStack(Items.DIAMOND_SHOVEL));
            }
        }
    }
    
    @Override
    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty,
            @Nullable IEntityLivingData livingdata) {
        livingdata = super.onInitialSpawn(difficulty, livingdata);
        this.setEquipmentBasedOnDifficulty(difficulty);
        this.setEnchantmentBasedOnDifficulty(difficulty);
        
        if (this.getItemStackFromSlot(EntityEquipmentSlot.HEAD) == null) {
            Calendar calendar = this.worldObj.getCurrentDate();
            
            if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31
                    && this.rand.nextFloat() < 0.25F) {
                this.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(
                        this.rand.nextFloat() < 0.1F ? Blocks.LIT_PUMPKIN : Blocks.PUMPKIN));
                this.inventoryArmorDropChances[EntityEquipmentSlot.HEAD.getIndex()] = 0.0F;
            }
        }
        
        this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).applyModifier(
                new AttributeModifier("Random spawn bonus", this.rand.nextDouble() * 0.05D, 0));
        
        return livingdata;
    }
    
    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        Entity causeEntity = cause.getEntity();
        Entity sourceEntity = cause.getSourceOfDamage();
        if (causeEntity instanceof EntityPlayerMP
                && sourceEntity instanceof EntityThrownRustPowder) {
            ((EntityPlayerMP) causeEntity).addStat(IronRustMod.ACHIEVEMENT_KILL_RUSTY_COW);
        }
    }
}
