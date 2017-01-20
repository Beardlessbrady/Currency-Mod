package gunn.modcurrency;

import gunn.modcurrency.common.core.proxy.CommonProxy;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Level;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

    public static boolean recipeVendor = true;
    public static boolean recipeSeller = true;
    public static boolean recipeWallet = true;

    public static boolean invincibleVendSell = true;
    public static int walletSize = 4;



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
        cfg.addCustomCategoryComment(CATEGORY_ITEMS, "These configs enable/disable a lot of main features of the mod.");
        enableVendor = cfg.getBoolean("Vending Machine", CATEGORY_ITEMS, enableVendor, "Block");
        enableSeller = cfg.getBoolean("Exchange Machine", CATEGORY_ITEMS, enableSeller, "Block");
        enableWallet = cfg.getBoolean("Wallet", CATEGORY_ITEMS, enableWallet, "Item");
    }

    private static void initRecipesConfig(Configuration cfg){
        cfg.addCustomCategoryComment(CATEGORY_RECIPES, "These configs enable/disable recipes of blocks and items in the mod.");
        recipeVendor = cfg.getBoolean("Vending Machine", CATEGORY_RECIPES, recipeVendor, "Block");
        recipeSeller = cfg.getBoolean("Exchange Machine", CATEGORY_RECIPES, recipeSeller, "Block");
        recipeWallet = cfg.getBoolean("Wallet", CATEGORY_RECIPES, recipeWallet, "Item");
    }

    private static void initgeneralConfig(Configuration cfg){
        cfg.addCustomCategoryComment(CATEGORY_GENERAL, "These configs modify the balance of some items and blocks in the mod");
        invincibleVendSell = cfg.getBoolean("Invincible Machine", CATEGORY_GENERAL, invincibleVendSell, "Enabling this makes it so only the player who placed the machine can break it");
        walletSize = cfg.getInt("Wallet Size", CATEGORY_GENERAL, walletSize, 0, 4, "This changes how many rows of inventory slots there are in the wallet");
    }
}
