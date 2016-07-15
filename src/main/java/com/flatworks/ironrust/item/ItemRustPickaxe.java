package com.flatworks.ironrust.item;

import com.flatworks.ironrust.IronRustMod;

import net.minecraft.item.ItemPickaxe;

public class ItemRustPickaxe extends ItemPickaxe {
    public ItemRustPickaxe() {
        super(IronRustMod.TOOL_MATERIAL_RUST);
        this.setUnlocalizedName("ironrust.pickaxeRust");
    }
}
