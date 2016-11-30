package gunn.modcurrency;

import gunn.modcurrency.items.ModItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class was created by <Brady Gunn>.
 * Distributed with the Currency-Mod for Minecraft.
 *
 * The Currency-Mod is open source and distributed under a
 * Custom License: https://github.com/BeardlessBrady/Currency-Mod/blob/master/LICENSE
 *
 * File Created on 2016-10-29.
 */
public class TabCurrency extends CreativeTabs{

    public TabCurrency(int index, String label) {
        super(index, label);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Item getTabIconItem() {
        return ModItems.itembanknote;
    }

    @Override
    public String getTranslatedTabLabel() {
        return "Currency Mod";
    }
}
