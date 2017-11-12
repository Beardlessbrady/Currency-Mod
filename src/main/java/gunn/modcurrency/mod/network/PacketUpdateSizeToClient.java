package gunn.modcurrency.mod.network;

import gunn.modcurrency.mod.tileentity.TileExchanger;
import gunn.modcurrency.mod.tileentity.TileVending;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-10-13
 */
public class PacketUpdateSizeToClient implements IMessage {
    private BlockPos blockPos;
    private int data;
    private int slot;

    public PacketUpdateSizeToClient(){}

    public void setData(BlockPos pos, int slot, int data) {
        this.blockPos = pos;
        this.slot = slot;
        this.data = data;

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        blockPos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        slot = buf.readInt();
        data = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(blockPos.getX());
        buf.writeInt(blockPos.getY());
        buf.writeInt(blockPos.getZ());
        buf.writeInt(slot);
        buf.writeInt(data);
    }

    public static class Handler implements IMessageHandler<PacketUpdateSizeToClient, IMessage> {

        @Override
        public IMessage onMessage(final PacketUpdateSizeToClient message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message,ctx));
            return null;
        }

        private void handle(PacketUpdateSizeToClient message, MessageContext ctx){
            final WorldClient worldClient = Minecraft.getMinecraft().world;

            if(worldClient.getTileEntity(message.blockPos) instanceof TileVending)((TileVending) worldClient.getTileEntity(message.blockPos)).setItemSize(message.data, message.slot);
        }
    }

    public static class DummyServerHandler implements IMessageHandler<PacketUpdateSizeToClient, IMessage> {
        @Override
        public IMessage onMessage(PacketUpdateSizeToClient message, MessageContext ctx) {
            return null;
        }
    }
}