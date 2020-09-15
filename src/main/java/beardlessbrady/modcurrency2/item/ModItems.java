package beardlessbrady.modcurrency2.item;

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

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event){
        event.getRegistry().register(itemCurrency);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event){
        itemCurrency.initModel();
    }
}
