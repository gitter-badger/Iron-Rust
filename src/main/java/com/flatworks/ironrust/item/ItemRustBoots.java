package com.flatworks.ironrust.item;

import com.flatworks.ironrust.IronRustMod;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;

public class ItemRustBoots extends ItemArmor {
    public ItemRustBoots() {
        super(IronRustMod.ARMOR_MATERIAL_RUST, 0, EntityEquipmentSlot.FEET);
        this.setUnlocalizedName("ironrust.bootsRust");
    }
}
