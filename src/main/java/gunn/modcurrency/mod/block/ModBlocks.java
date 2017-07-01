package gunn.modcurrency.mod.block;

import gunn.modcurrency.mod.ModConfig;
import net.minecraft.block.Block;
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
 * File Created on 2016-10-30.
 */
public class ModBlocks {
    public static BlockVending blockVending = new BlockVending();
    public static BlockExchanger blockExchanger = new BlockExchanger();

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        if(ModConfig.enableSeller) event.getRegistry().register(blockExchanger);
        if(ModConfig.enableVendor) event.getRegistry().register(blockVending);
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event){
        if(ModConfig.enableSeller) event.getRegistry().register(Item.getItemFromBlock(blockExchanger));
        if(ModConfig.enableVendor) event.getRegistry().register(Item.getItemFromBlock(blockVending));
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event){
        if(ModConfig.enableSeller) blockExchanger.initModel();
        if(ModConfig.enableVendor) blockVending.initModel();
    }

}
