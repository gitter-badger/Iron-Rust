package com.flatworks.ironrust.item;

import com.flatworks.ironrust.IronRustMod;

import net.minecraft.item.ItemFood;
import net.minecraft.potion.PotionEffect;

/**
 * @author sjx233
 */
public class ItemRustApple extends ItemFood {
    public ItemRustApple() {
        super(5, 0.3F, false);
        this.setUnlocalizedName("ironrust.appleRust");
        this.setPotionEffect(new PotionEffect(IronRustMod.POTION_RUST, 200, 10), 0.2f);
    }
}
