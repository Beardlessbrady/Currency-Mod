package beardlessbrady.modcurrency.block.tradein;

import beardlessbrady.modcurrency.utilities.UtilMethods;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-12-08
 */

public class ItemTradein {
    private ItemStack itemStack;
    private int cost, amount;
    private int itemMax, timeRaise, timeElapsed;

    public ItemTradein(ItemStack itemStack){
        this.itemStack = itemStack;
        itemStack.setCount(1);

        cost = 0;
        amount = 1;
        itemMax = 0;
        timeRaise = 0;
        timeElapsed = 0;
    }

    public ItemTradein(NBTTagCompound compound){
        fromNBT(compound);
    }

    public ItemStack getStack(){
        return itemStack;
    }

    public int getCost(){
        return cost;
    }

    public void setCost(int i){
        cost = i;
    }

    public int getAmount(){
        return amount;
    }

    public void setAmount(int i){
        amount = i;
    }

    public int getItemMax(){
        return itemMax;
    }

    public void setItemMax(int i){
        itemMax = i;
    }

    public int getTimeRaise(){
        return timeRaise;
    }

    public void setTimeRaise(int i){
        timeRaise = i;
    }

    public int getTimeElapsed(){
        return timeElapsed;
    }

    public void setTimeElapsed(int i){
        timeElapsed = i;
    }

    public NBTTagCompound toNBT(){
        NBTTagCompound compound = new NBTTagCompound();
        compound.setTag("stack", itemStack.serializeNBT());
        if(cost != 0) compound.setInteger("cost", cost);
        if(amount != 0) compound.setInteger("amount", amount);
        if(itemMax != 0) compound.setInteger("itemMax", itemMax);
        if(timeRaise != 0) compound.setInteger("timeRaise", timeRaise);
        if(timeElapsed != 0) compound.setInteger("timeElapsed", timeElapsed);

        return compound;
    }

    public void fromNBT(NBTTagCompound nbt){
        if(nbt.hasKey("stack")){
            itemStack = new ItemStack(nbt.getCompoundTag("stack"));

            if(nbt.hasKey("cost")){
                cost = nbt.getInteger("cost");
            }else{
                cost = 0;
            }
            if(nbt.hasKey("amount")){
                amount = nbt.getInteger("amount");
            }else amount = 0;

            if(nbt.hasKey("itemMax")){
                itemMax = nbt.getInteger("itemMax");
            }else itemMax = 0;

            if(nbt.hasKey("timeRaise")){
                timeRaise = nbt.getInteger("timeRaise");
            }else timeRaise = 0;

            if(nbt.hasKey("timeElapsed")){
                timeElapsed = nbt.getInteger("timeElapsed");
            }else timeElapsed = 0;
        }
    }

    public boolean isEmpty(){
        return itemStack == ItemStack.EMPTY;
    }
}
