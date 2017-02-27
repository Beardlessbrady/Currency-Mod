package gunn.modcurrency.common.core.handler;

import gunn.modcurrency.common.core.network.PacketSendIntDataToClient;
import gunn.modcurrency.common.core.network.PacketSendIntDataToServer;
import gunn.modcurrency.common.core.network.PacketSendItemToServer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Distributed with the Currency-Mod for Minecraft.
 * Copyright (C) 2016  Brady Gunn
 *
 * File Created on 2016-11-06.
 */
public class PacketHandler {
    private static int packetId = 0;
    public static SimpleNetworkWrapper INSTANCE = null;
    
    public PacketHandler(){}
    
    public static int nextID(){
        return packetId++;
    }
    
    public static void registerMessages(String channelName){
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);
        registerMessages();
    }
    
    public static void registerMessages(){
        INSTANCE.registerMessage(PacketSendItemToServer.Handler.class, PacketSendItemToServer.class, nextID(), Side.SERVER);
        INSTANCE.registerMessage(PacketSendIntDataToServer.Handler.class, PacketSendIntDataToServer.class, nextID(), Side.SERVER);
        INSTANCE.registerMessage(PacketSendIntDataToClient.Handler.class, PacketSendIntDataToClient.class, nextID(), Side.CLIENT);
    }
}
