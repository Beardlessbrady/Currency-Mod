package beardlessbrady.modcurrency2;

import beardlessbrady.modcurrency2.item.ModItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-02-09
 */

public class TabCurrency extends CreativeTabs {
    public TabCurrency(String label) {
        super(label);
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(ModItems.itemCurrency,1,0);
    }

    @Override
    public String getTranslatedTabLabel() {
        return "Good ol' Currency";
    }
}
