package gunn.modcurrency.common.core.handler;

import gunn.modcurrency.common.containers.ContainerBuySell;
import gunn.modcurrency.client.guis.GuiBuySell;
import gunn.modcurrency.common.tiles.TileSeller;
import gunn.modcurrency.common.tiles.TileVendor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

/**
 * Distributed with the Currency-Mod for Minecraft.
 * Copyright (C) 2016  Brady Gunn
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * File Created on 2016-11-02.
 */
public class GuiHandler implements IGuiHandler{
    //Id 30 = BlockVendor [Closed]

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

        if(tileEntity instanceof  TileSeller && ID == 31){
            TileSeller tileSeller = (TileSeller) tileEntity;
            return new GuiBuySell(player.inventory, tileSeller);
        }
        return null;
    }
}
