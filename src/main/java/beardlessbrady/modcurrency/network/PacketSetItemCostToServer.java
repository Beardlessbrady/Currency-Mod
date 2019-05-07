package beardlessbrady.modcurrency.network;

import beardlessbrady.modcurrency.block.vending.TileVending;
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
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-05-07
 */

public class PacketSetItemCostToServer implements IMessage {
    //Vendor: Uses player input anf sets cost of item, sends to server
    private BlockPos blockPos;
    private int data;

    public PacketSetItemCostToServer(){}

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

    public static class Handler implements IMessageHandler<PacketSetItemCostToServer, IMessage> {

        @Override
        public IMessage onMessage(final PacketSetItemCostToServer message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message,ctx));
            return null;
        }

        private void handle(PacketSetItemCostToServer message, MessageContext ctx){
            EntityPlayerMP playerEntity = ctx.getServerHandler().player;
            World world = playerEntity.world;
            TileEntity tile = world.getTileEntity(message.blockPos);

            if(tile instanceof TileVending) ((TileVending) tile).setItemCost(message.data);
        }
    }
}