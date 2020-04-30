package beardlessbrady.modcurrency2.network;

import beardlessbrady.modcurrency2.block.economyblocks.vending.TileVending;
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
 * File Created 2019-06-03
 */

public class PacketSendKeyToServer implements IMessage {
    private BlockPos blockPos;
    private int key;
    private Boolean bool;

    public PacketSendKeyToServer(){}

    public void setData(BlockPos pos, int key, Boolean bool) {
        this.blockPos = pos;
        this.key = key;
        this.bool = bool;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        blockPos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        key = buf.readInt();
        bool = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(blockPos.getX());
        buf.writeInt(blockPos.getY());
        buf.writeInt(blockPos.getZ());
        buf.writeInt(key);
        buf.writeBoolean(bool);
    }

    public static class Handler implements IMessageHandler<PacketSendKeyToServer, IMessage> {

        @Override
        public IMessage onMessage(final PacketSendKeyToServer message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message,ctx));
            return null;
        }

        private void handle(PacketSendKeyToServer message, MessageContext ctx){
            EntityPlayerMP playerEntity = ctx.getServerHandler().player;
            World world = playerEntity.world;
            if(world.getTileEntity(message.blockPos) instanceof TileVending){
                TileVending te = (TileVending)world.getTileEntity(message.blockPos);
                te.setKey(message.key, message.bool);
            }
        }
    }
}