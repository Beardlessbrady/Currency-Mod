package gunn.modcurrency.blocks;

import gunn.modcurrency.ModCurrency;
import gunn.modcurrency.tiles.TileVendor;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
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
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyEnum<EnumDyeColor> COLOR = PropertyEnum.create("color", EnumDyeColor.class);

    public BlockVendor() {
        super(Material.ROCK, "blockvendor");
        this.setDefaultState(this.blockState.getBaseState().withProperty(COLOR, EnumDyeColor.GRAY));
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

    //<editor-fold desc="Block States--------------------------------------------------------------------------------------------------------">
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos,state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
    }

    public TileVendor getTile(World world, BlockPos pos) {
        return (TileVendor) world.getTileEntity(pos);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this,FACING,COLOR);
    }

    @SuppressWarnings("depreciation")
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING,EnumFacing.getFront((meta & 3) + 2));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex()-2;
    }
    
    @SuppressWarnings("depreciation")
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        EnumDyeColor dyeColor = EnumDyeColor.GRAY;
        TileVendor tile = (TileVendor) worldIn.getTileEntity(pos);
        int currentColor = tile.getColor();
        
        switch(EnumDyeColor.byMetadata(currentColor)){
            case WHITE: dyeColor = EnumDyeColor.WHITE;
                break;
            case ORANGE: dyeColor = EnumDyeColor.ORANGE;
                break;
            case MAGENTA: dyeColor = EnumDyeColor.MAGENTA;
                break;
            case LIGHT_BLUE: dyeColor = EnumDyeColor.LIGHT_BLUE;
                break;
            case YELLOW: dyeColor = EnumDyeColor.YELLOW;
                break;
            case LIME: dyeColor = EnumDyeColor.LIME;
                break;
            case PINK: dyeColor = EnumDyeColor.PINK;
                break;
            case GRAY: dyeColor = EnumDyeColor.GRAY;
                break;
            case SILVER: dyeColor = EnumDyeColor.SILVER;
                break;
            case CYAN: dyeColor = EnumDyeColor.CYAN;
                break;
            case PURPLE: dyeColor = EnumDyeColor.PURPLE;
                break;
            case BLUE: dyeColor = EnumDyeColor.BLUE;
                break;
            case BROWN: dyeColor = EnumDyeColor.BROWN;
                break;
            case GREEN: dyeColor = EnumDyeColor.GREEN;
                break;
            case RED: dyeColor = EnumDyeColor.RED;
                break;
        }
        return state.withProperty(COLOR, dyeColor);
    }

    //@Override
   // public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        
    //}
    //</editor-fold>

    /*TODO 
    Fix Collision Box
    Add Lock Mode
    Detect Chests anywhere near block
    Detect redstone signal (To turn on)
    Animate Door Opening (Green light on if closed, Red Light on if Open)
    Render items in model
    When vending machine ON(redstone powered) inside lite up
    
    Particle Textures
    how long it takes to break and with what
    
    SELL MODE 
    
    BUG:changing a cost, exiting gui and then going back in doesnt update the cost changed until middle clicking
    
     */
}
