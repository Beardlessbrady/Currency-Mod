package gunn.modcurrency.mod.item;

import gunn.modcurrency.mod.ModConfig;
import gunn.modcurrency.mod.ModCurrency;
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

    public ItemBanknote(){
        setHasSubtypes(true);
        setRegistryName("banknote");
        setCreativeTab(ModCurrency.tabCurrency);
        GameRegistry.register(this);
    }

    @SideOnly(Side.CLIENT)
    public void initModel(){
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

    public void recipe(){
        ItemStack bill1 = new ItemStack(ModItems.itemBanknote,1, 0);
        ItemStack bill5 = new ItemStack(ModItems.itemBanknote,1,1);
        ItemStack bill10 = new ItemStack(ModItems.itemBanknote,1,2);
        ItemStack bill20 = new ItemStack(ModItems.itemBanknote,1,3);
        ItemStack bill50 = new ItemStack(ModItems.itemBanknote,1,4);
        ItemStack bill100 = new ItemStack(ModItems.itemBanknote,1,5);

        //Output 100 Dollar
        GameRegistry.addShapelessRecipe(bill100, bill1, bill1, bill1, bill1, bill1, bill5, bill20, bill20, bill50);
        GameRegistry.addShapelessRecipe(bill100, bill5, bill5, bill20, bill20, bill50);
        GameRegistry.addShapelessRecipe(bill100, bill5, bill5, bill10, bill10, bill20, bill50);
        GameRegistry.addShapelessRecipe(bill100, bill5, bill5, bill10, bill10, bill10, bill10, bill50);
        GameRegistry.addShapelessRecipe(bill100, bill5, bill5, bill10, bill10, bill10, bill10, bill10, bill20, bill20);
        GameRegistry.addShapelessRecipe(bill100, bill10, bill20, bill20, bill50);
        GameRegistry.addShapelessRecipe(bill100, bill10, bill10, bill10, bill10, bill10, bill10, bill20, bill20);
        GameRegistry.addShapelessRecipe(bill100, bill10, bill10, bill10, bill10, bill10, bill50);
        GameRegistry.addShapelessRecipe(bill100, bill20, bill20, bill20, bill20, bill20);
        GameRegistry.addShapelessRecipe(bill100, bill50, bill50);

        //Output 50 Dollar
        GameRegistry.addShapelessRecipe(bill50, bill1, bill1, bill1, bill1, bill1, bill5, bill10, bill10, bill20);
        GameRegistry.addShapelessRecipe(bill50, bill5, bill5, bill10, bill10, bill20);
        GameRegistry.addShapelessRecipe(bill50, bill10, bill10, bill10, bill20);
        GameRegistry.addShapelessRecipe(bill50, bill10, bill20, bill20);
        GameRegistry.addShapelessRecipe(bill50, bill1, bill1, bill1, bill1, bill1, bill5, bill20, bill20);
        GameRegistry.addShapelessRecipe(bill50, bill5, bill5, bill20, bill20);
        bill50 = new ItemStack(ModItems.itemBanknote,2,4);
        GameRegistry.addShapelessRecipe(bill50, bill100);

        //Reset to 1 stack size
        bill50 = new ItemStack(ModItems.itemBanknote,1,4);

        //Output 20 Dollar
        GameRegistry.addShapelessRecipe(bill20, bill1, bill1, bill1, bill1, bill1, bill5, bill10);
        GameRegistry.addShapelessRecipe(bill20, bill5, bill5, bill10);
        GameRegistry.addShapelessRecipe(bill20, bill10, bill10);
        bill20 = new ItemStack(ModItems.itemBanknote,5,3);
        GameRegistry.addShapelessRecipe(bill20, bill100);

        //Reset to 1 stack size
        bill20 = new ItemStack(ModItems.itemBanknote,1,3);

        //Output 10 Dollar
        GameRegistry.addShapelessRecipe(bill10, bill1, bill1, bill1, bill1, bill1, bill5);
        GameRegistry.addShapelessRecipe(bill10, bill5, bill5);
        bill10 = new ItemStack(ModItems.itemBanknote,2,2);
        GameRegistry.addShapelessRecipe(bill10, bill20);
        bill10 = new ItemStack(ModItems.itemBanknote,5,2);
        GameRegistry.addShapelessRecipe(bill10, bill50);
        bill10 = new ItemStack(ModItems.itemBanknote,10,2);
        GameRegistry.addShapelessRecipe(bill10, bill100);

        //Reset to 1 stack size
        bill10 = new ItemStack(ModItems.itemBanknote,1,2);

        //Output 5 Dollar
        GameRegistry.addShapelessRecipe(bill5, bill1, bill1, bill1, bill1 ,bill1);
        bill5 = new ItemStack(ModItems.itemBanknote,2,1);
        GameRegistry.addShapelessRecipe(bill5, bill10);
        bill5 = new ItemStack(ModItems.itemBanknote,4,1);
        GameRegistry.addShapelessRecipe(bill5, bill20);
        bill5 = new ItemStack(ModItems.itemBanknote,10,1);
        GameRegistry.addShapelessRecipe(bill5, bill50);
        bill5 = new ItemStack(ModItems.itemBanknote,20,1);
        GameRegistry.addShapelessRecipe(bill5, bill100);

        //Reset to 1 stack size
        bill5 = new ItemStack(ModItems.itemBanknote,1,1);

        //Output 1 Dollar
        bill1 = new ItemStack(ModItems.itemBanknote,5, 0);
        GameRegistry.addShapelessRecipe(bill1,bill5);
        bill1 = new ItemStack(ModItems.itemBanknote,10, 0);
        GameRegistry.addShapelessRecipe(bill1,bill10);
        bill1 = new ItemStack(ModItems.itemBanknote,20, 0);
        GameRegistry.addShapelessRecipe(bill1,bill20);
        bill1 = new ItemStack(ModItems.itemBanknote, 50, 0);
        GameRegistry.addShapelessRecipe(bill1,bill50);
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