package com.flatworks.ironrust.item;

import com.flatworks.ironrust.IronRustMod;

import net.minecraft.item.ItemSpade;

public class ItemRustSpade extends ItemSpade {
    public ItemRustSpade() {
        super(IronRustMod.TOOL_MATERIAL_RUST);
        this.setUnlocalizedName("ironrust.shovelRust");
    }
}
