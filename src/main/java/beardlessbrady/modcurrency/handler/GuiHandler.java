package beardlessbrady.modcurrency.handler;

import beardlessbrady.modcurrency.block.vending.ContainerVending;
import beardlessbrady.modcurrency.block.vending.GuiVending;
import beardlessbrady.modcurrency.block.vending.TileVending;
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

        if(tileEntity instanceof TileVending && ID == 30){
            TileVending tilevendor = (TileVending) tileEntity;
            return new ContainerVending(player, tilevendor);
        }

        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos xyz = new BlockPos(x,y,z);
        TileEntity tileEntity = world.getTileEntity(xyz);

        if(tileEntity instanceof TileVending && ID == 30){
            TileVending tilevendor = (TileVending) tileEntity;
            return new GuiVending(player, tilevendor);
        }

        return null;
    }
}
