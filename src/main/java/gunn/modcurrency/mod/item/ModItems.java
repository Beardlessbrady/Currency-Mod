package gunn.modcurrency.mod.item;

import gunn.modcurrency.mod.ModConfig;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Distributed with the Currency-Mod for Minecraft.
 * Copyright (C) 2016  Brady Gunn
 *
 * File Created on 2016-10-28.
 */
public class ModItems {
    public static ItemBanknote itemBanknote = new ItemBanknote();
    public static ItemWallet itemWallet = new ItemWallet();
    public static ItemGuideBook itemGuide = new ItemGuideBook();

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event){
        event.getRegistry().register(itemBanknote);
        event.getRegistry().register(itemGuide);
        if(ModConfig.enableWallet) event.getRegistry().register(itemWallet);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event){
        itemBanknote.initModel();
        itemGuide.initModel();
        if(ModConfig.enableWallet) itemWallet.initModel();
    }

}

