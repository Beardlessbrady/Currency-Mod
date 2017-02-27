package gunn.modcurrency.common.core.network;

import gunn.modcurrency.api.TileBuy;
import gunn.modcurrency.common.tiles.TileSeller;
import gunn.modcurrency.common.tiles.TileVendor;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Distributed with the Currency-Mod for Minecraft.
 * Copyright (C) 2016  Brady Gunn
 *
 * File Created on 2016-11-06.
 */
public class PacketSendIntDataToClient implements IMessage {
    private BlockPos blockPos;
    private int data, data2, mode;

    public PacketSendIntDataToClient() {
    }

    public void setData(int data, BlockPos pos, int mode) {
        this.blockPos = pos;
        this.data = data;
        this.mode = mode;
    }

    public void setData(int data, int data2, BlockPos pos, int mode) {
        this.blockPos = pos;
        this.data = data;
        this.data2 = data2;
        this.mode = mode;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        blockPos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        data = buf.readInt();
        data2 = buf.readInt();
        mode = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(blockPos.getX());
        buf.writeInt(blockPos.getY());
        buf.writeInt(blockPos.getZ());
        buf.writeInt(data);
        buf.writeInt(data2);
        buf.writeInt(mode);
    }

    public static class Handler implements IMessageHandler<PacketSendIntDataToClient, IMessage> {

        @Override
        public IMessage onMessage(final PacketSendIntDataToClient message, MessageContext ctx) {
            FMLClientHandler.instance().getClient().addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketSendIntDataToClient message, MessageContext ctx) {
            WorldClient worldClient = Minecraft.getMinecraft().world;
            switch (message.mode) {
                case 0: //Vendor send GhostItem
                    TileVendor te0= (TileVendor) worldClient.getTileEntity(message.blockPos);
                    te0.setGhost(message.data, true);
                    System.out.println("YES");
                    break;
            }
        }
    }
}
