package gunn.modcurrency.mod.container.itemhandler;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-02-25
 */
public class ItemHandlerCustom extends ItemStackHandler {
    Item allowed;

    public ItemHandlerCustom(int size) {
        super(size);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (stack.getItem() == allowed) {
            return super.insertItem(slot, stack, simulate);
        } else {
            return stack;
        }
    }

    public void setAllowedItem(Item item){
        allowed = item;
    }
}