package com.flatworks.ironrust.core;

import java.util.Arrays;

import com.flatworks.ironrust.IronRustMod;
import com.google.common.eventbus.EventBus;

import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;

/**
 * @author sjx233
 */
public class IronRustModContainer extends DummyModContainer {
    public IronRustModContainer() {
        super(new ModMetadata());
        ModMetadata meta = this.getMetadata();
        meta.modId = IronRustMod.MODID + "core";
        meta.name = IronRustMod.NAME + " (Core)";
        meta.version = IronRustMod.VERSION;
        meta.authorList = Arrays.asList("sjx233");
        meta.description = "Iron + Oxygen!";
    }
    
    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }
}
