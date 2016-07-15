package com.flatworks.ironrust;

// Import for @Mod annotation
import static com.flatworks.ironrust.IronRustMod.MODID;
import static com.flatworks.ironrust.IronRustMod.NAME;
import static com.flatworks.ironrust.IronRustMod.VERSION;

import com.flatworks.ironrust.block.BlockRustBlock;
import com.flatworks.ironrust.item.*;

import net.minecraft.block.Block;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * @author sjx233
 */
@Mod(modid = MODID, name = NAME, version = VERSION, acceptedMinecraftVersions = "1.9.4")
public class IronRustMod {
    public static final String MODID = "ironrust";
    public static final String NAME = "Iron Rust";
    public static final String VERSION = "${version}";
    
    @Instance(MODID)
    public static IronRustMod instance;
    @SidedProxy(clientSide = "com.flatworks.ironrust.ClientProxy",
            serverSide = "com.flatworks.ironrust.CommonProxy", modId = MODID)
    public static CommonProxy proxy;
    
    public static final ToolMaterial TOOL_MATERIAL_RUST =
            EnumHelper.addToolMaterial("RUST", 1, 46, 4f, 1f, 56);
    public static final ArmorMaterial ARMOR_MATERIAL_RUST =
            EnumHelper.addArmorMaterial("RUST", "ironrust:rust", 3, new int[] { 1, 3, 4, 2 }, 30,
                    SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0f);
    
    public static final Item RUST_POWDER = new ItemRustPowder();
    public static final Item RUST_SWORD = new ItemRustSword();
    public static final Item RUST_SHOVEL = new ItemRustSpade();
    public static final Item RUST_PICKAXE = new ItemRustPickaxe();
    public static final Item RUST_AXE = new ItemRustAxe();
    public static final Item RUST_HOE = new ItemRustHoe();
    public static final Item RUST_HELMET = new ItemRustHelmet();
    public static final Item RUST_CHESTPLATE = new ItemRustChestplate();
    public static final Item RUST_LEGGINGS = new ItemRustLeggings();
    public static final Item RUST_BOOTS = new ItemRustBoots();
    public static final Block RUST_BLOCK = new BlockRustBlock();
    
    public static final Achievement ACHIEVEMENT_KILL_RUSTY_COW =
            new Achievement("achievement.ironrust.killRustyCow", "ironrust.killRustyCow", 10, -2,
                    RUST_POWDER, AchievementList.KILL_ENEMY).setSpecial();
    public static final SoundEvent SOUND_ENTITY_RUSTPOWDER_THROW =
            new SoundEvent(new ResourceLocation(MODID, "entity.rustpowder.throw"));
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }
}
