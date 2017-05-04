package gunn.modcurrency.mod.block;

import gunn.modcurrency.mod.ModConfig;

/**
 * Distributed with the Currency-Mod for Minecraft.
 * Copyright (C) 2016  Brady Gunn
 *
 * File Created on 2016-10-30.
 */
public class ModBlocks {
    public static BlockTop blockTop;
    public static BlockVendor blockVendor;
    public static BlockSeller blockSeller;
    public static BlockATM blockATM;
    public static BlockShopMob blockShopMob;

    public static void preInit(){
        setupBlocks();
    }

    private static void setupBlocks(){
        if(ModConfig.enableVendor) blockVendor = new BlockVendor();
        if(ModConfig.enableSeller) blockSeller = new BlockSeller();
        if(ModConfig.enableVendor || ModConfig.enableSeller) blockTop = new BlockTop();
        if(ModConfig.enableATM) blockATM = new BlockATM();

        //blockShopMob = new BlockShopMob();
    }

    public static void ItemModels(){
        if(ModConfig.enableVendor) blockVendor.initModel();
        if(ModConfig.enableSeller) blockSeller.initModel();
        if(ModConfig.enableATM) blockATM.initModel();
    }
    
    public static void addRecipes(){
        if(ModConfig.enableVendor && ModConfig.recipeVendor) blockVendor.recipe();
        if(ModConfig.enableSeller && ModConfig.recipeSeller) blockSeller.recipe();
        if(ModConfig.enableATM && ModConfig.recipeATM) blockATM.recipe();
    }
}
