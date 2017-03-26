package gunn.modcurrency.mod.core.network;

import gunn.modcurrency.mod.core.data.BankAccount;
import gunn.modcurrency.mod.tile.TileATM;
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
public class PacketWithdrawToServer implements IMessage {
    //Player changes gear tab state by clicking it, sends this to the server
    private BlockPos blockPos;

    public PacketWithdrawToServer(){}

    public void setData(BlockPos pos) {
        this.blockPos = pos;
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

    public static class Handler implements IMessageHandler<PacketWithdrawToServer, IMessage> {

        @Override
        public IMessage onMessage(final PacketWithdrawToServer message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message,ctx));
            return null;
        }

        private void handle(PacketWithdrawToServer message, MessageContext ctx){
            EntityPlayerMP playerEntity = ctx.getServerHandler().playerEntity;
            World world = playerEntity.world;
            ((TileATM) world.getTileEntity(message.blockPos)).withdraw();
        }
    }
}