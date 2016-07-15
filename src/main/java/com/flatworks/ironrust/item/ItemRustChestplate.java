package com.flatworks.ironrust.item;

import com.flatworks.ironrust.IronRustMod;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;

public class ItemRustChestplate extends ItemArmor {
    public ItemRustChestplate() {
        super(IronRustMod.ARMOR_MATERIAL_RUST, 0, EntityEquipmentSlot.CHEST);
        this.setUnlocalizedName("ironrust.chestplateRust");
    }
}
