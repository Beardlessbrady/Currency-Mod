package beardlessbrady.modcurrency2;

import beardlessbrady.modcurrency2.proxy.CommonProxy;
import net.minecraftforge.common.config.Configuration;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-02-07
 */

public class ConfigCurrency {
    private static final String CATEGORY_CURRENCY = "dynamic currency";

    public static String[] currencyValues = {"0.01", "0.05", "0.10" , "0.25 " , "1 ", "2 ", "1" , "5" , "10" , "20" , "50" , "100"};
    public static String[] currencyNames = {"One Cent", "Five Cents", "Ten Cents", "Twenty-Five Cents", "One Dollar", "Two Dollars",
            "One Dollar", "Five Dollars", "Ten Dollars", "Twenty Dollars", "Fifty Dollars", "One-Hundred Dollars"};

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
        cfg.addCustomCategoryComment(CATEGORY_CURRENCY, "Configure the value and name of the currencies in the mod.\nYou can add more elements then the default but MAKE SURE TO ADD TO BOTH VALUES AND NAMES.");

        currencyValues = cfg.getStringList("Currency Values", CATEGORY_CURRENCY, currencyValues , "Set currency values");
        currencyNames = cfg.getStringList("Currency Names", CATEGORY_CURRENCY, currencyNames, "Set currency names");
    }
}
