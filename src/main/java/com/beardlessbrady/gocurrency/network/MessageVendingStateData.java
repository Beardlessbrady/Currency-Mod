package com.beardlessbrady.gocurrency.network;

import com.beardlessbrady.gocurrency.blocks.vending.VendingStateData;
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
public class MessageVendingStateData{
    private final BlockPos pos;
    private final int id;
    private final int value;

    public MessageVendingStateData(BlockPos pos, int id, int value){
        this.pos = pos;
        this.id = id;
        this.value = value;
    }

    public static MessageVendingStateData decode(PacketBuffer buf){
        BlockPos pos = buf.readBlockPos();
        int id = buf.readInt();
        int value = buf.readInt();

        return new MessageVendingStateData(pos, id, value);
    }

    public static void encode(MessageVendingStateData message, PacketBuffer buf){
        buf.writeBlockPos(message.pos);
        buf.writeInt(message.id);
        buf.writeInt(message.value);
    }

    public static void handle(MessageVendingStateData message, Supplier<NetworkEvent.Context> ctx){
        PlayerEntity playerEntity = ctx.get().getSender();

        if(playerEntity != null){
            ctx.get().enqueueWork(() -> {
                TileEntity tile = playerEntity.getEntityWorld().getTileEntity(message.pos);

                if (tile instanceof VendingTile) {
                  ((VendingTile) tile).setVendingStateData(message.id, message.value);
                }
            });
        }
    }
}
