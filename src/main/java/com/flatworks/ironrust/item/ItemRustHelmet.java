package com.flatworks.ironrust.item;

import com.flatworks.ironrust.IronRustMod;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;

public class ItemRustHelmet extends ItemArmor {
    public ItemRustHelmet() {
        super(IronRustMod.ARMOR_MATERIAL_RUST, 0, EntityEquipmentSlot.HEAD);
        this.setUnlocalizedName("ironrust.helmetRust");
    }
}
