package beardlessbrady.modcurrency2.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-02-22
 */

public class PacketHandler {
    public static SimpleNetworkWrapper INSTANCE = null;

    public static void registerClientMessages(String channelName){
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);
        registerClientMessages(registerCommonMessages(0));
    }

    public static void registerServerMessages(String channelName){
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);
        registerServerMessages(registerCommonMessages(0));
    }

    public static int registerCommonMessages(int index){
        INSTANCE.registerMessage(PacketSetFieldToServer.Handler.class, PacketSetFieldToServer.class, index++, Side.SERVER);
        INSTANCE.registerMessage(PacketSetItemToServer.Handler.class, PacketSetItemToServer.class, index++, Side.SERVER);
        INSTANCE.registerMessage(PacketSetItemBundleToServer.Handler.class, PacketSetItemBundleToServer.class, index++, Side.SERVER);
        INSTANCE.registerMessage(PacketOutChangeToServer.Handler.class, PacketOutChangeToServer.class, index++, Side.SERVER);
        INSTANCE.registerMessage(PacketSendKeyToServer.Handler.class, PacketSendKeyToServer.class, index++, Side.SERVER);
        return index;
    }

    public static void registerClientMessages(int index){

    }

    public static void registerServerMessages(int index){

    }
}
