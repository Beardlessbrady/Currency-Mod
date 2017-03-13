package gunn.modcurrency.mod.blocks.items;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Distributed with the Currency-Mod for Minecraft.
 * Copyright (C) 2016  Brady Gunn
 *
 * File Created on 2016-11-14.
 */
public class IBColored extends ItemBlock{
    
    public IBColored(Block block){
        super(block);
        setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        if(stack.getItemDamage() != 0) {
            tooltip.add(String.valueOf(EnumDyeColor.byDyeDamage(stack.getItemDamage())));
        }
    }
}



