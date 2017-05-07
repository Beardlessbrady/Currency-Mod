package gunn.modcurrency.mod.network;

import gunn.modcurrency.mod.tileentity.TileATM;
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
public class PacketBankWithdrawToServer implements IMessage {
    private BlockPos pos;
    private int amount;

    public PacketBankWithdrawToServer(){}

    public void setData(BlockPos pos, int amnt) {
        this.pos = pos;
        this.amount = amnt;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        this.amount = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        buf.writeInt(amount);
    }

    public static class Handler implements IMessageHandler<PacketBankWithdrawToServer, IMessage> {

        @Override
        public IMessage onMessage(final PacketBankWithdrawToServer message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message,ctx));
            return null;
        }

        private void handle(PacketBankWithdrawToServer message, MessageContext ctx){
            EntityPlayerMP playerEntity = ctx.getServerHandler().playerEntity;
            World world = playerEntity.world;
            ((TileATM) world.getTileEntity(message.pos)).withdraw(message.amount);
        }
    }
}