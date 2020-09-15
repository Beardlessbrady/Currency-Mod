package beardlessbrady.modcurrency2.handler;

import beardlessbrady.modcurrency2.block.economyblocks.tradein.ContainerTradein;
import beardlessbrady.modcurrency2.block.economyblocks.tradein.GuiTradein;
import beardlessbrady.modcurrency2.block.economyblocks.tradein.TileTradein;
import beardlessbrady.modcurrency2.block.economyblocks.vending.ContainerVending;
import beardlessbrady.modcurrency2.block.economyblocks.vending.GuiVending;
import beardlessbrady.modcurrency2.block.economyblocks.vending.TileVending;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-02-10
 */

public class GuiHandler implements IGuiHandler {
    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos xyz = new BlockPos(x,y,z);
        TileEntity tileEntity = world.getTileEntity(xyz);

        switch(ID){
            case 30: // Vending Machine
                TileVending tileVendor = (TileVending) tileEntity;
                return new ContainerVending(player, tileVendor);
            case 31: // Trade-in Machine
                TileTradein tileTradeIn = (TileTradein) tileEntity;
                return new ContainerTradein(player, tileTradeIn);
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos xyz = new BlockPos(x,y,z);
        TileEntity tileEntity = world.getTileEntity(xyz);

        switch(ID){
            case 30: // Vending Machine
                TileVending tileVendor = (TileVending) tileEntity;
                return new GuiVending(player, tileVendor);
            case 31: // Trade-in Machine
                TileTradein tileTradein = (TileTradein) tileEntity;
                return new GuiTradein(player, tileTradein);
            default:
                return null;
        }
    }
}
