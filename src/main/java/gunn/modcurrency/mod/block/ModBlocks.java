package gunn.modcurrency.mod.block;

import gunn.modcurrency.mod.ModConfig;

/**
 * Distributed with the Currency-Mod for Minecraft.
 * Copyright (C) 2016  Brady Gunn
 *
 * File Created on 2016-10-30.
 */
public class ModBlocks {
    public static BlockVending blockVending;
    public static BlockATM blockATM;
    //public static BlockEntityMarket blockEntityMarket;

    public static void preInit(){
        setupBlocks();
    }

    private static void setupBlocks(){
        if(ModConfig.enableVendor) blockVending = new BlockVending();
        if(ModConfig.enableATM) blockATM = new BlockATM();
        //blockEntityMarket = new BlockEntityMarket();
    }

    public static void ItemModels(){
        if(ModConfig.enableVendor)  blockVending.initModel();
        if(ModConfig.enableATM) blockATM.initModel();
    }
    
    public static void addRecipes(){
        if(ModConfig.enableVendor && ModConfig.recipeVendor) blockVending.recipe();
        if(ModConfig.enableATM && ModConfig.recipeATM) blockATM.recipe();
    }
}
