package gunn.modcurrency.mod;

import gunn.modcurrency.mod.item.ModItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Distributed with the Currency-Mod for Minecraft.
 * Copyright (C) 2016  Brady Gunn
 *
 * File Created on 2016-10-29.
 */
public class TabCurrency extends CreativeTabs{

    public TabCurrency(int index, String label) {
        super(index, label);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getTabIconItem() {
        return new ItemStack(ModItems.itemBanknote, 1, 0);
    }

    @Override
    public String getTranslatedTabLabel() {
        return "Currency Mod";
    }
}
