package gunn.modcurrency.handler;

import gunn.modcurrency.blocks.BlockVendor;
import gunn.modcurrency.client.containers.ContainerVendor;
import gunn.modcurrency.client.guis.GuiVendor;
import gunn.modcurrency.tiles.TileVendor;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

/**
 * This class was created by <Brady Gunn>.
 * Distributed with the Currency-Mod for Minecraft.
 *
 * The Currency-Mod is open source and distributed
 * under the General Public License
 *
 * File Created on 2016-11-02.
 */
public class GuiHandler implements IGuiHandler{
    private static final int GUIID_VENDOR = 30;
    public static int getGuiID(Block block){
        if(block instanceof BlockVendor){
            return GUIID_VENDOR;
        }else{
            return -1;
        }
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos xyz = new BlockPos(x,y,z);
        TileEntity tileEntity = world.getTileEntity(xyz);
        if(tileEntity instanceof TileVendor){
            TileVendor tilevendor = (TileVendor) tileEntity;
            return new ContainerVendor(player.inventory, tilevendor);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos xyz = new BlockPos(x,y,z);
        TileEntity tileEntity = world.getTileEntity(xyz);
        if(tileEntity instanceof TileVendor){
            TileVendor tilevendor = (TileVendor) tileEntity;
            return new GuiVendor(player.inventory, tilevendor);
        }
        return null;
    }
}
