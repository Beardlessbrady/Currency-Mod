package beardlessbrady.modcurrency2.block.economyblocks.vending;

import beardlessbrady.modcurrency2.utilities.UtilMethods;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-10-19
 */

public class ItemVendor {
    private ItemStack itemStack;
    private int size, cost, amount;
    private int[] bundled;
    private int itemMax, timeRaise, timeElapsed;
    private int sizeLimit;

    public ItemVendor(ItemStack itemStack, int size){
        this.itemStack = itemStack;
        itemStack.setCount(1);

        this.size = size;
        this.cost = 0;

        amount = 1;
        itemMax = 0;
        timeRaise = 0;
        timeElapsed = 0;
        sizeLimit = 256;
    }

    public ItemVendor(ItemStack itemStack){
        this.itemStack = itemStack;

        size = itemStack.getCount();
        itemStack.setCount(1);

        cost = 0;
        amount = 1;
        itemMax = 0;
        timeRaise = 0;
        timeElapsed = 0;
        sizeLimit = 256;
    }

    public ItemVendor(NBTTagCompound compound){
        fromNBT(compound);
    }

    public ItemStack getStack(){
        return itemStack;
    }

    public int getSize(){
        return size;
    }

    public void setSize(int i){
        size = i;
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

    public boolean hasBundle(){
        return bundled != null;
    }

    /**
     * @return bundle array, 0 is main slot
     */
    public int[] getBundle(){
        return bundled;
    }

    /**
     * @param i = Array of bundle slots
     * main slot is 0
     */
    public void setBundle(int[] i){
        if(i == null){
            bundled = null;
        } else {
            bundled = i.clone();
        }
    }

    public int getBundleMainSlot(){
        if(bundled != null){
            return bundled[0];
        }
        return -1;
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
        if(size != 0) compound.setInteger("size", size);
        if(cost != 0) compound.setInteger("cost", cost);

        if(amount != 0) compound.setInteger("amount", amount);
        if(bundled != null) compound.setIntArray("bundled", bundled);
        if(itemMax != 0) compound.setInteger("itemMax", itemMax);
        if(timeRaise != 0) compound.setInteger("timeRaise", timeRaise);
        if(timeElapsed != 0) compound.setInteger("timeElapsed", timeElapsed);

        return compound;
    }

    public void fromNBT(NBTTagCompound nbt){
        if(nbt.hasKey("stack")){
            itemStack = new ItemStack(nbt.getCompoundTag("stack"));

            if(nbt.hasKey("size")) {
                size = nbt.getInteger("size");
            }else size = 0;

            if(nbt.hasKey("cost")){
                cost = nbt.getInteger("cost");
            }else cost = 0;


            if(nbt.hasKey("amount")){
                amount = nbt.getInteger("amount");
            }else amount = 0;

            if(nbt.hasKey("bundled")) bundled = nbt.getIntArray("bundled");

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

    public void shrinkSize(int amount){
        size = size - amount;

        if(size < 0) size = 0;
    }

    public ItemStack shrinkSizeWithStackOutput(int amount){
            ItemStack outputStack = this.getStack().copy();
            int output = this.size - amount;

            if (output < 0) {
                size = 0;
                outputStack.setCount(amount + output);
            } else {
                size = size - amount;
                outputStack.setCount(amount);
            }

            //returns shrunk amount in a stack
            return outputStack;

    }

    public void growSize(int amount){
        int maxCheck = sizeLimit - size - amount;
        if(maxCheck >= 0){
            size = size + amount;
        }else{
            size = size + (amount+maxCheck);
        }
    }

    public ItemStack growSizeWithStack(ItemStack stack){
        if(UtilMethods.equalStacks(this.getStack(), stack, false)) {
            int amount = stack.getCount();
            int maxCheck = sizeLimit - size - amount;

            if (maxCheck >= 0) {
                size = size + amount;
                return ItemStack.EMPTY;
            } else { //If not enough room in stack...
                size = size + (amount + maxCheck);
                ItemStack itemStack = getStack();

                return stack;
            }
        }
            return stack;
    }

    public int getSizeLimit(){
        return sizeLimit;
    }

    public boolean isEmpty(){
        return itemStack == ItemStack.EMPTY;
    }
}
