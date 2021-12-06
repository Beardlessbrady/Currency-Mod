package com.beardlessbrady.gocurrency.blocks.vending;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

/**
 * Created by BeardlessBrady on 2021-07-24 for Currency-Mod
 * All Rights Reserved
 * https://github.com/Beardlessbrady/Currency-Mod
 */
public class OverloadedItemStackHandler implements IItemHandler, IItemHandlerModifiable, INBTSerializable<CompoundTag> {
    protected NonNullList<ItemStack> stacks;
    protected IntProvider stackSize;
    protected int stackSizeLimit = 256;

    public OverloadedItemStackHandler() {
        this(1);
    }

    public OverloadedItemStackHandler(int size) {
        stacks = NonNullList.withSize(size, ItemStack.EMPTY);
        stackSize = new IntArray
    }

    public OverloadedItemStackHandler(NonNullList<ItemStack> add) {
        stacks = NonNullList.withSize(add.size(), ItemStack.EMPTY);
        stackSize = new IntArray(add.size());

        for (int i = 0; i < add.size(); i++) {
            stackSize.set(i, add.get(i).getCount());
            add.get(i).setCount(1);
            stacks.set(i, add.get(i).copy());
        }
    }

    public void setSize(int size) {
        stacks = NonNullList.withSize(size, ItemStack.EMPTY);
        stackSize = new IntArray(size);
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        validateSlotIndex(slot);
      //  this.stackSize.set(slot, stack.getCount());

        stack.setCount(1);
        this.stacks.set(slot, stack);
        onContentsChanged(slot);
    }

    public void setSizeInSlot(int slot, int value) {
        this.stackSize.set(slot, value);
        onContentsChanged(slot);
    }

    @Override
    public int getSlots() {
        return stacks.size();
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot(int slot) {
        validateSlotIndex(slot);
        return this.stacks.get(slot);
    }

    public int getSizeInSlot(int slot) {
        return this.stackSize.get(slot);
    }

    /**
     * Return leftover if cant grow
     *
     * @param slot which slot
     * @param amnt amount to grow
     * @return leftover
     */
    protected int growStackSize(int slot, int amnt) {
        if (amnt + stackSize.get(slot) > stackSizeLimit) {
            int leftover = (stackSize.get(slot) + amnt) - stackSizeLimit;
            int grow = amnt - leftover;
            stackSize.set(slot, stackSize.get(slot) + grow);
            onContentsChanged(slot);
            return leftover;

        } else {
            stackSize.set(slot, stackSize.get(slot) + amnt);
            onContentsChanged(slot);
            return 0;
        }
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        if (!isItemValid(slot, stack))
            return stack;

        validateSlotIndex(slot);

        ItemStack existing = this.stacks.get(slot).copy();
        existing.setCount(stackSize.get(slot));

        int limit = getStackLimit(slot, stack);

        if (!existing.isEmpty()) {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                return stack;

            limit -= existing.getCount();
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate) {
            if (existing.isEmpty()) {
                this.stacks.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            } else {
                growStackSize(slot, reachedLimit ? limit : stack.getCount());
            }
            onContentsChanged(slot);
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
    }

    @Override
    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0)
            return ItemStack.EMPTY;

        validateSlotIndex(slot);

        ItemStack existing = this.stacks.get(slot).copy();
        existing.setCount(this.stackSize.get(slot));

        if (existing.isEmpty())
            return ItemStack.EMPTY;

        int toExtract = Math.min(amount, stackSizeLimit);

        if (existing.getCount() <= toExtract) {
            if (!simulate) {
              //  this.stacks.set(slot, ItemStack.EMPTY);
                this.stackSize.set(slot, 0);
                onContentsChanged(slot);
                return existing;
            } else {
                return existing.copy();
            }
        } else {
            if (!simulate) {
                this.stacks.set(slot, ItemHandlerHelper.copyStackWithSize(existing, 1));
                this.stackSize.set(slot, (existing.getCount() - toExtract));

                onContentsChanged(slot);
            }

            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return stackSizeLimit;
    }

    protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
        return stackSizeLimit;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return true;
    }

    @Override
    public CompoundTag serializeNBT() {
        ListTag nbtTagList = new ListTag();
        for (int i = 0; i < stacks.size(); i++) {
            if (!stacks.get(i).isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                stacks.get(i).write(itemTag);
                nbtTagList.add(itemTag);
            }
        }
        CompoundTag nbt = new CompoundTag();
        nbt.put("Items", nbtTagList);
        nbt.putInt("Size", stacks.size());


        int[] sizeArray = new int[stackSize.size()];
        for (int i = 0; i < sizeArray.length; i++) {
            sizeArray[i] = stackSize.get(i);
        }
        nbt.putIntArray("OverloadedSize", sizeArray);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        setSize(nbt.contains("Size", Constants.NBT.TAG_INT) ? nbt.getInt("Size") : stacks.size());
        ListTag tagList = nbt.getList("Items", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++) {
            CompoundTag itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("Slot");

            if (slot >= 0 && slot < stacks.size()) {
                stacks.set(slot, ItemStack.read(itemTags));
            }
        }

        int[] sizeArray = nbt.getIntArray("OverloadedSize");
        for (int i = 0; i < sizeArray.length; i++) {
            stackSize.set(i, sizeArray[i]);
        }
        onLoad();
    }

    protected void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= stacks.size())
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + stacks.size() + ")");
    }

    protected void onLoad() {

    }

    protected void onContentsChanged(int slot) {
        isDirty = true;
    }

    private boolean isDirty = true;
    public boolean isDirty() {
        boolean currentState = isDirty;
        isDirty = false;
        return currentState;
    }

    public IntArray getStackSizeIntArray(){
        return stackSize;
    }
}
