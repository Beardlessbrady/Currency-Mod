package beardlessbrady.modcurrency2.block.economyblocks.vending;

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
 * File Created 2019-10-19
 */

public class ItemVendorHandler implements IItemHandler, IItemHandlerModifiable, INBTSerializable<NBTTagCompound> {
    ItemVendor[] itemArray;

    public ItemVendorHandler(int size) {
        itemArray = new ItemVendor[size];
    }

    public ItemVendor getItemVendor(int i) {
        if (i >= 0 && i < itemArray.length) {
            if (itemArray[i] == null)
                return new ItemVendor(ItemStack.EMPTY);
            return itemArray[i];
        } else
            return new ItemVendor(ItemStack.EMPTY, -1);
    }

    public void setItemVendor(int i, ItemVendor item) {
        itemArray[i] = item;
    }

    public void voidSlot(int i){
        itemArray[i] = null;
    }

    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        for (int i = 0; i < itemArray.length; i++) {
            if (itemArray[i] != null) {
                compound.setTag(Integer.toString(i), itemArray[i].toNBT());
            }
        }
        return compound;
    }

    public void deserializeNBT(NBTTagCompound nbt) {
        for (int i = 0; i < itemArray.length; i++) {
            if (nbt.hasKey(Integer.toString(i))) {
                itemArray[i] = new ItemVendor(nbt.getCompoundTag(Integer.toString(i)));
            }
        }
    }

    public int length() {
        return itemArray.length;
    }

    //------------#ItemStackHandler Overwritten fields----------
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
       //     setItemVendor(slot, new ItemVendor(stack));
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
        return 256;
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
       //Breaks things, not sure what its for
    }
}
