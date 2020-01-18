package beardlessbrady.modcurrency.utilities;

import beardlessbrady.modcurrency.ConfigCurrency;
import beardlessbrady.modcurrency.item.ModItems;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;

import java.util.Stack;

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
        }else if(one.hasTagCompound() == false && two.hasTagCompound() == false) ench = true;
        return basic && ench;
    }

    public static String translateMoney(int amount){
        String amnt = Integer.toString(amount);
        String finalTranslation;


        if(amnt.length() == 1) {
            finalTranslation = "0.0" + amnt;
        }else if(amnt.length() >2){   //At least one dollar
            finalTranslation = amnt.substring(0, amnt.length()-2) + "." + amnt.substring(amnt.length()-2);
        }else if(amnt.equals(0)) {
            finalTranslation = amnt;
        }else{
            finalTranslation = "0." + amnt;
        }

        return finalTranslation;
    }

    /** Inputs a total amount of money and outputs a STACK of individual bill itemstacks **/
    public static Stack<ItemStack> stackBills(int bank) {
        Stack<ItemStack> billStack = new Stack<ItemStack>(); //Stack to hold itemstacks of bills

        // Loops through currency values
        OUTER_LOOP:
        for (int i = ConfigCurrency.currencyValues.length - 1; i >= 0; i--) {
            boolean repeat = false; //Used if one bill has more then 64 items (bigger then an itemstack limit)
            if ((bank / (Float.parseFloat(ConfigCurrency.currencyValues[i])) * 100) > 0) { //Divisible by currency value
                int amount = (bank / ((int) ((Float.parseFloat(ConfigCurrency.currencyValues[i])) * 100)));

                if (amount > 64) { // If more then a stack repeat and create another stack of the same bill*/
                    amount = 64;
                    repeat = true;
                }

                if(amount != 0) { //If bill amount is not 0 push the Itemstack and subtract from amount
                    billStack.push(new ItemStack(ModItems.itemCurrency, amount, i));
                    bank -= ((Float.parseFloat(ConfigCurrency.currencyValues[i]) * 100) * amount);
                }
            }

            if (repeat) i++; //Go back to previous bill since there is still more of it
            if (bank == 0) break OUTER_LOOP; // If bank is 0 stop loop */
        }

        return billStack;
    }
}