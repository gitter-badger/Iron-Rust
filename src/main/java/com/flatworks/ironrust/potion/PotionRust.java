package com.flatworks.ironrust.potion;

import com.flatworks.ironrust.IronRustMod;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author sjx233
 */
public class PotionRust extends Potion {
    private static final ResourceLocation TEXTURES =
            new ResourceLocation(IronRustMod.MODID, "textures/gui/potions.png");
    
    private static final int ICON_INDEX_X = 0;
    private static final int ICON_INDEX_Y = 0;
    
    public PotionRust() {
        super(true, 0x240d00);
        this.setPotionName("effect.rust");
        this.setEffectiveness(0.25d);
        this.registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED,
                "b4848d5d-4d9f-432f-a850-87efc2ad56dd", -0.15d, 2);
        this.registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED,
                "abd2b596-5974-4f99-bd63-71c4a12b3409", -0.1d, 2);
        this.registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_DAMAGE,
                "14d02f03-3d0c-4b7d-ae0d-08e8a1af0d20", +0.05d, 2);
        this.registerPotionAttributeModifier(SharedMonsterAttributes.ARMOR,
                "218119cf-ee1c-4cf9-953a-33bea6f5cbcf", +0.1d, 2);
    }
    
    @Override
    public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier) {
        entityLivingBaseIn.attackEntityFrom(DamageSource.magic, 1f);
    }
    
    @Override
    public boolean isReady(int duration, int amplifier) {
        int i = 100 >> amplifier;
        return i > 0 ? duration % i == 0 : true;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
        mc.getTextureManager().bindTexture(TEXTURES);
        mc.currentScreen.drawTexturedModalRect(x + 6, y + 7, ICON_INDEX_X * 18, ICON_INDEX_Y * 18,
                18, 18);
    }
}
