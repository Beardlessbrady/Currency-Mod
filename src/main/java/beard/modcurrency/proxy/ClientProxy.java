package beard.modcurrency.proxy;

import beard.modcurrency.ModCurrency;
import beard.modcurrency.client.BakedHandler;
import beard.modcurrency.item.EnumCurrencyPrime;
import beard.modcurrency.item.EnumCurrencyShape;
import beard.modcurrency.item.ItemColorCurrency;
import beard.modcurrency.item.ItemCurrency;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2018-02-16
 */
public class ClientProxy extends CommonProxy{

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
       MinecraftForge.EVENT_BUS.register(new BakedHandler());
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    @Override
    public void registerItems(IForgeRegistry<Item> registry, Item item) {
        super.registerItems(registry, item);

        ModelResourceLocation main = new ModelResourceLocation(item.getRegistryName(), "inventory");
        ModelLoader.setCustomModelResourceLocation(item, 0, main);
    }

    @Override
    public void registerCurrencyVariants(){
        Item item = ModCurrency.itemCurrency;

        //Register Currency Shapes
        for(int i = 0; i < EnumCurrencyShape.values().length; i++){
            ModelBakery.registerItemVariants(item, new ModelResourceLocation(item.getRegistryName() + "shape/" + EnumCurrencyShape.values()[i].getName(), "inventory"));
        }

        //Register Currency Prime Details
        for(int i = 0; i < EnumCurrencyShape.values().length; i++){
            ModelBakery.registerItemVariants(item, new ModelResourceLocation(item.getRegistryName() + "primedetail/" + EnumCurrencyPrime.values()[i].getName(), "inventory"));
        }
    }
}
