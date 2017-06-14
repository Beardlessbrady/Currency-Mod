package gunn.modcurrency.mod.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-06-14
 */
public final class UtilRecipe {

    public static void addShapedRecipe(ResourceLocation registryName, String name, int w, int h, ItemStack output, ItemStack[] ingredientList) {
        NonNullList<Ingredient> ingredients = NonNullList.create();

        for (int i = 0; i < ingredientList.length; i++) {
            ingredients.add(i, Ingredient.func_193369_a((ingredientList[i])));
        }

        CraftingManager.func_193372_a(registryName, new ShapedRecipes(name, w, h, ingredients, output));
    }
}
