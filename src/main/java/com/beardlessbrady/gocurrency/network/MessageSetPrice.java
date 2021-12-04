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
    private final int priceM;
    private final int priceT;
    private final int priceC;

    public MessageSetPrice(BlockPos pos, int index, int priceM, int priceT, int priceC){
        this.pos = pos;
        this.index = index;
        this.priceM = priceM;
        this.priceT = priceT;
        this.priceC = priceC;
    }

    public MessageSetPrice(BlockPos pos, int index, String price){
        this.pos = pos;
        this.index = index;

        String[] prices = price.split("[.]");

        if(prices[0].length() > 4) {
            this.priceM = Integer.parseInt(prices[0].substring(0, prices[0].length() - 4));
            this.priceT = Integer.parseInt(prices[0].substring(prices[0].length() - 4));
        } else {
            this.priceM = 0;
            this.priceT = Integer.parseInt(prices[0]);
        }
        this.priceC = Integer.parseInt(prices[1]);
    }

    public static MessageSetPrice decode(PacketBuffer buf){
        BlockPos pos = buf.readBlockPos();
        int index = buf.readInt();
        int priceM = buf.readInt();
        int priceT = buf.readInt();
        int priceC = buf.readInt();

        return new MessageSetPrice(pos, index, priceM, priceT, priceC);
    }

    public static void encode(MessageSetPrice message, PacketBuffer buf){
        buf.writeBlockPos(message.pos);
        buf.writeInt(message.index);

        buf.writeInt(message.priceM);
        buf.writeInt(message.priceT);
        buf.writeInt(message.priceC);
    }

    public static void handle(MessageSetPrice message, Supplier<NetworkEvent.Context> ctx) {
        PlayerEntity playerEntity = ctx.get().getSender();
        if (playerEntity != null) {
            ctx.get().enqueueWork(() -> {
                TileEntity tile = playerEntity.getEntityWorld().getTileEntity(message.pos);

                if (tile instanceof VendingTile) {
                    ((VendingTile) tile).setStockPrice(message.index, new int[] {message.priceM, message.priceT, message.priceC});
                }
            });
        }
    }
}