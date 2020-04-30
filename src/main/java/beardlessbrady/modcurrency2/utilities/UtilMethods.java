package beardlessbrady.modcurrency2.utilities;

import beardlessbrady.modcurrency2.ConfigCurrency;
import beardlessbrady.modcurrency2.item.ModItems;
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
    public static boolean equalStacks(ItemStack one, ItemStack two, boolean fuzzy){
        boolean basic = one.getItem().equals(two.getItem());

        if(!fuzzy) { //If NOT FUZZY (Smooth) Then will check if items have the same metadata and same tag compounds (Enchants)
            basic = basic && (one.getItemDamage() == two.getItemDamage());
            boolean ench = false;

            if (one.hasTagCompound() && two.hasTagCompound()) {
                ench = (one.getTagCompound().equals(two.getTagCompound()));
            } else if (one.hasTagCompound() == false && two.hasTagCompound() == false) ench = true;

            basic = basic && ench;
        }

        return basic;
    }

    /**Gets the currID and returns the CurrencySystem# (Usually the first 1 or 2 digits)
     */
    public static String getCurrSystemNumber(int currId){
        String currIdString = Integer.toString(currId);
        if(currIdString.length() == 1 || currIdString.length() == 2){ //Default System
            return "0";
        } else if(currIdString.length() == 3 ) { //Custom System 1-9
            
        } else if(currIdString.length() == 4 ) { //Custom System 10-99

        }

        return "Broken";
    }

    public static String translateMoney(long amount){
        String amnt = Long.toString(amount);
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