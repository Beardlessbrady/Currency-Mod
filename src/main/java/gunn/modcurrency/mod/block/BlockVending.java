package gunn.modcurrency.mod.block;

import gunn.modcurrency.mod.ModCurrency;
import gunn.modcurrency.mod.handler.ItemHandlerVendor;
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

        GameRegistry.register(this);
        GameRegistry.register(new ItemBlock(this), getRegistryName());
        GameRegistry.registerTileEntity(TileVending.class, ModCurrency.MODID + "_tevending");
    }

    void recipe() {
        ItemStack basic = new ItemStack(Item.getItemFromBlock(this));
        basic.setItemDamage(0);

        GameRegistry.addRecipe(basic,
                "ABA", "ACA", "ADA",
                'A', Items.IRON_INGOT,
                'B', Items.COMPARATOR,
                'C', Item.getItemFromBlock(Blocks.CHEST),
                'D', Items.IRON_DOOR);
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
        if (getTile(worldIn, pos).getPlayerUsing() == null) {
            getTile(worldIn, pos).setField(5, playerIn.isCreative() ? 1 : 0);
            if (!worldIn.isRemote) {
                getTile(worldIn, pos).openGui(playerIn, worldIn, pos);
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
                    int bank = tile.getField(0);
                    int profit = tile.getField(4);
                    String owner = tile.getOwner();
                    boolean locked = tile.getField(1) == 1;
                    boolean infinite = tile.getField(6) == 1;
                    int[] itemCosts = new int[tile.VEND_SLOT_COUNT];
                    for (int i = 0; i < itemCosts.length; i++) itemCosts[i] = tile.getItemCost(i);

                    worldIn.setBlockState(pos, state.withProperty(StateHandler.TWOTALL, StateHandler.EnumTwoBlock.TWOTOP)
                            .withProperty(StateHandler.FACING, placer.getHorizontalFacing().getOpposite()));

                    worldIn.setBlockState(pos.down(), state.withProperty(StateHandler.TWOTALL, StateHandler.EnumTwoBlock.TWOBOTTOM)
                            .withProperty(StateHandler.FACING, placer.getHorizontalFacing().getOpposite()));

                    //Re adding important variables to below tile
                    tile = (TileVending) worldIn.getTileEntity(pos.down());
                    tile.setField(7, 1);
                    tile.setInputStackHandler(inputStack);
                    tile.setVendStackHandler(vendStack);
                    tile.setBufferStackHandler(buffStack);
                    tile.setField(0, bank);
                    tile.setField(4, profit);
                    tile.setOwner(owner);
                    tile.setField(1, locked ? 1 : 0);
                    tile.setField(6, infinite ? 1 : 0);
                    for (int i = 0; i < itemCosts.length; i++) tile.setItemCost(itemCosts[i], i);
                }
            }
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (state.getValue(StateHandler.TWOTALL) == StateHandler.EnumTwoBlock.TWOTOP) {
            if(worldIn.getBlockState(pos.down()).getBlock() == ModBlocks.blockVending){
                if(getTile(worldIn, pos.down()).getPlayerUsing() != null) getTile(worldIn, pos.down()).getPlayerUsing().closeScreen();
                //Backing up important tile variables from below tile
                TileVending tile = (TileVending) worldIn.getTileEntity(pos.down());
                ItemStackHandler inputStack = tile.getInputStackHandler();
                ItemHandlerVendor vendStack = tile.getVendStackHandler();
                ItemStackHandler buffStack = tile.getBufferStackHandler();
                int bank = tile.getField(0);
                int profit = tile.getField(4);
                String owner = tile.getOwner();
                boolean locked = tile.getField(1) == 1;
                boolean infinite = tile.getField(6) == 1;
                int[] itemCosts = new int[tile.VEND_SLOT_COUNT];
                for (int i = 0; i < itemCosts.length; i++) itemCosts[i] = tile.getItemCost(i);

                worldIn.setBlockState(pos.down(), worldIn.getBlockState(pos.down()).withProperty(StateHandler.TWOTALL, StateHandler.EnumTwoBlock.ONE));
                ((TileVending) worldIn.getTileEntity(pos.down())).setField(7, 0);

                //Re adding important variables to below tile
                tile = (TileVending) worldIn.getTileEntity(pos.down());
                tile.setField(7, 0);
                tile.setInputStackHandler(inputStack);
                tile.setVendStackHandler(vendStack);
                tile.setBufferStackHandler(buffStack);
                tile.setField(0, bank);
                tile.setField(4, profit);
                tile.setOwner(owner);
                tile.setField(1, locked ? 1 : 0);
                tile.setField(6, infinite ? 1 : 0);
                for (int i = 0; i < itemCosts.length; i++) tile.setItemCost(itemCosts[i], i);
            }
        } else if (state.getValue(StateHandler.TWOTALL) == StateHandler.EnumTwoBlock.TWOBOTTOM) {
            if(getTile(worldIn, pos).getPlayerUsing() != null) getTile(worldIn, pos).getPlayerUsing().closeScreen();
            worldIn.setBlockState(pos.up(), worldIn.getBlockState(pos.up()).withProperty(StateHandler.TWOTALL, StateHandler.EnumTwoBlock.ONE));
        }

        if (state.getValue(StateHandler.TWOTALL) != StateHandler.EnumTwoBlock.TWOTOP) {
            TileVending te = getTile(worldIn, pos);
            te.setField(2, 1);
            te.outChange();
            te.setField(2, 0);
            te.outChange();
            te.dropItems();
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
        switch (getMetaFromState(state)) {
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
        switch (getMetaFromState(blockState)) {
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
