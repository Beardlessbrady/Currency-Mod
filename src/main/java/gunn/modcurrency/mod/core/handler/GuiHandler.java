package gunn.modcurrency.mod.core.handler;

import gunn.modcurrency.mod.client.container.ContainerATM;
import gunn.modcurrency.mod.client.gui.GuiATM;
import gunn.modcurrency.mod.client.gui.GuiWallet;
import gunn.modcurrency.mod.client.container.ContainerBuySell;
import gunn.modcurrency.mod.client.gui.GuiBuySell;
import gunn.modcurrency.mod.client.container.ContainerWallet;
import gunn.modcurrency.mod.item.ModItems;
import gunn.modcurrency.mod.tile.TileATM;
import gunn.modcurrency.mod.tile.TileSeller;
import gunn.modcurrency.mod.tile.TileVendor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

/**
 * Distributed with the Currency-Mod for Minecraft.
 * Copyright (C) 2016  Brady Gunn
 *
 * File Created on 2016-11-02.
 */
public class GuiHandler implements IGuiHandler{
    //Id 30 = BlockVendor
    //Id 31 = BlockSeller
    //Id 32 = Wallet
    //Id 33 = ATM


    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos xyz = new BlockPos(x,y,z);
        TileEntity tileEntity = world.getTileEntity(xyz);

        if(tileEntity instanceof TileVendor && ID == 30){
            TileVendor tilevendor = (TileVendor) tileEntity;
            return new ContainerBuySell(player.inventory, tilevendor);
        }

        if(tileEntity instanceof TileSeller && ID == 31){
            TileSeller tileSeller = (TileSeller) tileEntity;
            return new ContainerBuySell(player.inventory, tileSeller);
        }

        if(ID == 32 && player.getHeldItemMainhand().getItem().equals(ModItems.itemWallet)){
            return new ContainerWallet(player, player.getHeldItemMainhand());
        }

        if(tileEntity instanceof TileATM && ID == 33){
            TileATM tileATM = (TileATM) tileEntity;
            return new ContainerATM(player.inventory, tileATM);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos xyz = new BlockPos(x,y,z);
        TileEntity tileEntity = world.getTileEntity(xyz);

        if(tileEntity instanceof TileVendor && ID == 30){
            TileVendor tilevendor = (TileVendor) tileEntity;
            return new GuiBuySell(player.inventory, tilevendor);
        }

        if(tileEntity instanceof TileSeller && ID == 31){
            TileSeller tileSeller = (TileSeller) tileEntity;
            return new GuiBuySell(player.inventory, tileSeller);
        }

        if(ID == 32 && player.getHeldItemMainhand().getItem().equals(ModItems.itemWallet)){
            return new GuiWallet(player, player.getHeldItemMainhand());
        }

        if(tileEntity instanceof TileATM && ID == 33){
            TileATM tileATM = (TileATM) tileEntity;
            return new GuiATM(player.inventory, tileATM);
        }
        return null;
    }
}
