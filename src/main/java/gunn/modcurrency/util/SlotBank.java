package gunn.modcurrency.util;

import gunn.modcurrency.items.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

/**
 * This class was created by <Brady Gunn>.
 * Distributed with the Currency-Mod for Minecraft.
 *
 * The Currency-Mod is open source and distributed
 * under the General Public License
 *
 * File Created on 2016-11-11.
 */
public class SlotBank extends SlotItemHandler {

    public SlotBank(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack.getItem() == ModItems.itembanknote;
    }
}
