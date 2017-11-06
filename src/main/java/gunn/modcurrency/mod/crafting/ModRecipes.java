package gunn.modcurrency.mod.crafting;

import gunn.modcurrency.mod.ModCurrency;
import gunn.modcurrency.mod.crafting.recipe.BundledBagRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-11-06
 */
public class ModRecipes {

    @SubscribeEvent
    public void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        event.getRegistry().register(new BundledBagRecipe().setRegistryName(new ResourceLocation(ModCurrency.MODID, "bundled_bag_recipes")));
    }
}
