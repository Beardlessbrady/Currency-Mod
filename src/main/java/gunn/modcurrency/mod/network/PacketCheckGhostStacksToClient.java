package gunn.modcurrency.mod.network;

import gunn.modcurrency.mod.tileentity.TileATM;
import gunn.modcurrency.mod.tileentity.TileExchanger;
import gunn.modcurrency.mod.tileentity.TileVending;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-03-12
 */
public class PacketCheckGhostStacksToClient implements IMessage {
    private BlockPos blockPos;
    private int data, field;

    public PacketCheckGhostStacksToClient(){}

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

    public static class Handler implements IMessageHandler<PacketCheckGhostStacksToClient, IMessage> {

        @Override
        public IMessage onMessage(final PacketCheckGhostStacksToClient message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message,ctx));
            return null;
        }

        private void handle(PacketCheckGhostStacksToClient message, MessageContext ctx){
            final WorldClient worldClient = Minecraft.getMinecraft().world;

            if(worldClient.getTileEntity(message.blockPos) instanceof TileVending) {
                TileVending tile = (TileVending) worldClient.getTileEntity(message.blockPos);

                IItemHandler itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                for (int i = 0; i < tile.VEND_SLOT_COUNT; i++) {
                    if (tile.isGhostSlot(i) && itemHandler.getStackInSlot(i + 1).getCount() > 1) {
                        itemHandler.getStackInSlot(i + 1).shrink(1);
                        tile.setGhostSlot(i, false);
                    }
                }
            }
        }
    }

    public static class DummyServerHandler implements IMessageHandler<PacketCheckGhostStacksToClient, IMessage> {
        @Override
        public IMessage onMessage(PacketCheckGhostStacksToClient message, MessageContext ctx) {
            return null;
        }
    }
}