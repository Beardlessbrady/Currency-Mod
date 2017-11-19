package gunn.modcurrency.mod.network;

import gunn.modcurrency.mod.tileentity.TileExchanger;
import gunn.modcurrency.mod.tileentity.TileVending;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-03-12
 */
public class PacketSetItemMultiPricesToServer implements IMessage {
    //Vendor: Uses player input anf sets cost of item, sends to server
    private BlockPos blockPos;
    private int[] data;
    private int index;

    public PacketSetItemMultiPricesToServer(){}

    public void setData(int[] data, int index, BlockPos pos) {
        this.blockPos = pos;
        this.data = data.clone();
        this.index = index;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        blockPos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        data[0] = buf.readInt();
        data[1] = buf.readInt();
        data[2] = buf.readInt();
        index = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(blockPos.getX());
        buf.writeInt(blockPos.getY());
        buf.writeInt(blockPos.getZ());
        buf.writeInt(data[0]);
        buf.writeInt(data[1]);
        buf.writeInt(data[2]);
        buf.writeInt(index);
    }

    public static class Handler implements IMessageHandler<PacketSetItemMultiPricesToServer, IMessage> {

        @Override
        public IMessage onMessage(final PacketSetItemMultiPricesToServer message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message,ctx));
            return null;
        }

        private void handle(PacketSetItemMultiPricesToServer message, MessageContext ctx){
            EntityPlayerMP playerEntity = ctx.getServerHandler().player;
            World world = playerEntity.world;
            TileEntity tile = world.getTileEntity(message.blockPos);

            if(tile instanceof TileVending) ((TileVending) tile).setMultiPrices(message.index, message.data);
        }
    }
}