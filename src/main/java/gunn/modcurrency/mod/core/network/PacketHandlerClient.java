package gunn.modcurrency.mod.core.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Distributed with the Currency-Mod for Minecraft.
 * Copyright (C) 2016  Brady Gunn
 *
 * File Created on 2016-11-06.
 */
public class PacketHandlerClient {
    public static SimpleNetworkWrapper INSTANCE = null;

    public PacketHandlerClient(){}
    
    public static void registerMessages(String channelName){
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);
        registerMessages(0);
    }
    
    public static void registerMessages(int index){
        INSTANCE.registerMessage(PacketSyncBankDataToClient.Handler.class, PacketSyncBankDataToClient.class, index++, Side.CLIENT);
    }
}
