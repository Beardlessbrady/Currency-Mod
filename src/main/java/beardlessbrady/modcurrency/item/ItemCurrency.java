package beardlessbrady.modcurrency.item;

import beardlessbrady.modcurrency.ConfigCurrency;
import beardlessbrady.modcurrency.ModCurrency;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
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
    public static final int currencyLength = ConfigCurrency.currencyValues.length;

    public ItemCurrency(){
        setUnlocalizedName("currency");
        setRegistryName("currency");
        setHasSubtypes(true);
    }

    public int getCurrencyValue(ItemStack stack){
       return (int)(Float.parseFloat(ConfigCurrency.currencyValues[stack.getMetadata()]) * 100);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        for (int i = 0; i < currencyLength; i++) {
            ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(getRegistryName() + "_" + i, "inventory"));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "item." + this.getRegistryName().toString() + "_" + stack.getItemDamage();
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        if(stack.getItemDamage() >= ConfigCurrency.currencyNames.length){
            return "CurrSys: " + Integer.toString(stack.getMetadata());
        }else return ConfigCurrency.currencyNames[stack.getItemDamage()];
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        String defaultSystem = "000";
        String currentBill;
        if(tab == ModCurrency.tabCurrency){
            for (int i = 0; i < currencyLength; i++) {
                if((int) Math.log10(i) + 1 == 1){ //If int i digit length is 1
                    currentBill = defaultSystem + "0" + Integer.toString(i);
                }else{
                    currentBill = defaultSystem + Integer.toString(i);
                }
                ItemStack stack = new ItemStack(this, 1, Integer.parseInt(currentBill));
                items.add(stack);
            }
        }
    }
}
