package beard.modcurrency.proxy;

import beard.modcurrency.ModCurrency;
import beard.modcurrency.item.ItemColorCurrency;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2018-02-16
 */
public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event){

    }

    public void init(FMLInitializationEvent event){
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new ItemColorCurrency(), ModCurrency.itemCurrency);
    }

    public void postInit(FMLPostInitializationEvent event){

    }

    public void registerItems(IForgeRegistry<Item> registry, Item item)
    {
        registry.register(item);
    }

    public void registerCurrencyVariants(){
      //stay empty
    }
}
