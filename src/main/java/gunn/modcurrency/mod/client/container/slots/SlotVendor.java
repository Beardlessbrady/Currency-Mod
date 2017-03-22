package gunn.modcurrency.mod.client.container.slots;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-02-26
 */
public class SlotVendor extends SlotItemHandler{

    public SlotVendor(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        return true;
    }

    @Override
    public int getItemStackLimit(@Nonnull ItemStack stack) {
        ItemStack currentStack = getStack();
        int maxAdd = stack.getMaxStackSize();
        if(currentStack == null ) return 64;
        if(!ItemHandlerHelper.canItemStacksStack(stack, currentStack))  return 64;
        int stackSize = currentStack.getCount() + stack.getCount();
        if(stackSize > maxAdd) stackSize = maxAdd;
        return stackSize;
    }
}