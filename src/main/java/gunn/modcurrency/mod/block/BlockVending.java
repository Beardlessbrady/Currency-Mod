package gunn.modcurrency.mod.block;

import gunn.modcurrency.mod.ModCurrency;
import gunn.modcurrency.mod.container.itemhandler.ItemHandlerVendor;
import gunn.modcurrency.mod.handler.StateHandler;
import gunn.modcurrency.mod.tileentity.TileVending;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
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
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

/**
 * Distributed with the Currency-Mod for Minecraft.
 * Copyright (C) 2016  Brady Gunn
 *
 * File Created on 2017-05-05
 */
public class BlockVending extends Block implements ITileEntityProvider {
    private static final AxisAlignedBB BOUND_BOX_N = new AxisAlignedBB(0.03125, 0, 0.28125, 0.96875, 1, 1);
    private static final AxisAlignedBB BOUND_BOX_E = new AxisAlignedBB(0.71875, 0, 0.03125, 0, 1, 0.96875);
    private static final AxisAlignedBB BOUND_BOX_S = new AxisAlignedBB(0.03125, 0, 0.71875, 0.96875, 1, 0);
    private static final AxisAlignedBB BOUND_BOX_W = new AxisAlignedBB(0.28125, 0, 0.03125, 1, 1, 0.96875);

    BlockVending() {
        super(Material.ROCK);
        setRegistryName("blockvending");
        setUnlocalizedName(this.getRegistryName().toString());

        setHardness(3.0F);
        setCreativeTab(ModCurrency.tabCurrency);
        setSoundType(SoundType.METAL);

        GameRegistry.registerTileEntity(TileVending.class, ModCurrency.MODID + "_tevending");
    }

    void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileVending();
    }

    private TileVending getTile(World world, BlockPos pos) {
        if (world.getBlockState(pos.down()).getBlock() == ModBlocks.blockVending && world.getBlockState(pos.down()).getValue(StateHandler.TWOTALL) == StateHandler.EnumTwoBlock.TWOBOTTOM)
            return (TileVending) world.getTileEntity(pos.down());

        return (TileVending) world.getTileEntity(pos);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileVending tile = getTile(worldIn,pos);
        if (tile.getPlayerUsing() == null) {
            tile.setField(tile.FIELD_CREATIVE, playerIn.isCreative() ? 1 : 0);
            if (!worldIn.isRemote) {
                tile.openGui(playerIn, worldIn, pos);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.withProperty(StateHandler.FACING, placer.getHorizontalFacing().getOpposite()));


        if (placer instanceof EntityPlayer) {
            getTile(worldIn, pos).setOwner((placer).getUniqueID().toString());

            if (worldIn.getBlockState(pos.down()).getBlock() == ModBlocks.blockVending && (getTile(worldIn, pos.down()).getOwner().equals(placer.getUniqueID().toString()))) { //If Owner and a vending is below
                if(worldIn.getBlockState(pos.down()).getValue(StateHandler.TWOTALL) == StateHandler.EnumTwoBlock.ONE) {
                    if(getTile(worldIn, pos.down()).getPlayerUsing() != null) getTile(worldIn, pos.down()).getPlayerUsing().closeScreen();
                    //Backing up important tile variables from below tile
                    TileVending tile = (TileVending) worldIn.getTileEntity(pos.down());
                    ItemStackHandler inputStack = tile.getInputStackHandler();
                    ItemHandlerVendor vendStack = tile.getVendStackHandler();
                    ItemStackHandler buffStack = tile.getBufferStackHandler();
                    int bank = tile.getField(tile.FIELD_BANK);
                    int profit = tile.getField(tile.FIELD_PROFIT);
                    String owner = tile.getOwner();
                    boolean locked = tile.getField(tile.FIELD_LOCKED) == 1;
                    boolean infinite = tile.getField(tile.FIELD_INFINITE) == 1;
                    int[] itemCosts = new int[tile.VEND_SLOT_COUNT];
                    for (int i = 0; i < itemCosts.length; i++) itemCosts[i] = tile.getItemCost(i);

                    worldIn.setBlockState(pos, state.withProperty(StateHandler.TWOTALL, StateHandler.EnumTwoBlock.TWOTOP)
                            .withProperty(StateHandler.FACING, placer.getHorizontalFacing().getOpposite()));

                    worldIn.setBlockState(pos.down(), state.withProperty(StateHandler.TWOTALL, StateHandler.EnumTwoBlock.TWOBOTTOM)
                            .withProperty(StateHandler.FACING, placer.getHorizontalFacing().getOpposite()));

                    //Re adding important variables to below tile
                    tile = (TileVending) worldIn.getTileEntity(pos.down());
                    tile.setField(tile.FIELD_TWOBLOCK, 1);
                    tile.setInputStackHandler(inputStack);
                    tile.setVendStackHandler(vendStack);
                    tile.setBufferStackHandler(buffStack);
                    tile.setField(tile.FIELD_BANK, bank);
                    tile.setField(tile.FIELD_PROFIT, profit);
                    tile.setOwner(owner);
                    tile.setField(tile.FIELD_LOCKED, locked ? 1 : 0);
                    tile.setField(tile.FIELD_INFINITE, infinite ? 1 : 0);
                    for (int i = 0; i < itemCosts.length; i++) tile.setItemCost(itemCosts[i], i);
                }
            }
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (state.getValue(StateHandler.TWOTALL) == StateHandler.EnumTwoBlock.TWOTOP) {     //TOP BLOCK
            if(worldIn.getBlockState(pos.down()).getBlock() == ModBlocks.blockVending){
                if(getTile(worldIn, pos.down()).getPlayerUsing() != null) getTile(worldIn, pos.down()).getPlayerUsing().closeScreen();
                //Backing up important tile variables from below tile
                TileVending tile = (TileVending) worldIn.getTileEntity(pos.down());
                ItemStackHandler inputStack = tile.getInputStackHandler();
                ItemHandlerVendor vendStack = tile.getVendStackHandler();
                ItemStackHandler buffStack = tile.getBufferStackHandler();
                int bank = tile.getField(tile.FIELD_BANK);
                int profit = tile.getField(tile.FIELD_PROFIT);
                String owner = tile.getOwner();
                boolean locked = tile.getField(tile.FIELD_LOCKED) == 1;
                boolean infinite = tile.getField(tile.FIELD_INFINITE) == 1;
                int[] itemCosts = new int[tile.VEND_SLOT_COUNT];
                for (int i = 0; i < itemCosts.length; i++) itemCosts[i] = tile.getItemCost(i);

                worldIn.setBlockState(pos.down(), worldIn.getBlockState(pos.down()).withProperty(StateHandler.TWOTALL, StateHandler.EnumTwoBlock.ONE));
                ((TileVending) worldIn.getTileEntity(pos.down())).setField(tile.FIELD_TWOBLOCK, 0);

                //Re adding important variables to below tile
                tile = (TileVending) worldIn.getTileEntity(pos.down());
                tile.setField(tile.FIELD_TWOBLOCK, 0);
                tile.setInputStackHandler(inputStack);
                tile.setVendStackHandler(vendStack);
                tile.setBufferStackHandler(buffStack);
                tile.setField(tile.FIELD_BANK, bank);
                tile.setField(tile.FIELD_PROFIT, profit);
                tile.setOwner(owner);
                tile.setField(tile.FIELD_LOCKED, locked ? 1 : 0);
                tile.setField(tile.FIELD_INFINITE, infinite ? 1 : 0);
                for (int i = 0; i < itemCosts.length; i++) tile.setItemCost(itemCosts[i], i);

                tile.dropTopItems();
            }
        } else if (state.getValue(StateHandler.TWOTALL) == StateHandler.EnumTwoBlock.TWOBOTTOM) {   //BOTTOM BLOCK
            if(getTile(worldIn, pos).getPlayerUsing() != null) getTile(worldIn, pos).getPlayerUsing().closeScreen();
            worldIn.setBlockToAir(pos.up());
            worldIn.spawnEntity(new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ModBlocks.blockVending)));
        }

        if (state.getValue(StateHandler.TWOTALL) != StateHandler.EnumTwoBlock.TWOTOP) {
            TileVending tile = getTile(worldIn, pos);
            tile.setField(tile.FIELD_MODE, 1);
            tile.outChange();
            tile.setField(tile.FIELD_MODE, 0);
            tile.outChange();
            tile.dropItems();
        }
        super.breakBlock(worldIn, pos, state);
    }

    //<editor-fold desc="Block States--------------------------------------------------------------------------------------------------------">
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, StateHandler.FACING, StateHandler.TWOTALL);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(StateHandler.FACING, EnumFacing.getHorizontal(meta % 4))
                .withProperty(StateHandler.TWOTALL, StateHandler.EnumTwoBlock.class.getEnumConstants()[meta / 4]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return (state.getValue(StateHandler.FACING).getHorizontalIndex() + (state.getValue(StateHandler.TWOTALL).ordinal() * 4));
    }

    //</editor-fold>

    //<editor-fold desc="Rendering-----------------------------------------------------------------------------------------------------------">
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch (getMetaFromState(state)% 4) {
                default:
                case 2: return BOUND_BOX_N;
                case 3: return BOUND_BOX_E;
                case 0: return BOUND_BOX_S;
                case 1: return BOUND_BOX_W;
        }
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        switch (getMetaFromState(blockState)% 4) {
                default:
                case 2: return BOUND_BOX_N;
                case 3: return BOUND_BOX_E;
                case 0: return BOUND_BOX_S;
                case 1: return BOUND_BOX_W;
        }
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.TRANSLUCENT || layer == BlockRenderLayer.SOLID;
    }

    //</editor-fold>
}
