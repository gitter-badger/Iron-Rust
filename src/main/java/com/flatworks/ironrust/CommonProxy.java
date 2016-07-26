package com.flatworks.ironrust;

import static com.flatworks.ironrust.IronRustMod.*;
import static net.minecraft.init.Blocks.IRON_BLOCK;
import static net.minecraft.init.Items.APPLE;
import static net.minecraft.init.Items.GLOWSTONE_DUST;
import static net.minecraft.init.Items.IRON_INGOT;
import static net.minecraft.init.Items.REDSTONE;
import static net.minecraft.init.Items.STICK;

import java.util.Iterator;

import com.flatworks.ironrust.entity.EntityRustyCow;
import com.flatworks.ironrust.entity.EntityThrownRustPowder;
import com.flatworks.ironrust.potion.PotionBrewingRecipe;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.stats.Achievement;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author sjx233
 */
public class CommonProxy {
    private static int configInt(String name, int dflt, int min, int max, String desc) {
        return config.getInt(name, Configuration.CATEGORY_GENERAL, dflt, min, max, desc,
                "config.ironrust." + name);
    }
    
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
    
    private static void registerSound(SoundEvent sound, String name) {
        GameRegistry.register(sound.setRegistryName(name));
    }
    
    private static void registerPotion(Potion potion, String name, int duration, Item ingredient) {
        GameRegistry.register(potion.setRegistryName(name));
        PotionType normalType =
                new PotionType(name, new PotionEffect(potion, duration)).setRegistryName(name);
        GameRegistry.register(normalType);
        PotionType longType = new PotionType(name, new PotionEffect(potion, duration * 2))
                .setRegistryName("long_" + name);
        GameRegistry.register(longType);
        PotionType strongType = new PotionType(name, new PotionEffect(potion, duration / 2, 1))
                .setRegistryName("strong_" + name);
        GameRegistry.register(strongType);
        BrewingRecipeRegistry.addRecipe(new PotionBrewingRecipe(normalType, REDSTONE, longType));
        BrewingRecipeRegistry
                .addRecipe(new PotionBrewingRecipe(normalType, GLOWSTONE_DUST, strongType));
        BrewingRecipeRegistry
                .addRecipe(new PotionBrewingRecipe(PotionTypes.AWKWARD, ingredient, normalType));
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
    
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(instance);
        
        config = new Configuration(event.getSuggestedConfigurationFile());
        randomTicksNeeded = configInt("randomTicksNeeded", 200, 1, Integer.MAX_VALUE,
                "How many random ticks are needed for iron to rust?");
        waterEffect =
                configInt("waterEffect", 25, 0, Integer.MAX_VALUE, "Water effect multiplier.");
        airEffect = configInt("airEffect", 5, 0, Integer.MAX_VALUE,
                "Air effect multiplier without enough water.");
        airEffectWithWater = configInt("airEffectWithWater", 35, 0, Integer.MAX_VALUE,
                "Air effect multiplier with enough water.");
        minWaterToMakeAirEffective =
                configInt("minWaterToMakeAirEffective", 2, 0, 5, "How much water is enough?");
        config.save();
        
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
        registerItem(RUST_APPLE, "rust_apple");
        registerItem(RUST_BLOCK, "rust_block");
        registerEntity(EntityRustyCow.class, "RustyCow", 64, 3, true, 0x240d00, 0x6d5a31);
        registerEntity(EntityThrownRustPowder.class, "ThrownRustPowder", 64, 10, true);
        registerSpawn(EntityRustyCow.class, 8, 40, 40, EnumCreatureType.MONSTER);
        registerAchievement(ACHIEVEMENT_KILL_RUSTY_COW);
        registerSound(SOUND_ENTITY_RUSTPOWDER_THROW, "entity.rustpowder.throw");
        registerPotion(POTION_RUST, "rust", 900, RUST_POWDER);
    }
    
    public void init(@SuppressWarnings("unused") FMLInitializationEvent event) {
        addRecipe(RUST_BLOCK, "###", "###", "###", '#', RUST_POWDER);
        addRecipe(RUST_SWORD, "#", "#", "|", '#', RUST_POWDER, '|', STICK);
        addRecipe(RUST_SHOVEL, "#", "|", "|", '#', RUST_POWDER, '|', STICK);
        addRecipe(RUST_PICKAXE, "###", " | ", " | ", '#', RUST_POWDER, '|', STICK);
        addRecipe(RUST_AXE, "##", "#|", " |", '#', RUST_POWDER, '|', STICK);
        addRecipe(RUST_HOE, "##", " |", " |", '#', RUST_POWDER, '|', STICK);
        addRecipe(RUST_HELMET, "###", "# #", '#', RUST_POWDER);
        addRecipe(RUST_CHESTPLATE, "# #", "###", "###", '#', RUST_POWDER);
        addRecipe(RUST_LEGGINGS, "###", "# #", "# #", '#', RUST_POWDER);
        addRecipe(RUST_BOOTS, "# #", "# #", '#', RUST_POWDER);
        addRecipe(RUST_APPLE, "###", "#X#", "###", '#', RUST_POWDER, 'X', APPLE);
        addSmelting(RUST_POWDER, IRON_INGOT, 0.15f);
        addSmelting(RUST_BLOCK, IRON_BLOCK, 1.5f);
    }
}
