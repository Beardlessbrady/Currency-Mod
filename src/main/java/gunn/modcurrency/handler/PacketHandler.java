package gunn.modcurrency.handler;

import gunn.modcurrency.network.PacketSendIntData;
import gunn.modcurrency.network.PacketSendItemToServer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class was created by <Brady Gunn>.
 * Distributed with the Currency-Mod for Minecraft.
 *
 * The Currency-Mod is open source and distributed under a
 * Custom License: https://github.com/BeardlessBrady/Currency-Mod/blob/master/LICENSE
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
        INSTANCE.registerMessage(PacketSendIntData.Handler.class, PacketSendIntData.class, nextID(), Side.SERVER);
    }
}
