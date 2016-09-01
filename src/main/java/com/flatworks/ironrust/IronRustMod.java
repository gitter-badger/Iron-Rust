package com.flatworks.ironrust;

import static com.flatworks.ironrust.IronRustMod.MODID;
import static com.flatworks.ironrust.IronRustMod.NAME;
import static com.flatworks.ironrust.IronRustMod.VERSION;

import java.util.HashMap;

import org.apache.logging.log4j.Logger;

import com.flatworks.ironrust.block.BlockRust;
import com.flatworks.ironrust.block.BlockRustGrass;
import com.flatworks.ironrust.block.BlockRustPortal;
import com.flatworks.ironrust.item.*;
import com.flatworks.ironrust.potion.PotionRust;
import com.flatworks.ironrust.world.BiomeRust;
import com.flatworks.ironrust.world.TeleporterRust;
import com.flatworks.ironrust.world.WorldProviderRust;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.potion.Potion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

/**
 * @author sjx233
 */
@Mod(modid = MODID, name = NAME, version = VERSION,
        guiFactory = "com.flatworks.ironrust.client.gui.IronRustModGuiFactory",
        acceptedMinecraftVersions = "1.10.2")
public class IronRustMod {
    public static final String MODID = "ironrust";
    public static final String NAME = "Iron Rust";
    public static final String VERSION = "<build_version>";
    
    @Mod.Instance(MODID)
    public static IronRustMod instance;
    @SidedProxy(clientSide = "com.flatworks.ironrust.client.ClientProxy",
            serverSide = "com.flatworks.ironrust.CommonProxy", modId = MODID)
    public static CommonProxy proxy;
    public static Logger logger;
    public static Configuration config;
    
    public static int randomTicksNeeded = 200;
    public static int waterEffect = 25;
    public static int airEffect = 5;
    public static int airEffectWithWater = 35;
    public static int minWaterToMakeAirEffective = 2;
    
    public static final ToolMaterial TOOL_MATERIAL_RUST =
            EnumHelper.addToolMaterial("RUST", 1, 46, 4f, 1f, 56);
    public static final ArmorMaterial ARMOR_MATERIAL_RUST =
            EnumHelper.addArmorMaterial("RUST", "ironrust:rust", 3, new int[] { 1, 3, 4, 2 }, 30,
                    SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0f);
    public static final Potion POTION_RUST = new PotionRust();
    
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
    public static final Item RUST_APPLE = new ItemRustApple();
    public static final Block RUST_BLOCK = new BlockRust();
    public static final Block RUST_GRASS = new BlockRustGrass();
    public static final Block RUST_PORTAL = new BlockRustPortal();
    
    public static final Achievement ACHIEVEMENT_KILL_RUSTY_COW =
            new Achievement("achievement.ironrust.killRustyCow", "ironrust.killRustyCow", 10, -2,
                    RUST_POWDER, AchievementList.KILL_ENEMY).setSpecial();
    public static final SoundEvent SOUND_ENTITY_RUSTPOWDER_THROW =
            new SoundEvent(new ResourceLocation(MODID, "entity.rustpowder.throw"));
    public static final ResourceLocation LOOT_TABLE_ENTITIES_RUSTY_COW =
            new ResourceLocation(MODID, "entities/rusty_cow");
    public static final Biome BIOME_RUST = new BiomeRust();
    public static final DimensionType DIMENSION_RUST = DimensionType.register("Rust", "_rustworld",
            DimensionManager.getNextFreeDimId(), WorldProviderRust.class, false);
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }
    
    @EventHandler
    public void serverStarted(@SuppressWarnings("unused") FMLServerStartedEvent event) {
        TeleporterRust.inPortal.clear();
        TeleporterRust.lastPortalPos.clear();
        TeleporterRust.lastPortalVec.clear();
        TeleporterRust.teleportDirection.clear();
        TeleporterRust.timeUntilPortal.clear();
    }
    
    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent event) {
        if (event.getModID() == MODID) {
            this.updateConfig();
        }
    }
    
    public void updateConfig() {
        proxy.updateConfig();
    }
    
    @SubscribeEvent
    public void onWorldTick(WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.START && event.side.isServer()) {
            WorldServer world = (WorldServer) event.world;
            int size = world.loadedEntityList.size();
            for (int i = 0; i < size - 1; i++) {
                Entity entity = world.loadedEntityList.get(i);
                boolean inPortal = IronRustMod.get(TeleporterRust.inPortal, entity, false);
                int timeUntilPortal = IronRustMod.get(TeleporterRust.timeUntilPortal, entity, 0);
                int portalCounter = IronRustMod.get(TeleporterRust.portalCounter, entity, 0);
                if (!entity.isDead) {
                    if (inPortal) {
                        MinecraftServer minecraftserver = world.getMinecraftServer();
                        if (minecraftserver.getAllowNether()) {
                            if (!entity.isRiding()) {
                                int time = entity.getMaxInPortalTime();
                                if (portalCounter++ >= time) {
                                    portalCounter = time;
                                    timeUntilPortal = entity.getPortalCooldown();
                                    PlayerList list = world.getMinecraftServer().getPlayerList();
                                    MinecraftServer server = world.getMinecraftServer();
                                    int dimensionId =
                                            entity.dimension == IronRustMod.DIMENSION_RUST.getId()
                                                    ? 0 : IronRustMod.DIMENSION_RUST.getId();
                                    Teleporter teleporter = new TeleporterRust(
                                            server.worldServerForDimension(dimensionId));
                                    if (entity instanceof EntityPlayerMP) {
                                        list.transferPlayerToDimension((EntityPlayerMP) entity,
                                                dimensionId, teleporter);
                                    } else {
                                        list.transferEntityToWorld(entity, dimensionId, world,
                                                server.worldServerForDimension(dimensionId),
                                                teleporter);
                                    }
                                }
                            }
                            inPortal = false;
                        }
                    } else {
                        if (portalCounter > 0) {
                            portalCounter -= 4;
                        }
                        
                        if (portalCounter < 0) {
                            portalCounter = 0;
                        }
                    }
                    
                    if (timeUntilPortal > 0) {
                        timeUntilPortal--;
                    }
                }
                IronRustMod.put(TeleporterRust.inPortal, entity, inPortal);
                IronRustMod.put(TeleporterRust.timeUntilPortal, entity, timeUntilPortal);
                IronRustMod.put(TeleporterRust.portalCounter, entity, portalCounter);
            }
        }
    }
    
    public static <K, V> V get(HashMap<K, V> map, K key, V dflt) {
        V value = map.get(key);
        return value == null ? dflt : value;
    }
    
    public static <K, V> V put(HashMap<K, V> map, K key, V value) {
        return map.put(key, value);
    }
}
