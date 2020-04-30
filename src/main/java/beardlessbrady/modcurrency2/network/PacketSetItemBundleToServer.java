package beardlessbrady.modcurrency2.network;

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
 * File Created 2019-05-07
 */

public class PacketSetItemBundleToServer implements IMessage {
    //Vendor: Uses player input anf sets cost of item, sends to server
    private BlockPos blockPos;
    private int data;
    private int[] bundle;

    public PacketSetItemBundleToServer(){}

    public void setData(int slot, int[] bundle, BlockPos pos) {
        this.blockPos = pos;
        this.data = slot;
        this.bundle = bundle;

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        blockPos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        data = buf.readInt();
        bundle = new int[buf.readInt()];

        for(int i = 0; i < bundle.length; i++){
            bundle[i] = buf.readInt();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(blockPos.getX());
        buf.writeInt(blockPos.getY());
        buf.writeInt(blockPos.getZ());
        buf.writeInt(data);
        buf.writeInt(bundle.length);

        for(int i = 0; i < bundle.length; i++) {
            buf.writeInt(bundle[i]);
        }
    }

    public static class Handler implements IMessageHandler<PacketSetItemBundleToServer, IMessage> {

        @Override
        public IMessage onMessage(final PacketSetItemBundleToServer message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message,ctx));
            return null;
        }

        private void handle(PacketSetItemBundleToServer message, MessageContext ctx){
            EntityPlayerMP playerEntity = ctx.getServerHandler().player;
            World world = playerEntity.world;
            TileEntity tile = world.getTileEntity(message.blockPos);


            if(tile instanceof TileVending){
                TileVending te = ((TileVending) tile);
                te.getItemVendor(message.data).setBundle(message.bundle);
            }
        }
    }
}