package com.flatworks.ironrust.item;

import com.flatworks.ironrust.IronRustMod;

import net.minecraft.item.ItemSword;

public class ItemRustSword extends ItemSword {
    public ItemRustSword() {
        super(IronRustMod.TOOL_MATERIAL_RUST);
        this.setUnlocalizedName("ironrust.swordRust");
    }
}
