package gunn.modcurrency.mod.item;

import gunn.modcurrency.mod.ModConfig;
import gunn.modcurrency.mod.ModCurrency;
import gunn.modcurrency.mod.block.ModBlocks;
import gunn.modcurrency.mod.utils.UtilRecipe;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
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

    public ItemBanknote() {
        setHasSubtypes(true);
        setRegistryName("banknote");
        setCreativeTab(ModCurrency.tabCurrency);
        GameRegistry.register(this);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        switch (ModConfig.textureType) {
            default:
            case 0:
                for (int i = 0; i < noteLength; i++) {
                    ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(getRegistryName() + "_" + i, "inventory"));
                }
                break;
            case 1:
                for (int i = 0; i < noteLength; i++) {
                    ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(getRegistryName() + "16" + "_" + i, "inventory"));
                }
                break;
            case 2:
                for (int i = 0; i < noteLength; i++) {
                    ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(getRegistryName() + "fool" + "_" + i, "inventory"));
                }
                break;
        }
    }

    public void recipe() {
        ItemStack bill1 = new ItemStack(ModItems.itemBanknote,1, 0);
        ItemStack bill5 = new ItemStack(ModItems.itemBanknote,1,1);
        ItemStack bill10 = new ItemStack(ModItems.itemBanknote,1,2);
        ItemStack bill20 = new ItemStack(ModItems.itemBanknote,1,3);
        ItemStack bill50 = new ItemStack(ModItems.itemBanknote,1,4);
        ItemStack bill100 = new ItemStack(ModItems.itemBanknote,1,5);

        //Output 100 Dollar
        ItemStack[] itemList = {bill1, bill1, bill1, bill1, bill1, bill5, bill20, bill20, bill50};
        UtilRecipe.addShapelessRecipe("itemdollarbill_100_0", bill100, itemList);

        itemList = new ItemStack[] {bill1, bill1, bill1, bill1, bill1, bill5, bill20, bill20, bill50};
        UtilRecipe.addShapelessRecipe("itemdollarbill_100_1", bill100, itemList);

        itemList = new ItemStack[] {bill5, bill5, bill20, bill20, bill50};
        UtilRecipe.addShapelessRecipe("itemdollarbill_100_2", bill100, itemList);

        itemList = new ItemStack[] {bill5, bill5, bill10, bill10, bill20, bill50};
        UtilRecipe.addShapelessRecipe("itemdollarbill_100_3", bill100, itemList);

        itemList = new ItemStack[] {bill5, bill5, bill10, bill10, bill10, bill10, bill50};
        UtilRecipe.addShapelessRecipe("itemdollarbill_100_4", bill100, itemList);

        itemList = new ItemStack[] {bill5, bill5, bill10, bill10, bill10, bill10, bill10, bill20, bill20};
        UtilRecipe.addShapelessRecipe("itemdollarbill_100_5", bill100, itemList);

        itemList = new ItemStack[] {bill10, bill20, bill20, bill50};
        UtilRecipe.addShapelessRecipe("itemdollarbill_100_6", bill100, itemList);

        itemList = new ItemStack[] {bill10, bill10, bill10, bill10, bill10, bill10, bill20, bill20};
        UtilRecipe.addShapelessRecipe("itemdollarbill_100_7", bill100, itemList);

        itemList = new ItemStack[] {bill10, bill10, bill10, bill10, bill10, bill50};
        UtilRecipe.addShapelessRecipe("itemdollarbill_100_8", bill100, itemList);

        itemList = new ItemStack[] {bill20, bill20, bill20, bill20, bill20};
        UtilRecipe.addShapelessRecipe("itemdollarbill_100_9", bill100, itemList);

        itemList = new ItemStack[] {bill50, bill50};
        UtilRecipe.addShapelessRecipe("itemdollarbill_100_10", bill100, itemList);


        //Output 50 Dollar
        itemList = new ItemStack[] {bill1, bill1, bill1, bill1, bill1, bill5, bill10, bill10, bill20};
        UtilRecipe.addShapelessRecipe("itemdollarbill_50_0", bill50, itemList);

        itemList = new ItemStack[] {bill5, bill5, bill10, bill10, bill20};
        UtilRecipe.addShapelessRecipe("itemdollarbill_50_1", bill50, itemList);

        itemList = new ItemStack[] {bill10, bill10, bill10, bill20};
        UtilRecipe.addShapelessRecipe("itemdollarbill_50_2", bill50, itemList);

        itemList = new ItemStack[] {bill10, bill20, bill20};
        UtilRecipe.addShapelessRecipe("itemdollarbill_50_3", bill50, itemList);

        itemList = new ItemStack[] {bill1, bill1, bill1, bill1, bill1, bill5, bill20, bill20};
        UtilRecipe.addShapelessRecipe("itemdollarbill_50_4", bill50, itemList);

        itemList = new ItemStack[] {bill5, bill5, bill20, bill20};
        UtilRecipe.addShapelessRecipe("itemdollarbill_50_5", bill50, itemList);

        bill50 = new ItemStack(ModItems.itemBanknote,2,4);
        itemList = new ItemStack[] {bill100};
        UtilRecipe.addShapelessRecipe("itemdollarbill_50_6", bill50, itemList);


        //Reset to 1 stack size
        bill50 = new ItemStack(ModItems.itemBanknote,1,4);


        //Output 20 Dollar
        itemList = new ItemStack[] {bill1, bill1, bill1, bill1, bill1, bill5, bill10};
        UtilRecipe.addShapelessRecipe("itemdollarbill_20_0", bill20, itemList);

        itemList = new ItemStack[] {bill5, bill5, bill10};
        UtilRecipe.addShapelessRecipe("itemdollarbill_20_1", bill20, itemList);

        itemList = new ItemStack[] {bill10, bill10};
        UtilRecipe.addShapelessRecipe("itemdollarbill_20_2", bill20, itemList);

        bill20 = new ItemStack(ModItems.itemBanknote,5,3);
        itemList = new ItemStack[] {bill100};
        UtilRecipe.addShapelessRecipe("itemdollarbill_20_3", bill20, itemList);


        //Reset to 1 stack size
        bill20 = new ItemStack(ModItems.itemBanknote,1,3);


        //Output 10 Dollar
        itemList = new ItemStack[] {bill1, bill1, bill1, bill1, bill1, bill5};
        UtilRecipe.addShapelessRecipe("itemdollarbill_10_0", bill10, itemList);

        itemList = new ItemStack[] {bill5, bill5};
        UtilRecipe.addShapelessRecipe("itemdollarbill_10_1", bill10, itemList);

        bill10 = new ItemStack(ModItems.itemBanknote,2,2);
        itemList = new ItemStack[] {bill20};
        UtilRecipe.addShapelessRecipe("itemdollarbill_10_2", bill10, itemList);

        bill10 = new ItemStack(ModItems.itemBanknote,5,2);
        itemList = new ItemStack[] {bill50};
        UtilRecipe.addShapelessRecipe("itemdollarbill_10_3", bill10, itemList);

        bill10 = new ItemStack(ModItems.itemBanknote,10,2);
        itemList = new ItemStack[] {};
        UtilRecipe.addShapelessRecipe("itemdollarbill_10_4", bill10, itemList);


        //Reset to 1 stack size
        bill10 = new ItemStack(ModItems.itemBanknote,1,2);


        //Output 5 Dollar
        itemList = new ItemStack[] {bill1, bill1, bill1, bill1 ,bill1};
        UtilRecipe.addShapelessRecipe("itemdollarbill_5_0", bill5, itemList);

        bill5 = new ItemStack(ModItems.itemBanknote,2,1);
        itemList = new ItemStack[] {bill10};
        UtilRecipe.addShapelessRecipe("itemdollarbill_5_1", bill5, itemList);

        bill5 = new ItemStack(ModItems.itemBanknote,4,1);
        itemList = new ItemStack[] {bill20};
        UtilRecipe.addShapelessRecipe("itemdollarbill_5_2", bill5, itemList);

        bill5 = new ItemStack(ModItems.itemBanknote,10,1);
        itemList = new ItemStack[] {bill50};
        UtilRecipe.addShapelessRecipe("itemdollarbill_5_3", bill5, itemList);

        bill5 = new ItemStack(ModItems.itemBanknote,20,1);
        itemList = new ItemStack[] {bill100};
        UtilRecipe.addShapelessRecipe("itemdollarbill_5_4", bill5, itemList);


        //Reset to 1 stack size
        bill5 = new ItemStack(ModItems.itemBanknote,1,1);


        //Output 1 Dollar
        bill1 = new ItemStack(ModItems.itemBanknote,5, 0);
        itemList = new ItemStack[] {bill5};
        UtilRecipe.addShapelessRecipe("itemdollarbill_1_0", bill1, itemList);

        bill1 = new ItemStack(ModItems.itemBanknote,10, 0);
        itemList = new ItemStack[] {bill10};
        UtilRecipe.addShapelessRecipe("itemdollarbill_1_1", bill1, itemList);

        bill1 = new ItemStack(ModItems.itemBanknote,20, 0);
        itemList = new ItemStack[] {bill20};
        UtilRecipe.addShapelessRecipe("itemdollarbill_1_2", bill1, itemList);

        bill1 = new ItemStack(ModItems.itemBanknote,50, 0);
        itemList = new ItemStack[] {bill50};
        UtilRecipe.addShapelessRecipe("itemdollarbill_1_3", bill1, itemList);
    }




    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "item." + this.getRegistryName().toString() + "_" + stack.getItemDamage();
    }

    @Override
    public void getSubItems(CreativeTabs itemIn, NonNullList<ItemStack> tab) {
        for(int i = 0; i < noteLength; i++){
            ItemStack stack = new ItemStack(this,1,i);
            tab.add(stack);
        }
    }
}