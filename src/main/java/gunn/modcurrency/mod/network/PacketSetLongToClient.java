package gunn.modcurrency.mod.network;

import gunn.modcurrency.mod.tileentity.TileVending;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-10-13
 */
public class PacketSetLongToClient implements IMessage {
    private BlockPos blockPos;
    private long data;
    private byte field;

    public PacketSetLongToClient(){}

    public void setData(BlockPos pos, byte id, long data) {
        this.blockPos = pos;
        this.field = id;
        this.data = data;

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        blockPos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        field = buf.readByte();
        data = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(blockPos.getX());
        buf.writeInt(blockPos.getY());
        buf.writeInt(blockPos.getZ());
        buf.writeByte(field);
        buf.writeLong(data);
    }

    public static class Handler implements IMessageHandler<PacketSetLongToClient, IMessage> {

        @Override
        public IMessage onMessage(final PacketSetLongToClient message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message,ctx));
            return null;
        }

        private void handle(PacketSetLongToClient message, MessageContext ctx){
            final WorldClient worldClient = Minecraft.getMinecraft().world;

            if(worldClient.getTileEntity(message.blockPos) instanceof TileVending)((TileVending) worldClient.getTileEntity(message.blockPos)).setLong(message.field, message.data);
        }
    }

    public static class DummyServerHandler implements IMessageHandler<PacketSetLongToClient, IMessage> {
        @Override
        public IMessage onMessage(PacketSetLongToClient message, MessageContext ctx) {
            return null;
        }
    }
}