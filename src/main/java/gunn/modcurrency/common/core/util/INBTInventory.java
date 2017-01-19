package gunn.modcurrency.common.core.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemStackHandler;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
