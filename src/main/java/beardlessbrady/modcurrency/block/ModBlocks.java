package beardlessbrady.modcurrency.block;

import beardlessbrady.modcurrency.block.vending.BlockVending;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
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
 * File Created 2019-02-08
 */

public class ModBlocks {
    public static final BlockVending blockVending = new BlockVending();
    EconomyBlockBase[] blocks = {blockVending};

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event){
        for(int i = 0; i < blocks.length; i++)
            registerBlock(event, blocks[i]);
    }

    @SubscribeEvent
    public void registerBlockItems(RegistryEvent.Register<Item> event){
        for(int i = 0; i < blocks.length; i++)
            registerItems(event, blocks[i]);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void registerModels(ModelRegistryEvent event){
        for(int i = 0; i < blocks.length; i++)
            registerModel(blocks[i]);
    }


    public void registerBlock(RegistryEvent.Register<Block> event, EconomyBlockBase block){
        event.getRegistry().register(block);
        block.registerTileEntity();
    }

    public void registerItems(RegistryEvent.Register<Item> event, EconomyBlockBase block){
        event.getRegistry().register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
    }

    public void registerModel(EconomyBlockBase block){
        block.registerModel();
    }
}
