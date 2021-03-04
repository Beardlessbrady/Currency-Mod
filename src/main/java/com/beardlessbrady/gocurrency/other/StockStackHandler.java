package com.beardlessbrady.gocurrency.other;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

/**
 * Created by BeardlessBrady on 2021-03-02 for Currency-Mod
 * All Rights Reserved
 * https://github.com/Beardlessbrady/Currency-Mod
 */
public class StockStackHandler extends ItemStackHandler {
    protected NonNullList<ItemStack> stacks;
    protected NonNullList<Integer> counts;
    protected final int MAX_COUNT = 256; // TODO

    public StockStackHandler(int size) {
        stacks = NonNullList.withSize(size, ItemStack.EMPTY);
        counts = NonNullList.withSize(size, 0);
    }

    @Override
    public void setSize(int size) {
        stacks = NonNullList.withSize(size, ItemStack.EMPTY);
        counts = NonNullList.withSize(size, 0);
    }

    // Inputting in GUI
    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        validateSlotIndex(slot);

        int count = stack.getCount();
        ItemStack copyStack = stack.copy();
        copyStack.setCount(1);

        this.stacks.set(slot, copyStack);
        this.counts.set(slot, count);
        onContentsChanged(slot);
    }

    @Override
    public int getSlots()
    {
        return stacks.size();
    }

    @Override
    @Nonnull
    // Displays
    public ItemStack getStackInSlot(int slot) {
        validateSlotIndex(slot);

        ItemStack stack = stacks.get(slot);
        return stack;
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        ItemStack copyStack = stack.copy();
        copyStack.setCount(1);

        if (stack.isEmpty())
            return ItemStack.EMPTY;

        if (!isItemValid(slot, stack))
            return stack;

        validateSlotIndex(slot);

        ItemStack existingStack = this.stacks.get(slot);
        int existingCount = this.counts.get(slot);

        int limit = getStackLimit(slot, stack);

        if (!existingStack.isEmpty()) {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existingStack))
                return stack;

            limit -= existingCount;
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate) {
            if (existingStack.isEmpty()) {
                this.counts.set(slot, reachedLimit ? limit: stack.getCount());
                this.stacks.set(slot, copyStack);
            } else {
                growCount(slot, reachedLimit ? limit : stack.getCount());
            }
            onContentsChanged(slot);
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount()- limit) : ItemStack.EMPTY;
    }

    // Taking in GUI
    @Override
    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0)
            return ItemStack.EMPTY;

        validateSlotIndex(slot);

        ItemStack existingStack = this.stacks.get(slot);
        int existingCount = this.counts.get(slot);

        System.out.println(amount + " " + existingStack + " " + existingCount);


        ItemStack copyStack = existingStack.copy();
        copyStack.setCount(existingCount);

        if (existingStack.isEmpty())
            return ItemStack.EMPTY;

        int toExtract = Math.min(amount, existingStack.getMaxStackSize());

        if (existingCount <= toExtract) { // Count <= Extract
            if (!simulate) {
                this.stacks.set(slot, ItemStack.EMPTY);
                this.counts.set(slot, 0);
                onContentsChanged(slot);

                return copyStack;
            } else {
                return copyStack.copy();
            }
        }  else { // Count > Extract, less then Max
            if (!simulate) {
                this.counts.set(slot, existingCount - toExtract);
                onContentsChanged(slot);
            }

            return ItemHandlerHelper.copyStackWithSize(copyStack, toExtract);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return MAX_COUNT;
    }

    @Override
    protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
        return Math.min(getSlotLimit(slot), MAX_COUNT);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return true;
    }

    @Override
    public CompoundNBT serializeNBT() {
        ListNBT nbtTagList = new ListNBT();
        for (int i = 0; i < stacks.size(); i++) {
            if (!stacks.get(i).isEmpty()) {
                CompoundNBT itemTag = new CompoundNBT();

                itemTag.putInt("Slot", i);
                itemTag.putInt("StockCount", counts.get(i));
                stacks.get(i).write(itemTag);

                nbtTagList.add(itemTag);
            }
        }
        CompoundNBT nbt = new CompoundNBT();
        nbt.put("Items", nbtTagList);
        nbt.putInt("Size", stacks.size());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        setSize(nbt.contains("Size", Constants.NBT.TAG_INT) ? nbt.getInt("Size") : stacks.size());
        ListNBT tagList = nbt.getList("Items", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++) {
            CompoundNBT itemTags = tagList.getCompound(i);

            int slot = itemTags.getInt("Slot");

            if (slot >= 0 && slot < stacks.size()) {
                stacks.set(slot, ItemStack.read(itemTags));
                counts.set(slot, itemTags.getInt("StockCount"));
            }
        }
        onLoad();
    }

    @Override
    protected void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= stacks.size())
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + stacks.size() + ")");
    }

    @Override
    protected void onLoad() {

    }

    @Override
    protected void onContentsChanged(int slot) {

    }

    // ------
    protected void growCount(int slot, int amount){
        counts.set(slot, counts.get(slot) + amount);
    }

    protected void shrinkCount(int slot, int amount){
        counts.set(slot, counts.get(slot) - amount);
    }
}
