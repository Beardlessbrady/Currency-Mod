package gunn.modcurrency.mod.container.slot;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Distributed with the Currency-Mod for Minecraft.
 * Copyright (C) 2016  Brady Gunn
 *
 * File Created on 2016-11-11.
 */
public class SlotCustomizable extends SlotItemHandler {
    List itemsAllowed;

    public SlotCustomizable(IItemHandler itemHandler, int index, int xPosition, int yPosition, Item onlyItemAllowed) {
        super(itemHandler, index, xPosition, yPosition);
        itemsAllowed = new ArrayList();
        itemsAllowed.add(onlyItemAllowed);
    }

    public SlotCustomizable(IItemHandler itemHandler, int index, int xPosition, int yPosition, List onlyItemsAllowed) {
        super(itemHandler, index, xPosition, yPosition);
        itemsAllowed = new ArrayList();
        itemsAllowed.addAll(onlyItemsAllowed);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        if(itemsAllowed.contains(stack.getItem())) return true;
        return false;
    }
}
