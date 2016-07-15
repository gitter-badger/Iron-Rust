package com.flatworks.ironrust.item;

import com.flatworks.ironrust.IronRustMod;

import net.minecraft.item.ItemAxe;

public class ItemRustAxe extends ItemAxe {
    public ItemRustAxe() {
        super(IronRustMod.TOOL_MATERIAL_RUST, -3.1f, 6f);
        this.setUnlocalizedName("ironrust.hatchetRust");
    }
}
