package gunn.modcurrency.mod.crafting.recipe;

import gunn.modcurrency.mod.item.ModItems;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.ArrayList;

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
            }else if(!inv.getStackInSlot(i).isEmpty()) otherItem = true;
        }

        return foundBag && otherItem;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack bag = new ItemStack(ModItems.itemBundledBag);
        ArrayList items = new ArrayList();

        for(int i = 0; i < inv.getSizeInventory(); i++){
            if(!inv.getStackInSlot(i).getItem().equals(ModItems.itemBundledBag)){
                items.add(inv.getStackInSlot(i));
            }
        }
        setInventory(bag, items);
        return bag;
    }

    @Override
    public boolean canFit(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    public void setInventory(ItemStack bag, ArrayList<ItemStack> items){
        ItemStackHandler itemStackHandler = new ItemStackHandler(items.size());
        for(int i = 0; i < itemStackHandler.getSlots(); i++) itemStackHandler.setStackInSlot(i, items.get(i));

        NBTTagCompound compound = new NBTTagCompound();
        compound.setTag("inventory", itemStackHandler.serializeNBT());
        bag.setTagCompound(compound);
    }
}
