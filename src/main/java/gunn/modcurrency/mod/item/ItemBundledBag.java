package gunn.modcurrency.mod.item;

import gunn.modcurrency.mod.ModCurrency;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-11-04
 */
public class ItemBundledBag extends Item{
    public ItemBundledBag() {
        setRegistryName("bundledbag");
        setCreativeTab(ModCurrency.tabCurrency);
        setUnlocalizedName(getRegistryName().toString());
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(this, 1, new ModelResourceLocation(getRegistryName() + "_open", "inventory"));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
       if(stack.hasTagCompound()) {
           if (stack.getTagCompound().hasKey("inventory")) {
               ItemStackHandler itemStackHandler = new ItemStackHandler(8);
               itemStackHandler.deserializeNBT((NBTTagCompound) stack.getTagCompound().getTag("inventory"));

               tooltip.add("ITEMS");
               for (int i = 0; i < itemStackHandler.getSlots(); i++) {
                   if (!itemStackHandler.getStackInSlot(i).isEmpty()) {
                       tooltip.add(itemStackHandler.getStackInSlot(i).getDisplayName() + " x" + itemStackHandler.getStackInSlot(i).getCount());
                   }
               }
           }
       }
    }
}
