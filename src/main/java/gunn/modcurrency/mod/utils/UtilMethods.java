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
        boolean ench = false;
        if(one.hasTagCompound() && two.hasTagCompound()) {
            ench = (one.getTagCompound().equals(two.getTagCompound()));
        }
        return basic && ench;
    }

    public static String translateMoney(long amount){
       String amnt = Long.toString(amount);
       String finalTranslation;


        if(amnt.length() >2){   //At least one dollar
            finalTranslation = amnt.substring(0, amnt.length()-2) + "." + amnt.substring(amnt.length()-2, amnt.length());
        }else if(amnt.equals("0")) {
            finalTranslation = amnt;
        }else{
            finalTranslation = "0." + amnt;
        }

        return finalTranslation;
    }
}
