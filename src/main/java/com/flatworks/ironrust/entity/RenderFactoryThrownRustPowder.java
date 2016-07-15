package com.flatworks.ironrust.entity;

import com.flatworks.ironrust.IronRustMod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderFactoryThrownRustPowder implements IRenderFactory<EntityThrownRustPowder> {
    @Override
    public Render<? super EntityThrownRustPowder> createRenderFor(RenderManager manager) {
        return new RenderSnowball<EntityThrownRustPowder>(manager, IronRustMod.RUST_POWDER,
                Minecraft.getMinecraft().getRenderItem());
    }
}
