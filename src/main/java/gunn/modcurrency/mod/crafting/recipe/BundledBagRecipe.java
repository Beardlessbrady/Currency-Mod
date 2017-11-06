package gunn.modcurrency.mod.crafting.recipe;

import gunn.modcurrency.mod.item.ModItems;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-11-06
 */
public class BundledBagRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        boolean foundBag = false;
        boolean otherItem = false;

        for(int i = 0; i < inv.getSizeInventory(); i ++){
            if(inv.getStackInSlot(i).getItem().equals(ModItems.itemBundledBag)){
                foundBag = true;
            }else otherItem = true;
        }

        return foundBag && otherItem;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        System.out.println(inv.getStackInSlot(0));
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }
}
