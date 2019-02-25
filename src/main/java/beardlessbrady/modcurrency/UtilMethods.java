package beardlessbrady.modcurrency;

import net.minecraft.item.ItemStack;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-02-25
 */

public final class UtilMethods {
    public static boolean equalStacks(ItemStack one, ItemStack two){
        boolean basic = one.getItem().equals(two.getItem()) && (one.getItemDamage() == two.getItemDamage());
        boolean ench = false;
        if(one.hasTagCompound() && two.hasTagCompound()) {
            ench = (one.getTagCompound().equals(two.getTagCompound()));
        }else if(one.hasTagCompound() == false && one.hasTagCompound() == false) ench = true;
        return basic && ench;
    }

    public static String translateMoney(long amount){
        String amnt = Long.toString(amount);
        String finalTranslation;


        if(amnt.length() == 1) {
            finalTranslation = "0.0" + amnt;
        }else if(amnt.length() >2){   //At least one dollar
            finalTranslation = amnt.substring(0, amnt.length()-2) + "." + amnt.substring(amnt.length()-2, amnt.length());
        }else if(amnt.equals(0)) {
            finalTranslation = amnt;
        }else{
            finalTranslation = "0." + amnt;
        }

        return finalTranslation;
    }
}