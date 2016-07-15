package com.flatworks.ironrust;

import static com.flatworks.ironrust.IronRustMod.*;

import java.util.Iterator;

import com.flatworks.ironrust.entity.EntityRustyCow;
import com.flatworks.ironrust.entity.EntityThrownRustPowder;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author sjx233
 */
public class CommonProxy {
    private static void registerItem(Item item, String name) {
        GameRegistry.register(item.setRegistryName(name));
    }
    
    private static void registerItem(Block block, String name) {
        GameRegistry.register(block.setRegistryName(name));
        GameRegistry.register(new ItemBlock(block).setRegistryName(name));
    }
    
    private static int nextEntityId = 0;
    
    private static void registerEntity(Class<? extends Entity> entityClass, String entityName,
            int trackingRange, int updateFrequency, boolean sendsVelocityUpdates) {
        EntityRegistry.registerModEntity(entityClass, entityName, nextEntityId++, instance,
                trackingRange, updateFrequency, sendsVelocityUpdates);
    }
    
    private static void registerEntity(Class<? extends Entity> entityClass, String entityName,
            int trackingRange, int updateFrequency, boolean sendsVelocityUpdates, int eggPrimary,
            int eggSecondary) {
        EntityRegistry.registerModEntity(entityClass, entityName, nextEntityId++, instance,
                trackingRange, updateFrequency, sendsVelocityUpdates, eggPrimary, eggSecondary);
    }
    
    private static void registerSpawn(Class<? extends EntityLiving> entityClass, int weight,
            int min, int max, EnumCreatureType type, Biome[] biomes) {
        EntityRegistry.addSpawn(entityClass, weight, min, max, type, biomes);
    }
    
    private static void registerSpawn(Class<? extends EntityLiving> entityClass, int weight,
            int min, int max, EnumCreatureType type) {
        registerSpawn(entityClass, weight, min, max, type, allBiomes());
    }
    
    private static Biome[] allBiomesCache = null;
    
    private static Biome[] allBiomes() {
        if (allBiomesCache == null) {
            Biome[] result = new Biome[Biome.REGISTRY.getKeys().size()];
            Iterator<Biome> it = Biome.REGISTRY.iterator();
            int i = 0;
            while (it.hasNext()) {
                result[i++] = it.next();
            }
            allBiomesCache = result;
        }
        return allBiomesCache;
    }
    
    private static void registerAchievement(Achievement achievement) {
        achievement.registerStat();
    }
    
    private static int nextSoundId = 8192;
    
    private static void registerSound(ResourceLocation soundId) {
        SoundEvent.REGISTRY.register(nextSoundId++, soundId, new SoundEvent(soundId));
    }
    
    private static void addRecipe(Item output, Object... params) {
        GameRegistry.addRecipe(new ItemStack(output), params);
    }
    
    private static void addRecipe(Block output, Object... params) {
        GameRegistry.addRecipe(new ItemStack(output), params);
    }
    
    private static void addSmelting(Item input, Item output, float xp) {
        GameRegistry.addSmelting(input, new ItemStack(output), xp);
    }
    
    private static void addSmelting(Block input, Block output, float xp) {
        GameRegistry.addSmelting(input, new ItemStack(output), xp);
    }
    
    public void preInit(@SuppressWarnings("unused") FMLPreInitializationEvent event) {
        TOOL_MATERIAL_RUST.setRepairItem(new ItemStack(RUST_POWDER, 1,
                net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE));
        ARMOR_MATERIAL_RUST.customCraftingMaterial = RUST_POWDER;
        registerItem(RUST_POWDER, "rust_powder");
        registerItem(RUST_SWORD, "rust_sword");
        registerItem(RUST_SHOVEL, "rust_shovel");
        registerItem(RUST_PICKAXE, "rust_pickaxe");
        registerItem(RUST_AXE, "rust_axe");
        registerItem(RUST_HOE, "rust_hoe");
        registerItem(RUST_HELMET, "rust_helmet");
        registerItem(RUST_CHESTPLATE, "rust_chestplate");
        registerItem(RUST_LEGGINGS, "rust_leggings");
        registerItem(RUST_BOOTS, "rust_boots");
        registerItem(RUST_BLOCK, "rust_block");
        registerEntity(EntityRustyCow.class, "RustyCow", 64, 3, true, 0x240d00, 0x6d5a31);
        registerEntity(EntityThrownRustPowder.class, "ThrownRustPowder", 64, 10, true);
        registerSpawn(EntityRustyCow.class, 8, 40, 40, EnumCreatureType.MONSTER);
        registerAchievement(ACHIEVEMENT_KILL_RUSTY_COW);
        registerSound(SOUND_ENTITY_RUSTPOWDER_THROW);
    }
    
    public void init(@SuppressWarnings("unused") FMLInitializationEvent event) {
        addRecipe(RUST_BLOCK, "###", "###", "###", '#', RUST_POWDER);
        addRecipe(RUST_SWORD, "#", "#", "|", '#', RUST_POWDER, '|', Items.STICK);
        addRecipe(RUST_SHOVEL, "#", "|", "|", '#', RUST_POWDER, '|', Items.STICK);
        addRecipe(RUST_PICKAXE, "###", " | ", " | ", '#', RUST_POWDER, '|', Items.STICK);
        addRecipe(RUST_AXE, "##", "#|", " |", '#', RUST_POWDER, '|', Items.STICK);
        addRecipe(RUST_HOE, "##", " |", " |", '#', RUST_POWDER, '|', Items.STICK);
        addRecipe(RUST_HELMET, "###", "# #", '#', RUST_POWDER);
        addRecipe(RUST_CHESTPLATE, "# #", "###", "###", '#', RUST_POWDER);
        addRecipe(RUST_LEGGINGS, "###", "# #", "# #", '#', RUST_POWDER);
        addRecipe(RUST_BOOTS, "# #", "# #", '#', RUST_POWDER);
        addSmelting(RUST_POWDER, Items.IRON_INGOT, 0.15f);
        addSmelting(RUST_BLOCK, Blocks.IRON_BLOCK, 1.5f);
    }
}