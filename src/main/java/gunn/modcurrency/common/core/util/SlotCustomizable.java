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
        boolean multiple = true;
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
