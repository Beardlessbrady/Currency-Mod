package gunn.modcurrency.mod.container.itemhandler;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

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

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if(stack != null && this.getStackInSlot(slot) != null) {
            if (this.getStackInSlot(slot).getItem() == stack.getItem() && this.getStackInSlot(slot).getItemDamage() == stack.getItemDamage()) {
                if (isGhost(slot)) {
                    setGhost(slot, false);
                    stack.stackSize--;
                }
                return super.insertItem(slot, stack, simulate);
            } else {
                return stack;
            }
        }
        return stack;
    }

    public boolean isGhost(int slot) {
        return ghostItems[slot];
    }

    public void setGhost(int slot, boolean bool) {
        ghostItems[slot] = bool;
    }
}