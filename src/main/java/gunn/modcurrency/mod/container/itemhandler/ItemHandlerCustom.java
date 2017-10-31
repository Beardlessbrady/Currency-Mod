package gunn.modcurrency.mod.container.itemhandler;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-02-25
 */
public class ItemHandlerCustom extends ItemStackHandler {
    List valid;

    public ItemHandlerCustom(int size) {
        super(size);
        valid = new ArrayList(1);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (valid.contains(stack.getItem())) {
            return super.insertItem(slot, stack, simulate);
        } else {
            return stack;
        }
    }

    public void setAllowedItem(Item item) {
        valid.add(item);
    }
}