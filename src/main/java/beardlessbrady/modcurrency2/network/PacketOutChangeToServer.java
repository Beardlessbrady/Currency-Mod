package beardlessbrady.modcurrency2.network;

import beardlessbrady.modcurrency2.block.economyblocks.tradein.TileTradein;
import beardlessbrady.modcurrency2.block.economyblocks.vending.TileVending;
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
 * File Created 2019-06-03
 */

public class PacketOutChangeToServer implements IMessage {
    private BlockPos blockPos;
    private Boolean breakBlock;

    public PacketOutChangeToServer(){}

    public void setData(BlockPos pos, Boolean breakBlock) {
        this.blockPos = pos;
        this.breakBlock = breakBlock;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        blockPos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        breakBlock = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(blockPos.getX());
        buf.writeInt(blockPos.getY());
        buf.writeInt(blockPos.getZ());
        buf.writeBoolean(breakBlock);
    }

    public static class Handler implements IMessageHandler<PacketOutChangeToServer, IMessage> {

        @Override
        public IMessage onMessage(final PacketOutChangeToServer message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message,ctx));
            return null;
        }

        private void handle(PacketOutChangeToServer message, MessageContext ctx){
            EntityPlayerMP playerEntity = ctx.getServerHandler().player;
            World world = playerEntity.world;
            TileEntity te = world.getTileEntity(message.blockPos);
            if(te instanceof TileVending) ((TileVending) te).outChange(message.breakBlock);
            if(te instanceof TileTradein) ((TileTradein) te).outChange(message.breakBlock);
        }
    }
}