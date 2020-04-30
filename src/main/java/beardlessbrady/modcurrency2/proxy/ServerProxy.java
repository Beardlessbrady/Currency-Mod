package beardlessbrady.modcurrency2.proxy;

import beardlessbrady.modcurrency2.network.PacketHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-02-07
 */

public class ServerProxy extends CommonProxy{
    public void preInit(FMLPreInitializationEvent e){
        super.preInit(e);
        PacketHandler.registerServerMessages("modcurrency");
    }

    public void Init(FMLInitializationEvent e){
        super.Init(e);
    }


    public void postInit(FMLPostInitializationEvent e){
        super.postInit(e);
    }
}
