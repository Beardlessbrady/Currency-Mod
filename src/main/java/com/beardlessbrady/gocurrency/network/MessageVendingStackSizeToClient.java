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
public class MessageVendingStackSizeToClient {
    private final BlockPos pos;
    private final int[] stackSize;

    public MessageVendingStackSizeToClient(BlockPos pos, int[] array){
        this.pos = pos;
        stackSize = array;
    }

    public static MessageVendingStackSizeToClient decode(PacketBuffer buf){
        BlockPos pos = buf.readBlockPos();
        int id = buf.readInt();
        int[] sizeArray = buf.readVarIntArray();

        return new MessageVendingStackSizeToClient(pos, sizeArray);
    }

    public static void encode(MessageVendingStackSizeToClient message, PacketBuffer buf){
        buf.writeBlockPos(message.pos);
        buf.writeVarIntArray(message.stackSize);
    }

    public static void handle(MessageVendingStackSizeToClient message, Supplier<NetworkEvent.Context> ctx){
        PlayerEntity playerEntity = ctx.get().getSender();

        if(playerEntity != null){
            ctx.get().enqueueWork(() -> {
                TileEntity tile = playerEntity.getEntityWorld().getTileEntity(message.pos);

                if(tile instanceof VendingTile){
                    System.out.println(message.stackSize[0]);
                    ((VendingTile) tile).setStackSizes(message.stackSize);
                }
            });
        }
    }
}
