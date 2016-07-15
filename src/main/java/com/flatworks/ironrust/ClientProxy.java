package com.flatworks.ironrust;

import static com.flatworks.ironrust.IronRustMod.*;

import com.flatworks.ironrust.entity.EntityRustyCow;
import com.flatworks.ironrust.entity.EntityThrownRustPowder;
import com.flatworks.ironrust.entity.RenderFactoryRustyCow;
import com.flatworks.ironrust.entity.RenderFactoryThrownRustPowder;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * @author sjx233
 */
public class ClientProxy extends CommonProxy {
    private static <T extends Entity> void registerEntityRender(Class<T> entityClass,
            IRenderFactory<? super T> renderFactory) {
        RenderingRegistry.<T>registerEntityRenderingHandler(entityClass, renderFactory);
    }
    
    private static void registerModel(Item item, int meta, String modelId) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta,
                new ModelResourceLocation(MODID + ":" + modelId, "inventory"));
    }
    
    private static void registerModel(Block block, int meta, String id) {
        registerModel(Item.getItemFromBlock(block), meta, id);
    }
    
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        registerEntityRender(EntityThrownRustPowder.class, new RenderFactoryThrownRustPowder());
        registerEntityRender(EntityRustyCow.class, new RenderFactoryRustyCow());
    }
    
    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        registerModel(RUST_POWDER, 0, "rust_powder");
        registerModel(RUST_SWORD, 0, "rust_sword");
        registerModel(RUST_SHOVEL, 0, "rust_shovel");
        registerModel(RUST_PICKAXE, 0, "rust_pickaxe");
        registerModel(RUST_AXE, 0, "rust_axe");
        registerModel(RUST_HOE, 0, "rust_hoe");
        registerModel(RUST_HELMET, 0, "rust_helmet");
        registerModel(RUST_CHESTPLATE, 0, "rust_chestplate");
        registerModel(RUST_LEGGINGS, 0, "rust_leggings");
        registerModel(RUST_BOOTS, 0, "rust_boots");
        registerModel(RUST_BLOCK, 0, "rust_block");
    }
}