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
public class MessageVendingCashButton {
    private final BlockPos pos;
    private final int mode;

    public MessageVendingCashButton(BlockPos pos, int mode){
        this.pos = pos;
        this.mode = mode;
    }

    public static MessageVendingCashButton decode(PacketBuffer buf){
        BlockPos pos = buf.readBlockPos();
        int mode = buf.readInt();

        return new MessageVendingCashButton(pos, mode);
    }

    public static void encode(MessageVendingCashButton message, PacketBuffer buf){
        buf.writeBlockPos(message.pos);
        buf.writeInt(message.mode);
    }

    public static void handle(MessageVendingCashButton message, Supplier<NetworkEvent.Context> ctx) {
        PlayerEntity playerEntity = ctx.get().getSender();

        if (playerEntity != null) {
            ctx.get().enqueueWork(() -> {
                TileEntity tile = playerEntity.getEntityWorld().getTileEntity(message.pos);

                if (tile instanceof VendingTile) {
                    ((VendingTile) tile).cashButton((message.mode));
                }
            });
        }
    }
}
