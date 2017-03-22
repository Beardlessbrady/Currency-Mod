package gunn.modcurrency.mod.client.container.itemhandlers;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-02-25
 */
public class ItemHandlerVendor extends ItemStackHandler {
    private boolean ghostItems[];

    public ItemHandlerVendor(int size) {
        super(size);
        ghostItems = new boolean[size];
        for (int i = 0; i < ghostItems.length; i++) ghostItems[i] = false;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (this.getStackInSlot(slot).getItem() == stack.getItem()) {
            if (isGhost(slot)) {
                setGhost(slot, false);
                stack.shrink(1);
            }
            return super.insertItem(slot, stack, simulate);
        } else {
            return stack;
        }
    }

    public boolean isGhost(int slot) {
        return ghostItems[slot];
    }

    public void setGhost(int slot, boolean bool) {
        ghostItems[slot] = bool;
    }
}