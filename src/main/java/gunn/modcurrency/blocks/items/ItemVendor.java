package gunn.modcurrency.blocks.items;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * This class was created by <Brady Gunn>.
 * Distributed with the Currency-Mod for Minecraft.
 *
 * The Currency-Mod is open source and distributed
 * under the General Public License
 *
 * File Created on 2016-11-14.
 */
public class ItemVendor extends ItemBlock{
    
    public ItemVendor(Block block){
        super(block);
        setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        if(stack.getItemDamage() != 7) {
            tooltip.add(String.valueOf(EnumDyeColor.byMetadata(stack.getItemDamage())));
        }
    }
}



