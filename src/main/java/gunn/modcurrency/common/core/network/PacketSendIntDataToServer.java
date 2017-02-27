package gunn.modcurrency.common.core.network;

import gunn.modcurrency.api.TileBuy;
import gunn.modcurrency.common.tiles.TileSeller;
import gunn.modcurrency.common.tiles.TileVendor;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Distributed with the Currency-Mod for Minecraft.
 * Copyright (C) 2016  Brady Gunn
 *
 * File Created on 2016-11-06.
 */
public class PacketSendIntDataToServer implements IMessage {
    private BlockPos blockPos;
    private int data, data2, mode;

    public PacketSendIntDataToServer() {
    }

    public void setData(int data, BlockPos pos, int mode) {
        this.blockPos = pos;
        this.data = data;
        this.mode = mode;
    }

    public void setData(int data, int data2, BlockPos pos, int mode) {
        this.blockPos = pos;
        this.data = data;
        this.data2 = data2;
        this.mode = mode;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        blockPos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        data = buf.readInt();
        data2 = buf.readInt();
        mode = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(blockPos.getX());
        buf.writeInt(blockPos.getY());
        buf.writeInt(blockPos.getZ());
        buf.writeInt(data);
        buf.writeInt(data2);
        buf.writeInt(mode);
    }

    public static class Handler implements IMessageHandler<PacketSendIntDataToServer, IMessage> {

        @Override
        public IMessage onMessage(final PacketSendIntDataToServer message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketSendIntDataToServer message, MessageContext ctx) {
            EntityPlayerMP playerEntity = ctx.getServerHandler().playerEntity;
            World world = playerEntity.world;
            switch (message.mode) {
                case 0:     //BlockBuy set Lock
                    TileBuy te0 = (TileBuy) world.getTileEntity(message.blockPos);
                    te0.setField(1, message.data);
                    break;
                case 1:     //Block Vendor set Cost
                    TileBuy te1 = (TileBuy) world.getTileEntity(message.blockPos);
                    te1.setItemCost(message.data);
                    break;
                case 2:     //Block Vendor, updated cost
                    TileBuy te2 = (TileBuy) world.getTileEntity(message.blockPos);
                    te2.setItemCost(message.data);
                    break;
                case 3:     //Enable/Disable Creative Button
                    TileBuy te3 = (TileBuy) world.getTileEntity(message.blockPos);
                    te3.setField(6, message.data);
                    break;
                case 4:     //Send Gear Tab State
                    TileBuy te4 = (TileBuy) world.getTileEntity(message.blockPos);
                    te4.setField(8, message.data);
                    break;
                case 5:     //Update Client
                    TileBuy te5 = (TileBuy) world.getTileEntity(message.blockPos);
                    te5.update(world, message.blockPos);
                    break;
                case 6:     //Block Seller set Amount
                    TileSeller te6 = (TileSeller) world.getTileEntity(message.blockPos);
                    te6.setItemAmount(message.data);
                    break;
                case 7:     //BlockBuy set Fuzzy
                    TileBuy te7 = (TileBuy) world.getTileEntity(message.blockPos);
                    te7.setField(11, message.data);
                    break;
            }
        }
    }
}
