package beardlessbrady.modcurrency.item;

import beardlessbrady.modcurrency.ModConfig;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-02-07
 */

public class ItemCurrency extends Item {
    public static final int currencyLength = ModConfig.currencyValues.length;

    public ItemCurrency(){
        setUnlocalizedName("currency");
        setRegistryName("currency");
        setHasSubtypes(true);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        for (int i = 0; i < 2; i++) {
            ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(getRegistryName() + "_" + i, "inventory"));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "Item." + this.getRegistryName().toString() + "_" + stack.getItemDamage();
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        if(stack.getItemDamage() >= ModConfig.currencyNames.length){
            return "SOMETHING WENT WRONG: ITEM DAMAGE TOO HIGH.";
        }else return ModConfig.currencyNames[stack.getItemDamage()];
    }
}
