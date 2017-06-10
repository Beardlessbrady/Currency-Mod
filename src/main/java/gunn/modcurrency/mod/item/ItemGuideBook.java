package gunn.modcurrency.mod.item;

import gunn.modcurrency.mod.ModCurrency;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-06-10
 */
public class ItemGuideBook extends Item {
    public ItemGuideBook() {
        setRegistryName("guidebook");
        setUnlocalizedName(this.getRegistryName().toString());
        setCreativeTab(ModCurrency.tabCurrency);
        GameRegistry.register(this);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    public void recipe() {
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.itemGuide, 1, 0),
                Items.BOOK, Items.GOLD_INGOT);
    }
}
