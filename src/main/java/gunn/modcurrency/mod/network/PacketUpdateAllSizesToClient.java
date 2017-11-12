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

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-10-13
 */
public class PacketUpdateAllSizesToClient implements IMessage {
    private BlockPos blockPos;
    private int[] allSizes;

    public PacketUpdateAllSizesToClient(){}

    public void setData(BlockPos pos, int[] sizes) {
        this.blockPos = pos;
        this.allSizes = sizes;

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        blockPos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());

        allSizes = new int[TileVending.VEND_SLOT_COUNT];
        for(int i = 0; i < TileVending.VEND_SLOT_COUNT; i++) allSizes[i] = buf.readInt();

    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(blockPos.getX());
        buf.writeInt(blockPos.getY());
        buf.writeInt(blockPos.getZ());
        for(int i = 0; i < TileVending.VEND_SLOT_COUNT; i++) buf.writeInt(allSizes[i]);
    }

    public static class Handler implements IMessageHandler<PacketUpdateAllSizesToClient, IMessage> {

        @Override
        public IMessage onMessage(final PacketUpdateAllSizesToClient message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message,ctx));
            return null;
        }

        private void handle(PacketUpdateAllSizesToClient message, MessageContext ctx){
            final WorldClient worldClient = Minecraft.getMinecraft().world;

            if(worldClient.getTileEntity(message.blockPos) instanceof TileVending){
                TileVending tile = (TileVending)worldClient.getTileEntity(message.blockPos);
                for(int i = 0; i < TileVending.VEND_SLOT_COUNT; i++){
                    tile.setItemSize(message.allSizes[i], i);
                }
            }
        }
    }

    public static class DummyServerHandler implements IMessageHandler<PacketUpdateAllSizesToClient, IMessage> {
        @Override
        public IMessage onMessage(PacketUpdateAllSizesToClient message, MessageContext ctx) {
            return null;
        }
    }
}