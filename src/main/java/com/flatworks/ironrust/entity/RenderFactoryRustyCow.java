package com.flatworks.ironrust.entity;

import com.flatworks.ironrust.IronRustMod;

import net.minecraft.client.model.ModelCow;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderFactoryRustyCow implements IRenderFactory<EntityRustyCow> {
    public static final ResourceLocation TEXTURES =
            new ResourceLocation(IronRustMod.MODID, "textures/entity/rusty_cow.png");
    
    @Override
    public Render<? super EntityRustyCow> createRenderFor(RenderManager manager) {
        return new RenderLiving<EntityRustyCow>(manager, new ModelCow(), 0.7F) {
            @Override
            protected ResourceLocation getEntityTexture(EntityRustyCow entity) {
                return TEXTURES;
            }
        };
    }
}
