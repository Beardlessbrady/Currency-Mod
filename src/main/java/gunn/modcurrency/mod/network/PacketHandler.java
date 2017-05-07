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

    public static void registerClientMessages(String channelName){
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);
        int index = registerCommonMessages(0);
        registerClientMessages(index);
    }
    
    public static int registerCommonMessages(int index){
       // INSTANCE.registerMessage(PacketItemSpawnToServer.Handler.class, PacketItemSpawnToServer.class, index++, Side.SERVER);
       // INSTANCE.registerMessage(PacketSetItemCostToServer.Handler.class, PacketSetItemCostToServer.class, index++, Side.SERVER);
       // INSTANCE.registerMessage(PacketSetItemAmountToServer.Handler.class, PacketSetItemAmountToServer.class, index++, Side.SERVER);
        INSTANCE.registerMessage(PacketSetGearTabStateToServer.Handler.class, PacketSetGearTabStateToServer.class, index++, Side.SERVER);
       // INSTANCE.registerMessage(PacketSetLockTabToServer.Handler.class, PacketSetLockTabToServer.class, index++, Side.SERVER);
       // INSTANCE.registerMessage(PacketSetInfiniteToServer.Handler.class, PacketSetInfiniteToServer.class, index++, Side.SERVER);
        INSTANCE.registerMessage(PacketBankDepositToServer.Handler.class, PacketBankDepositToServer.class, index++, Side.SERVER);
        INSTANCE.registerMessage(PacketBankWithdrawToServer.Handler.class, PacketBankWithdrawToServer.class, index++, Side.SERVER);
        INSTANCE.registerMessage(PacketSetATMFeeToServer.Handler.class, PacketSetATMFeeToServer.class, index++, Side.SERVER);
        return index;
    }

    public static void registerClientMessages(int index){
        INSTANCE.registerMessage(PacketSyncBankDataToClient.Handler.class, PacketSyncBankDataToClient.class, index++, Side.CLIENT);
    }
}
