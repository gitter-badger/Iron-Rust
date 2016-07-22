package com.flatworks.ironrust.potion;

import static net.minecraft.init.Items.LINGERING_POTION;
import static net.minecraft.init.Items.POTIONITEM;
import static net.minecraft.init.Items.SPLASH_POTION;

import java.util.Arrays;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.oredict.OreDictionary;

/**
 * @author sjx233
 */
public class PotionBrewingRecipe implements IBrewingRecipe {
    private final PotionType input;
    private final ItemStack ingredient;
    private final PotionType output;
    
    public PotionBrewingRecipe(PotionType input, Item ingredient, PotionType output) {
        this.input = input;
        this.ingredient = new ItemStack(ingredient);
        this.output = output;
    }
    
    @Override
    public boolean isInput(ItemStack input) {
        return PotionUtils.getPotionFromItem(input).equals(this.input) && Arrays
                .asList(POTIONITEM, SPLASH_POTION, LINGERING_POTION).contains(input.getItem());
    }
    
    @Override
    public boolean isIngredient(ItemStack ingredient) {
        return OreDictionary.itemMatches(this.ingredient, ingredient, false);
    }
    
    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
        return this.isInput(input) && this.isIngredient(ingredient)
                ? PotionUtils.addPotionToItemStack(new ItemStack(input.getItem()), this.output)
                : null;
    }
}
