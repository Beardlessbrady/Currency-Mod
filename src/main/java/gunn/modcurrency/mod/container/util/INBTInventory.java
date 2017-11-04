package gunn.modcurrency.mod.container.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemStackHandler;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-01-19
 */
public interface INBTInventory {

    default ItemStackHandler readInventoryTag(ItemStack stack, int handlerSize){
        NBTTagCompound compound = stack.getTagCompound();
        ItemStackHandler itemStackHandler = new ItemStackHandler(handlerSize);
        itemStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("inventory"));

        return itemStackHandler;
    }

     default void writeInventoryTag(ItemStack stack, ItemStackHandler inventory){
        NBTTagCompound compound = stack.getTagCompound();
        compound.setTag("inventory", inventory.serializeNBT());
        stack.setTagCompound(compound);
    }
}
