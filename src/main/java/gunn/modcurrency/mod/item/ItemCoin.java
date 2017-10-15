package gunn.modcurrency.mod.item;

import gunn.modcurrency.mod.ModCurrency;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-10-15
 */
public class ItemCoin extends Item{
    public static final int coinLength = 6;

    public ItemCoin() {
        setHasSubtypes(true);
        setRegistryName("coin");
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        for (int i = 0; i < coinLength; i++) {
            ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(getRegistryName() + "_" + i, "inventory"));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "item." + this.getRegistryName().toString() + "_" + stack.getItemDamage();
    }

    @Override
    public void getSubItems(CreativeTabs itemIn, NonNullList<ItemStack> tab) {
        if(itemIn == ModCurrency.tabCurrency){
            for (int i = 0; i < coinLength; i++) {
                ItemStack stack = new ItemStack(this, 1, i);
                tab.add(stack);
            }
        }
    }
}

