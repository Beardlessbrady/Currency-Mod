package gunn.modcurrency.mod;

import gunn.modcurrency.mod.proxy.CommonProxy;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Level;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-01-20
 */
public class ModConfig {
    private static final String CATEGORY_ITEMS = "Items & Blocks";
    private static final String CATEGORY_RECIPES = "Recipes";
    private static final String CATEGORY_GENERAL = "general";

    public static boolean enableVendor = true;
    public static boolean enableSeller = true;
    public static boolean enableWallet = true;
    public static boolean enableATM = true;

    public static boolean recipeVendor = true;
    public static boolean recipeSeller = true;
    public static boolean recipeWallet = true;
    public static boolean recipeATM = true;


    public static int textureType = 0;

    public static boolean invincibleVendSell = true;
    public static int walletSize = 4;
    public static boolean dropATM = false;



    public static void readConfig(){
        Configuration cfg = CommonProxy.config;
        try {
            cfg.load();
            initRecipesConfig(cfg);
            initItemsConfig(cfg);
            initgeneralConfig(cfg);

        }catch(Exception e){
            ModCurrency.logger.log(Level.ERROR, "Problem loading config file", e);
        } finally {
            if (cfg.hasChanged()){
                cfg.save();
            }
        }
    }

    private static void initItemsConfig(Configuration cfg){
        cfg.addCustomCategoryComment(CATEGORY_ITEMS, "These configs enable/disable a lot of main features of the old.");
        enableVendor = cfg.getBoolean("Vending Machine", CATEGORY_ITEMS, enableVendor, "Block");
        enableSeller = cfg.getBoolean("Exchange Machine", CATEGORY_ITEMS, enableSeller, "Block");
        enableWallet = cfg.getBoolean("Wallet", CATEGORY_ITEMS, enableWallet, "Item");
      //  enableATM = cfg.getBoolean("ATM", CATEGORY_ITEMS, enableATM, "Block");
    }

    private static void initRecipesConfig(Configuration cfg){
        cfg.addCustomCategoryComment(CATEGORY_RECIPES, "These configs enable/disable recipes of block and item in the old.");
        recipeVendor = cfg.getBoolean("Vending Machine", CATEGORY_RECIPES, recipeVendor, "Block");
        recipeSeller = cfg.getBoolean("Exchange Machine", CATEGORY_RECIPES, recipeSeller, "Block");
        recipeWallet = cfg.getBoolean("Wallet", CATEGORY_RECIPES, recipeWallet, "Item");
       // recipeATM = cfg.getBoolean("ATM", CATEGORY_RECIPES, recipeATM, "Block");
    }

    private static void initgeneralConfig(Configuration cfg){
        cfg.addCustomCategoryComment(CATEGORY_GENERAL, "These configs modify the balance/experience of the old");
        dropATM = cfg.getBoolean("Drop ATM when broken", CATEGORY_GENERAL, dropATM, "This changes if the ATM will drop itself when broken or not");
        invincibleVendSell = cfg.getBoolean("Invincible Machine", CATEGORY_GENERAL, invincibleVendSell, "Enabling this makes it so only the player who placed the machine can break it");
        walletSize = cfg.getInt("Wallet Size", CATEGORY_GENERAL, walletSize, 0, 4, "This changes how many rows of inventory slot there are in the wallet");
        textureType = cfg.getInt("Item Textures", CATEGORY_GENERAL, textureType, 0, 2,"Default=0, 16x16=1, Foolcraft=2");

    }
}
