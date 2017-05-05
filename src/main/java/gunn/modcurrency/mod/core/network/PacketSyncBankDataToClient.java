package gunn.modcurrency.mod.core.network;

import gunn.modcurrency.mod.core.data.BankAccount;
import gunn.modcurrency.mod.core.data.BankAccountSavedData;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.World;
import net.minecraftforge.common.util.WorldCapabilityData;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-03-12
 */
public class PacketSyncBankDataToClient implements IMessage {
    //Player changes gear tab state by clicking it, sends this to the server
    private int balance;
    private String name;


    public PacketSyncBankDataToClient(){}

    public void setData(BankAccount acc) {
        this.balance = acc.getBalance();
        this.name = acc.getName();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        name = ByteBufUtils.readUTF8String(buf);
        balance = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, name);
        buf.writeInt(balance);
    }

    public static class Handler implements IMessageHandler<PacketSyncBankDataToClient, IMessage> {

        @Override
        public IMessage onMessage(final PacketSyncBankDataToClient message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message,ctx));
            return null;
        }

        private void handle(PacketSyncBankDataToClient message, MessageContext ctx){
            WorldClient world = Minecraft.getMinecraft().world;
            BankAccountSavedData data = BankAccountSavedData.getData(world);
            data.setBankAccount(new BankAccount(message.name, message.balance));
        }
    }
}