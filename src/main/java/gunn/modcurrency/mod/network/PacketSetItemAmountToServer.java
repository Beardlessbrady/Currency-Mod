package gunn.modcurrency.mod.network;

import gunn.modcurrency.mod.tileentity.TileExchanger;
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
public class PacketSetItemAmountToServer implements IMessage {
    //Seller: Uses player input and sets amount of item, sends to server
    private BlockPos blockPos;
    private int data, index;

    public PacketSetItemAmountToServer(){}

    public void setData(int data, BlockPos pos, int index) {
        this.blockPos = pos;
        this.data = data;
        this.index = index;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        blockPos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        data = buf.readInt();
        index = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(blockPos.getX());
        buf.writeInt(blockPos.getY());
        buf.writeInt(blockPos.getZ());
        buf.writeInt(data);
        buf.writeInt(index);
    }

    public static class Handler implements IMessageHandler<PacketSetItemAmountToServer, IMessage> {

        @Override
        public IMessage onMessage(final PacketSetItemAmountToServer message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message,ctx));
            return null;
        }

        private void handle(PacketSetItemAmountToServer message, MessageContext ctx){
            EntityPlayerMP playerEntity = ctx.getServerHandler().player;
            World world = playerEntity.world;
            TileExchanger tile = ((TileExchanger) world.getTileEntity(message.blockPos));
            tile.setItemAmount(message.data, message.index);
        }
    }
}