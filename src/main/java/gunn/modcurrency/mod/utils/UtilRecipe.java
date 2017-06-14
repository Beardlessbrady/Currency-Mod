package gunn.modcurrency.mod.utils;

import gunn.modcurrency.mod.ModCurrency;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-06-14
 */
public final class UtilRecipe {

    public static void addShapedRecipe(String name, int w, int h, ItemStack output, ItemStack[] ingredientList) {
        NonNullList<Ingredient> ingredients = NonNullList.create();

        for (int i = 0; i < ingredientList.length; i++) {
            if(ingredientList[i] == ItemStack.EMPTY){
                ingredients.add(i, Ingredient.EMPTY);
            }else {
                ingredients.add(i, Ingredient.fromStacks((ingredientList[i])));
            }
        }

        CraftingManager.register(new ResourceLocation(ModCurrency.MODID, "recipe" + name), new ShapedRecipes(name, w, h, ingredients, output));
    }

    public static void addShapelessRecipe(String name, ItemStack output, ItemStack[] ingredientList) {
        NonNullList<Ingredient> ingredients = NonNullList.create();

        for (int i = 0; i < ingredientList.length; i++) {
            ingredients.add(i, Ingredient.EMPTY);
        }

        CraftingManager.register(new ResourceLocation(ModCurrency.MODID, "recipe" + name), new ShapelessRecipes(name, output, ingredients));
    }
}
