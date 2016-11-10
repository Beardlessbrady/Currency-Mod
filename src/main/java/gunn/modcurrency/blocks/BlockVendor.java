package gunn.modcurrency.blocks;

import gunn.modcurrency.ModCurrency;
import gunn.modcurrency.handler.GuiHandler;
import gunn.modcurrency.tiles.TileVendor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
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
public class BlockVendor extends BaseBlock implements ITileEntityProvider{

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
        if(!world.isRemote) System.out.println(getTile(world,pos).getField(1));
        if(world.isRemote) return true;
        player.openGui(ModCurrency.instance, GuiHandler.getGuiID(getDefaultState().getBlock()), world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    public TileVendor getTile(World world, BlockPos pos){
        return (TileVendor) world.getTileEntity(pos);
    }


}
