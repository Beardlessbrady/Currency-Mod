package gunn.modcurrency.mod.network;

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
    public static SimpleNetworkWrapper INSTANCE = null;
    
    public PacketHandler(){}
    
    public static void registerCommonMessages(String channelName){
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);
        registerCommonMessages(0);
    }

    public static void registerCommonMessages(int index){
        INSTANCE.registerMessage(PacketItemSpawnToServer.Handler.class, PacketItemSpawnToServer.class, index++, Side.SERVER);
        INSTANCE.registerMessage(PacketSetItemCostToServer.Handler.class, PacketSetItemCostToServer.class, index++, Side.SERVER);
        INSTANCE.registerMessage(PacketSetItemAmountToServer.Handler.class, PacketSetItemAmountToServer.class, index++, Side.SERVER);
        INSTANCE.registerMessage(PacketBankDepositToServer.Handler.class, PacketBankDepositToServer.class, index++, Side.SERVER);
        INSTANCE.registerMessage(PacketBankWithdrawToServer.Handler.class, PacketBankWithdrawToServer.class, index++, Side.SERVER);
        INSTANCE.registerMessage(PacketSetFieldToServer.Handler.class, PacketSetFieldToServer.class, index++, Side.SERVER);
       // INSTANCE.registerMessage(PacketSyncBankDataToClient.Handler.class, PacketSyncBankDataToClient.class, index++, Side.CLIENT);
    }
}
