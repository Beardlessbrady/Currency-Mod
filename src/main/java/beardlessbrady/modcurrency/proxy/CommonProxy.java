package beardlessbrady.modcurrency.proxy;

import beardlessbrady.modcurrency.ModConfig;
import beardlessbrady.modcurrency.item.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-02-07
 */

public class CommonProxy {
    public static Configuration config;

    public void preInit(FMLPreInitializationEvent e){
        File directory = e.getModConfigurationDirectory();
        config = new Configuration(new File(directory.getPath(), "currency.cfg"));
        ModConfig.readConfig();

        MinecraftForge.EVENT_BUS.register(new ModItems());
    }

    public void Init(FMLInitializationEvent e){
    }


    public void postInit(FMLPostInitializationEvent e){
    }
}
