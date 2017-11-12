package gunn.modcurrency.mod.container.itemhandler;

import gunn.modcurrency.mod.tileentity.TileVending;
import gunn.modcurrency.mod.utils.UtilMethods;
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
    TileVending tile;

    public ItemHandlerVendor(int size, TileVending tile) {
        super(size);
        this.tile = tile;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (UtilMethods.equalStacks(this.getStackInSlot(slot),stack)) {
            tile.growItemSize(stack.getCount(), slot);
            return ItemStack.EMPTY;
        } else {
            return stack;
        }
    }
}