package gunn.modcurrency.mod.items;

import gunn.modcurrency.mod.ModConfig;
import gunn.modcurrency.mod.ModCurrency;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Distributed with the Currency-Mod for Minecraft.
 * Copyright (C) 2016  Brady Gunn
 *
 * File Created on 2016-10-28.
 */
public class ItemBanknote extends Item {
    public static final int noteLength = 6;

    public ItemBanknote(){
        setHasSubtypes(true);
        setRegistryName("banknote");
        setCreativeTab(ModCurrency.tabCurrency);
        GameRegistry.register(this);
    }

    @SideOnly(Side.CLIENT)
    public void initModel(){
        if(ModConfig.shitTextures){
            for (int i = 0; i < noteLength; i++) {
                ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(getRegistryName()+ "16" + "_" + i, "inventory"));
            }
        }else {
            for (int i = 0; i < noteLength; i++) {
                ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(getRegistryName() + "_" + i, "inventory"));
            }
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "item." + this.getRegistryName().toString() + "_" + stack.getItemDamage();
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> subItems) {
        for(int i = 0; i < noteLength; i++){
            ItemStack stack = new ItemStack(item,1,i);
            subItems.add(stack);
        }
    }
}