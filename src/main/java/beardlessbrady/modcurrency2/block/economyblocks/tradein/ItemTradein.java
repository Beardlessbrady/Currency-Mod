package beardlessbrady.modcurrency2.block.economyblocks.tradein;

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
    private int cost, amount, size, until;
    private int itemMax, timeRaise, timeElapsed;

    public ItemTradein(ItemStack itemStack){
        this.itemStack = itemStack;
        itemStack.setCount(1); // Set itemStack count to 1 as this custom Item handles its own size */

        size = 0;
        until = 0;
        cost = 0;
        amount = 1;
        itemMax = 0;
        timeRaise = 0;
        timeElapsed = 0;
    }

    public ItemTradein(NBTTagCompound compound){
        fromNBT(compound);
    }

    /** Setters & Getter Methods **/
    //<editor-fold desc="Setters & Getters">
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

    public int  getUntil(){
        return until;
    }

    public void setUntil(int i){
        until = i;
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

    //</editor-fold>

    /** NBT Methods **/
    //<editor-fold desc="NBT Stuff">
    public NBTTagCompound toNBT(){
        NBTTagCompound compound = new NBTTagCompound();
        compound.setTag("stack", itemStack.serializeNBT());
        if(size != 0) compound.setInteger("size", size);
        if(cost != 0) compound.setInteger("cost", cost);
        if(amount != 0) compound.setInteger("amount", amount);
        if(until != 0) compound.setInteger("until", until);
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
            }else{
                cost = 0;
            }
            if(nbt.hasKey("amount")){
                amount = nbt.getInteger("amount");
            }else amount = 0;

            if(nbt.hasKey("until")){
                until = nbt.getInteger("until");
            }else until = 0;

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
    //</editor-fold>

    /** Shrink size of Item by amount **/
    public void shrinkSize(int amount){
        size = size - amount;
        if(size < 0) size = 0;
    }

    /** Shrink size of Item by amount AND output an itemStack of the shrunken amount **/
    public ItemStack shrinkSizeWithStackOutput(int amount){
        ItemStack outputStack = this.getStack().copy();
        int output = size - amount; // Calculate output stack size */

        if (output < 0) { // If output is < 0 and therefore the stack can't be shrunken by the amount specified */
            size = 0; //Empty Stack
            outputStack.setCount(amount + output); // Set Output stack size to amount + output (which is the original size of the itemStack) */
        } else { // If output is >= 0*/
            size = size - amount; // Shrink size of itemStack by amount */
            outputStack.setCount(amount); // set Output stack to amount */
        }
        return outputStack; // Return output stack with shrunken amount */
    }

    /** Grow size of item by amount **/
    public void growSize(int amount){
        int maxCheck = 512 - size - amount; // Ensures itemStack size can't grow past size limit */
        if(maxCheck >= 0){ // If maxCheck is >= 0 it is within the size limit */
            size = size + amount; // Add amount to size */
        }else{ // Trying to grow past size limit */
            size = size + (amount+maxCheck); // Add amount + maxCheck(will be negative) to only grow the item up to the size limit */
        }
    }
}
