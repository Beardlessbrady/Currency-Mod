package gunn.modcurrency.mod.network;

import gunn.modcurrency.mod.tileEntity.TileATM;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
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
public class PacketSetATMFeeToServer implements IMessage {
    //Vendor: Uses player input anf sets cost of item, sends to server
    private BlockPos blockPos;
    private int data;

    public PacketSetATMFeeToServer(){}

    public void setData(int data, BlockPos pos) {
        this.blockPos = pos;
        this.data = data;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        blockPos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        data = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(blockPos.getX());
        buf.writeInt(blockPos.getY());
        buf.writeInt(blockPos.getZ());
        buf.writeInt(data);
    }

    public static class Handler implements IMessageHandler<PacketSetATMFeeToServer, IMessage> {

        @Override
        public IMessage onMessage(final PacketSetATMFeeToServer message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message,ctx));
            return null;
        }

        private void handle(PacketSetATMFeeToServer message, MessageContext ctx){
            EntityPlayerMP playerEntity = ctx.getServerHandler().playerEntity;
            World world = playerEntity.world;
            ((TileATM) world.getTileEntity(message.blockPos)).setField(2, message.data);
        }
    }
}