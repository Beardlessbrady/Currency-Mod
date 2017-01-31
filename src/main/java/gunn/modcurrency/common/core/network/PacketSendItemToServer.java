package gunn.modcurrency.common.core.network;

import gunn.modcurrency.api.ModTile;
import gunn.modcurrency.common.blocks.BlockVendor;
import gunn.modcurrency.common.blocks.ModBlocks;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Distributed with the Currency-Mod for Minecraft.
 * Copyright (C) 2016  Brady Gunn
 *
 * File Created on 2016-11-06.
 */
public class PacketSendItemToServer implements IMessage{
    private BlockPos blockPos;
    
    public PacketSendItemToServer(){}
    
    public void setBlockPos(BlockPos blockpos) {
        this.blockPos = blockpos;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        blockPos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(blockPos.getX());
        buf.writeInt(blockPos.getY());
        buf.writeInt(blockPos.getZ());
    }
    
    public static class Handler implements IMessageHandler<PacketSendItemToServer, IMessage>{

        @Override
        public IMessage onMessage(final PacketSendItemToServer message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message,ctx));
            return null;
        }
        
        private void handle(PacketSendItemToServer message, MessageContext ctx){
            EntityPlayerMP playerEntity = ctx.getServerHandler().playerEntity;
            World world = playerEntity.world;
            ModTile tile = (ModTile) world.getTileEntity(message.blockPos);
            tile.outChange();
        }
    }
}
