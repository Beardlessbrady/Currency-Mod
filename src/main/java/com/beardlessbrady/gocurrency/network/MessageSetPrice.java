package com.beardlessbrady.gocurrency.network;

import com.beardlessbrady.gocurrency.blocks.vending.VendingTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Created by BeardlessBrady on 2021-03-06 for Currency-Mod
 * All Rights Reserved
 * https://github.com/Beardlessbrady/Currency-Mod
 */
public class MessageSetPrice {
    private final BlockPos pos;
    private final int index;
    private final int priceD;
    private final int priceC;

    public MessageSetPrice(BlockPos pos, int index, int priceD, int priceC){
        this.pos = pos;
        this.index = index;
        this.priceD = priceD;
        this.priceC = priceC;
    }

    public MessageSetPrice(BlockPos pos, int index, String price){
        this.pos = pos;
        this.index = index;

        String[] prices = price.split("[.]");

        this.priceD = Integer.parseInt(prices[0]);
        this.priceC = Integer.parseInt(prices[1]);
    }

    public static MessageSetPrice decode(PacketBuffer buf){
        BlockPos pos = buf.readBlockPos();
        int index = buf.readInt();
        int priceD = buf.readInt();
        int priceC = buf.readInt();

        return new MessageSetPrice(pos, index, priceD, priceC);
    }

    public static void encode(MessageSetPrice message, PacketBuffer buf){
        buf.writeBlockPos(message.pos);
        buf.writeInt(message.index);
        buf.writeInt(message.priceD);
        buf.writeInt(message.priceC);
    }

    public static void handle(MessageSetPrice message, Supplier<NetworkEvent.Context> ctx) {
        PlayerEntity playerEntity = ctx.get().getSender();
        if (playerEntity != null) {
            ctx.get().enqueueWork(() -> {
                TileEntity tile = playerEntity.getEntityWorld().getTileEntity(message.pos);

                if (tile instanceof VendingTile) {
                    ((VendingTile) tile).setPrice(message.index, new int[] {message.priceD, message.priceC});
                }
            });
        }
    }
}