package beardlessbrady.modcurrency2.proxy;

import beardlessbrady.modcurrency2.ConfigCurrency;
import beardlessbrady.modcurrency2.ModCurrency;
import beardlessbrady.modcurrency2.block.ModBlocks;
import beardlessbrady.modcurrency2.handler.EventHandler;
import beardlessbrady.modcurrency2.handler.GuiHandler;
import beardlessbrady.modcurrency2.item.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

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
        ConfigCurrency.readConfig();

        MinecraftForge.EVENT_BUS.register(new ModItems());
        MinecraftForge.EVENT_BUS.register(new ModBlocks());

        NetworkRegistry.INSTANCE.registerGuiHandler(ModCurrency.instance, new GuiHandler());

    }

    public void Init(FMLInitializationEvent e){
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }


    public void postInit(FMLPostInitializationEvent e){
        if (config.hasChanged()) {
            config.save();
        }
    }

}
