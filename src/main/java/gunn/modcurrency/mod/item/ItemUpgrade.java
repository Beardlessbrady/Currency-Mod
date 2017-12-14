package gunn.modcurrency.mod.item;

import gunn.modcurrency.mod.ModCurrency;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-11-07
 */
public class ItemUpgrade extends Item {
    int upgradeSize = 6;
    public ItemUpgrade() {
        setHasSubtypes(true);
        setRegistryName("upgrade");
        setMaxStackSize(16);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
     //   ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName() + "_" + "multiprices", "inventory"));
        ModelLoader.setCustomModelResourceLocation(this, 1, new ModelResourceLocation(getRegistryName() + "_" + "requestamnt", "inventory"));
        ModelLoader.setCustomModelResourceLocation(this, 2, new ModelResourceLocation(getRegistryName() + "_" + "stacksize0", "inventory"));
        ModelLoader.setCustomModelResourceLocation(this, 3, new ModelResourceLocation(getRegistryName() + "_" + "stacksize1", "inventory"));
        ModelLoader.setCustomModelResourceLocation(this, 4, new ModelResourceLocation(getRegistryName() + "_" + "stacksize2", "inventory"));
        ModelLoader.setCustomModelResourceLocation(this, 5, new ModelResourceLocation(getRegistryName() + "_" + "stacksize3", "inventory"));
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "item." + this.getRegistryName().toString() + "_" + stack.getItemDamage();
    }

    @Override
    public void getSubItems(CreativeTabs itemIn, NonNullList<ItemStack> tab) {
        if(itemIn == ModCurrency.tabCurrency){
            for (int i = 0; i < upgradeSize; i++) {
                if(i != 0) {
                    ItemStack stack = new ItemStack(this, 1, i);
                    tab.add(stack);
                }
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        switch (stack.getItemDamage()){
            case 1: tooltip.add("-Exchange Machine-");
                tooltip.add("Can set machine to only request a # of items");
                break;
            case 2: tooltip.add("-Vending Machine-");
                tooltip.add("Sets items max stack size to 32");
                break;
            case 3: tooltip.add("-Vending Machine-");
                tooltip.add("Sets items max stack size to 64");
                break;
            case 4: tooltip.add("-Vending Machine-");
                tooltip.add("Sets items max stack size to 128");
                break;
            case 5: tooltip.add("-Vending Machine-");
                tooltip.add("Sets items max stack size to 256");
                break;
        }
    }
}


