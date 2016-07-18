package com.flatworks.ironrust.item;

import com.flatworks.ironrust.IronRustMod;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;

public class ItemRustArmor extends ItemArmor {
    public ItemRustArmor(EntityEquipmentSlot equipmentSlotIn) {
        super(IronRustMod.ARMOR_MATERIAL_RUST, 0, equipmentSlotIn);
        switch (equipmentSlotIn) {
            case HEAD:
                this.setUnlocalizedName("ironrust.helmetRust");
                break;
            case CHEST:
                this.setUnlocalizedName("ironrust.chestplateRust");
                break;
            case LEGS:
                this.setUnlocalizedName("ironrust.leggingsRust");
                break;
            case FEET:
                this.setUnlocalizedName("ironrust.bootsRust");
                break;
            default:
                throw new IllegalArgumentException("Invalid equipment slot");
        }
    }
}
