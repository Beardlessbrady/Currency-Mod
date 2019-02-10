package beardlessbrady.modcurrency.block;

import beardlessbrady.modcurrency.block.vending.BlockVending;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-02-08
 */

public class ModBlocks {
    public static BlockVending blockVending = new BlockVending();

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event){
        event.getRegistry().register(blockVending);
    }

    @SubscribeEvent
    public void registerBlockItems(RegistryEvent.Register<Item> event){
        event.getRegistry().register(new ItemBlock(blockVending).setRegistryName(blockVending.getRegistryName()));
    }

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event){

    }
}
