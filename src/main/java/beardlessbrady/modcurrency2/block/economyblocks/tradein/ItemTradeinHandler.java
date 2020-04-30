package beardlessbrady.modcurrency2.block.economyblocks.tradein;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-12-08
 */

public class ItemTradeinHandler implements IItemHandler, IItemHandlerModifiable, INBTSerializable<NBTTagCompound> {
    ItemTradein[] itemArray;
    private final int LIMIT = 256;
    private int counter = 0;

    public ItemTradeinHandler(int size) {
        itemArray = new ItemTradein[size];
    }

    public ItemTradein getItemTradein(int i) {
        if (itemArray[i] == null)
            return new ItemTradein(ItemStack.EMPTY); // If ItemStack is empty return an ItemTradeIn with an empty ItemStack */
        return itemArray[i];
    }

    public void setItemTradein(int i, ItemTradein item) {
        itemArray[i] = item;
    }

    public void voidSlot(int i){
        itemArray[i] = null;
    }

    public int length() {
        return itemArray.length;
    }

    /** Serialize NBT of ItemTradeinHandler for saving **/
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        for (int i = 0; i < itemArray.length; i++) {
            if (itemArray[i] != null) {
                compound.setTag(Integer.toString(i), itemArray[i].toNBT());
            }
        }
        return compound;
    }

    /** Deserialize NBT from save to be unpacked **/
    public void deserializeNBT(NBTTagCompound nbt) {
        for (int i = 0; i < itemArray.length; i++) {
            if (nbt.hasKey(Integer.toString(i))) {
                itemArray[i] = new ItemTradein(nbt.getCompoundTag(Integer.toString(i)));
            }
        }
    }

    //#ItemStackHandler Overwritten fields
    @Override
    public int getSlots() {
        return itemArray.length;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        if(itemArray[slot] == null){
            return ItemStack.EMPTY;
        }

        return itemArray[slot].getStack();
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
      //  if(itemArray[slot] == null){
       //     setItemTradein(slot, new ItemTradein(stack));
        //    return ItemStack.EMPTY;
      //  }else{
       //    return itemArray[slot].growSizeWithStack(stack);
     //   }

        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
      //  if(itemArray[slot] == null){
     //       return ItemStack.EMPTY;
     //   }else{
       //   return itemArray[slot].shrinkSizeWithStackOutput(amount);
      //  }

        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return LIMIT; //TODO CONFIG
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
       //Breaks things, not sure what its for
    }
}
