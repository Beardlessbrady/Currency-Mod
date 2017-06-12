package gunn.modcurrency.mod.utils;

import net.minecraft.item.ItemStack;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-06-12
 */
public final class UtilMethods {
   public static boolean equalStacks(ItemStack one, ItemStack two){
        boolean basic = one.getItem().equals(two.getItem()) && (one.getItemDamage() == two.getItemDamage());
        boolean ench = true;
        if(one.hasTagCompound() && two.hasTagCompound()) {
            ench = (one.getTagCompound().equals(two.getTagCompound()));
        }
        return basic && ench;
    }
}
