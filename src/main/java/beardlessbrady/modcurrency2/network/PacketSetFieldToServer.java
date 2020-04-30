package beardlessbrady.modcurrency2.network;

import beardlessbrady.modcurrency2.block.economyblocks.TileEconomyBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
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
 * File Created 2019-02-22
 */

public class PacketSetFieldToServer implements IMessage {
    private BlockPos blockPos;
    private int data, field;

    public PacketSetFieldToServer(){}

    public void setData(int data, int field, BlockPos pos) {
        this.blockPos = pos;
        this.data = data;
        this.field = field;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        blockPos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        data = buf.readInt();
        field = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(blockPos.getX());
        buf.writeInt(blockPos.getY());
        buf.writeInt(blockPos.getZ());
        buf.writeInt(data);
        buf.writeInt(field);
    }

    public static class Handler implements IMessageHandler<PacketSetFieldToServer, IMessage> {

        @Override
        public IMessage onMessage(final PacketSetFieldToServer message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message,ctx));
            return null;
        }

        private void handle(PacketSetFieldToServer message, MessageContext ctx){
            EntityPlayerMP playerEntity = ctx.getServerHandler().player;
            World world = playerEntity.world;

            if(world.getTileEntity(message.blockPos) instanceof TileEconomyBase)((TileEconomyBase) world.getTileEntity(message.blockPos)).setField(message.field, message.data);
        }
    }
}