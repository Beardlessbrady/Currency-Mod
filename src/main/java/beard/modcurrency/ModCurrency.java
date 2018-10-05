package beard.modcurrency;

import beard.modcurrency.item.ItemCurrency;
import beard.modcurrency.proxy.CommonProxy;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2018-02-16
 */

@Mod(modid = ModCurrency.MODID, name = ModCurrency.MODNAME, version = ModCurrency.VERSION)
public class ModCurrency {
    public static final String MODID = "modcurrency";
    public static final String MODNAME = "Currency Mod";
    public static final String VERSION = "1.12-2.0.0 ALPHA";


    @SidedProxy(clientSide = "beard.modcurrency.proxy.ClientProxy", serverSide = "beard.modcurrency.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.Instance
    public static ModCurrency instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event){
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event){
        proxy.postInit(event);
    }


    public static final ItemCurrency itemCurrency = new ItemCurrency();

    @Mod.EventBusSubscriber
    public static class Handler {
        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event) {
            IForgeRegistry<Item> registry = event.getRegistry();

            ModCurrency.proxy.registerItems(registry, itemCurrency);
        }
    }
}