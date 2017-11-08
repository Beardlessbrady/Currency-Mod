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
 * File Created on 2017-11-07
 */
public class ItemUpgrade extends Item {
    int upgradeSize = 6;
    public ItemUpgrade() {
        setHasSubtypes(true);
        setRegistryName("upgrade");
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName() + "_" + "multiprices", "inventory"));
        ModelLoader.setCustomModelResourceLocation(this, 1, new ModelResourceLocation(getRegistryName() + "_" + "requestamnt", "inventory"));
        ModelLoader.setCustomModelResourceLocation(this, 2, new ModelResourceLocation(getRegistryName() + "_" + "stacksize0", "inventory"));
        ModelLoader.setCustomModelResourceLocation(this, 3, new ModelResourceLocation(getRegistryName() + "_" + "stacksize1", "inventory"));
        ModelLoader.setCustomModelResourceLocation(this, 4, new ModelResourceLocation(getRegistryName() + "_" + "stacksize2", "inventory"));
        ModelLoader.setCustomModelResourceLocation(this, 5, new ModelResourceLocation(getRegistryName() + "_" + "stacksize3", "inventory"));
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "item." + this.getRegistryName().toString() + "_" + stack.getItemDamage();
    }

    @Override
    public void getSubItems(CreativeTabs itemIn, NonNullList<ItemStack> tab) {
        if(itemIn == ModCurrency.tabCurrency){
            for (int i = 0; i < upgradeSize; i++) {
                ItemStack stack = new ItemStack(this, 1, i);
                tab.add(stack);
            }
        }
    }
}


