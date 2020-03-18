package beardlessbrady.modcurrency.item;

import beardlessbrady.modcurrency.item.playercurrency.ItemPlayerCurrency;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-02-07
 */

public class ModItems {
    public static ItemCurrency itemCurrency = new ItemCurrency();
    public static ItemPlayerCurrency itemPlayerCurrency = new ItemPlayerCurrency();

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event){
        event.getRegistry().register(itemCurrency);
        event.getRegistry().register(itemPlayerCurrency);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event){
        itemCurrency.initModel();
        itemPlayerCurrency.initModel();
        itemPlayerCurrency.registerCurrencyVariants(); // For special layered item texture
    }
}
