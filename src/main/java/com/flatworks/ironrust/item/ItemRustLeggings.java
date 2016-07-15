package com.flatworks.ironrust.item;

import com.flatworks.ironrust.IronRustMod;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;

public class ItemRustLeggings extends ItemArmor {
    public ItemRustLeggings() {
        super(IronRustMod.ARMOR_MATERIAL_RUST, 0, EntityEquipmentSlot.LEGS);
        this.setUnlocalizedName("ironrust.leggingsRust");
    }
}
