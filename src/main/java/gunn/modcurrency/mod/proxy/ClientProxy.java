package gunn.modcurrency.mod.proxy;

import gunn.modcurrency.mod.block.ModBlocks;
import gunn.modcurrency.mod.item.ModItems;
import gunn.modcurrency.mod.network.PacketHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Distributed with the Currency-Mod for Minecraft.
 * Copyright (C) 2016  Brady Gunn
 *
 * File Created on 2016-10-28.
 */
public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        ModItems.ItemModels();
        ModBlocks.ItemModels();
    }

    @Override
    public void Init(FMLInitializationEvent e){
        super.Init(e);
    }
}