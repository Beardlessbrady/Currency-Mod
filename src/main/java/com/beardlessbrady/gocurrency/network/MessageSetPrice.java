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
    private final String price;

    public MessageSetPrice(BlockPos pos, int index, String price){
        this.pos = pos;
        this.index = index;
        this.price = price;
    }

    public static MessageSetPrice decode(PacketBuffer buf){
        BlockPos pos = buf.readBlockPos();
        int index = buf.readInt();
        String price = buf.readString(20);

        return new MessageSetPrice(pos, index, price);
    }

    public static void encode(MessageSetPrice message, PacketBuffer buf){
        buf.writeBlockPos(message.pos);
        buf.writeInt(message.index);
        buf.writeString(message.price, 20);
    }

    public static void handle(MessageSetPrice message, Supplier<NetworkEvent.Context> ctx) {
        PlayerEntity playerEntity = ctx.get().getSender();
        if (playerEntity != null) {
            ctx.get().enqueueWork(() -> {
                TileEntity tile = playerEntity.getEntityWorld().getTileEntity(message.pos);

                if (tile instanceof VendingTile) {
                    ((VendingTile) tile).setPrice(message.index, message.price);
                }
            });
        }
    }
}
