package beardlessbrady.modcurrency;

import beardlessbrady.modcurrency.proxy.CommonProxy;
import net.minecraftforge.common.config.Configuration;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-02-07
 */

public class ModConfig {
    private static final String CATEGORY_ITEMS = "Item & Blocks";

    public static String[] currencyArray = {"0.01", "0.05", "0.10" , "0.25 " , "1 ", "2 ", "1" , "5" , "10" , "20" , "50" , "100"};

    public static void readConfig(){
        Configuration cfg = CommonProxy.config;
        try{
            cfg.load();
            initItemsConfig(cfg);
        }catch (Exception e){
        } finally {
            if (cfg.hasChanged()){
                cfg.save();
            }
        }
    }

    private static void initItemsConfig(Configuration cfg){
        cfg.addCustomCategoryComment(CATEGORY_ITEMS, "Configure items in the mod");

        cfg.getStringList("Currencies", CATEGORY_ITEMS, currencyArray , "Set your currencies here!");
    }
}
