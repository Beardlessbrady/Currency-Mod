package gunn.modcurrency.common.core.util;

import gunn.modcurrency.common.items.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

/**
 * Distributed with the Currency-Mod for Minecraft.
 * Copyright (C) 2016  Brady Gunn
 *
 * File Created on 2016-11-11.
 */
public class SlotCustomizable extends SlotItemHandler {
    Item itemAllowed;
    Item[] itemsAllowed;
    boolean multiple = false;

    public SlotCustomizable(IItemHandler itemHandler, int index, int xPosition, int yPosition, Item onlyItemAllowed) {
        super(itemHandler, index, xPosition, yPosition);
        itemAllowed = onlyItemAllowed;
    }

    public SlotCustomizable(IItemHandler itemHandler, int index, int xPosition, int yPosition, Item[] onlyItemsAllowed) {
        super(itemHandler, index, xPosition, yPosition);
        itemsAllowed= onlyItemsAllowed.clone();
        multiple = true;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        if(multiple == false) {
            return stack.getItem() == itemAllowed;
        }else{
            for(int i = 0; i < itemsAllowed.length; i++){
                if(stack.getItem() == itemsAllowed[i]) return true;
            }
            return false;
        }

    }
}
