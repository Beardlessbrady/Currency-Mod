package beardlessbrady.modcurrency2;

import beardlessbrady.modcurrency2.command.CurrencyCommand;
import beardlessbrady.modcurrency2.proxy.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-02-07
 */

@Mod(modid = ModCurrency.MODID, name = ModCurrency.MODNAME, version = ModCurrency.VERSION)
public class ModCurrency {
    public static final String MODID = "modcurrency2";
    public static final String MODNAME = "Good Ol' Currency";
    public static final String VERSION = "1.12.0-2.0.0-ALPHA-20200430";

    public static CreativeTabs tabCurrency = new TabCurrency("Good ol' Currency");

    @SidedProxy(clientSide = "beardlessbrady.modcurrency2.proxy.ClientProxy", serverSide = "beardlessbrady.modcurrency2.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.Instance
    public static ModCurrency instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event){
        proxy.Init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event){
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CurrencyCommand());
    }
}