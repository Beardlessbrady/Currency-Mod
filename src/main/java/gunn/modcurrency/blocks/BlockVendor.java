package gunn.modcurrency.blocks;

import gunn.modcurrency.ModCurrency;
import gunn.modcurrency.tiles.TileVendor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;

/**
 * This class was created by <Brady Gunn>.
 * Distributed with the Currency-Mod for Minecraft.
 *
 * The Currency-Mod is open source and distributed
 * under the General Public License
 *
 * File Created on 2016-10-30.
 */
public class BlockVendor extends BaseBlock implements ITileEntityProvider {

    public BlockVendor() {
        super(Material.ROCK, "blockvendor");
        GameRegistry.register(new ItemBlock(this), getRegistryName());
        GameRegistry.registerTileEntity(TileVendor.class, ModCurrency.MODID + "_tevendor");
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileVendor();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;
        if (player.isSneaking()) {
            if (getTile(world, pos).getField(2) == 1) {   //If True
                getTile(world, pos).setField(2, 0);
            } else {
                getTile(world, pos).setField(2, 1);
            }
            getTile(world, pos).getWorld().notifyBlockUpdate(getTile(world, pos).getPos(), getTile(world, pos).getBlockType().getDefaultState(), getTile(world, pos).getBlockType().getDefaultState(), 3);
            return true;
        }
        player.openGui(ModCurrency.instance, 30, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }


    public TileVendor getTile(World world, BlockPos pos) {
        return (TileVendor) world.getTileEntity(pos);
    }


    /*TODO 
    Fix Collision Box
    Add Lock Mode
    Detect Chests anywhere near block
    Detect redstone signal (To turn on)
    Animate Door Opening (Green light on if closed, Red Light on if Open)
    Render items in model
    When vending machine ON(redstone powered) inside lite up
    
    FACING, it can only place one way :C
    Particle Textures
    how long it takes to break and with what
    
    
    TOOLTIPS
    
    SELL MODE 
    
    BUG:changing a cost, exiting gui and then going back in doesnt update the cost changed until middle clicking
    
     */
}
