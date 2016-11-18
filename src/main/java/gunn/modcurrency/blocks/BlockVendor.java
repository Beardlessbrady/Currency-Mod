package gunn.modcurrency.blocks;

import gunn.modcurrency.ModCurrency;
import gunn.modcurrency.blocks.items.ItemVendor;
import gunn.modcurrency.handler.StateHandler;
import gunn.modcurrency.tiles.TileVendor;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.List;

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
    public static final AxisAlignedBB TWO_HIGH_BLOCK_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 2.0D, 1.0D);
    
    public BlockVendor() {
        super(Material.ROCK, "blockvendor");
        
        setHardness(3.0F);
        setSoundType(SoundType.METAL);
        
        GameRegistry.register(new ItemVendor(this), getRegistryName());
        GameRegistry.registerTileEntity(TileVendor.class, ModCurrency.MODID + "_tevendor");
    }
    
    @Override
    public void initModel(){
        for(int i =0; i < 16; i++){
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), i, new ModelResourceLocation(getRegistryName(), "color=" + EnumDyeColor.byMetadata(i) + ",facing=north"));
        }
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
            list.add(new ItemStack(item, 1, 15));
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileVendor();
    }

    public TileVendor getTile(World world, BlockPos pos) {
        return (TileVendor) world.getTileEntity(pos);
    }
    
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;
        if(heldItem != null) {
            if (heldItem.getItem() == Items.DYE) {
                //Saving tile variables
                int face = getTile(world, pos).getFaceData();
                int bank = getTile(world, pos).getField(0);
                int[] itemCosts = getTile(world, pos).getAllItemCosts();
                ItemStackHandler stackHandler = getTile(world, pos).getStackHandler();


                world.setBlockState(pos, state.withProperty(StateHandler.COLOR, EnumDyeColor.byDyeDamage(heldItem.getItemDamage())), 3);

                //Setting tile variables
                getTile(world, pos).setFaceData(face);
                getTile(world, pos).setField(0, bank);
                getTile(world, pos).setAllItemCosts(itemCosts);
                getTile(world, pos).setStackHandler(stackHandler);

                if (!player.isCreative()) heldItem.stackSize--;
                return true;
            }
        }

        if(player.isSneaking()) {
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


    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        int face = 0;
        switch(placer.getHorizontalFacing().getOpposite()){
            case NORTH: face = 0;
                break;
            case EAST: face = 1;
                break;
            case SOUTH: face = 2;
                break;
            case WEST: face = 3;
                break;
        }

        getTile(worldIn, pos).setFaceData(face);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }
    
    //<editor-fold desc="Model Methods-------------------------------------------------------------------------------------------------------">
    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
        return TWO_HIGH_BLOCK_AABB;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }
    //</editor-fold>

    //<editor-fold desc="Block States--------------------------------------------------------------------------------------------------------">
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {StateHandler.COLOR, StateHandler.FACING});
    }
    
    @SuppressWarnings("depreciation")
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(StateHandler.COLOR, EnumDyeColor.byMetadata(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return ((EnumDyeColor)state.getValue(StateHandler.COLOR)).getMetadata();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        EnumFacing face = EnumFacing.NORTH;
        TileVendor tile = (TileVendor)worldIn.getTileEntity(pos);
        int i = tile.getFaceData();
        
        switch(i){
            case 0: face = EnumFacing.NORTH;
                break;
            case 1: face = EnumFacing.EAST;
                break;
            case 2: face = EnumFacing.SOUTH;
                break;
            case 3: face = EnumFacing.WEST;
                break;
        }
        
        return state.withProperty(StateHandler.FACING, face);
    }

    //</editor-fold>

    /*TODO 
    //Features
    -Lock Mode (allow pumping in items = Unlocked)
    -Visual(or sound) que that you cant afford something
    -Hopper/Pipe items in (Works with Lock Mode, obviously 
    -Render items in model
    -When vending machine ON(redstone powered) inside lite up
   
    //Polishing
    -Activate block on top part too
    -uses dye even if the vending machine is already that color (may just leave this)
    -Do Culling for certain Sides
    -changing a cost, exiting gui and then going back in doesnt update the cost changed until middle clicking
    
    BACK BURNER
    animate door opening when in edit mode
     */
}
