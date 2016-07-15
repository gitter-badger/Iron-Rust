package com.flatworks.ironrust.item;

import com.flatworks.ironrust.IronRustMod;

import net.minecraft.item.ItemHoe;

public class ItemRustHoe extends ItemHoe {
    public ItemRustHoe() {
        super(IronRustMod.TOOL_MATERIAL_RUST);
        this.setUnlocalizedName("ironrust.hoeRust");
    }
}
