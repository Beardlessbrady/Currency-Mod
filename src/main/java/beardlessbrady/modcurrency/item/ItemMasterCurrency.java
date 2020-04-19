package beardlessbrady.modcurrency.item;

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
 * File Created 2020-04-19
 */
public class ItemMasterCurrency extends Item {
    public ItemMasterCurrency(){
        setUnlocalizedName("mastercurrency");
        setRegistryName("mastercurrency");
    }

    /** Used to initialize the textures and models for the item **/
    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if(tab == ModCurrency.tabCurrency) {
            ItemStack stack = new ItemStack(this, 1, 0);
            items.add(stack);
        }
    }
}
