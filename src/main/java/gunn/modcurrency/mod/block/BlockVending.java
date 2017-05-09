package gunn.modcurrency.mod.block;

import gunn.modcurrency.mod.ModCurrency;
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

import javax.annotation.Nullable;

/**
 * Distributed with the Currency-Mod for Minecraft.
 * Copyright (C) 2016  Brady Gunn
 *
 * File Created on 2017-05-05
 */
public class BlockVending extends Block implements ITileEntityProvider{
    private static final AxisAlignedBB BOUND_BOX_N = new AxisAlignedBB(0.03125, 0, 0.28125, 0.96875, 1, 1);
    private static final AxisAlignedBB BOUND_BOX_E = new AxisAlignedBB(0.71875, 0, 0.03125, 0, 1, 0.96875);
    private static final AxisAlignedBB BOUND_BOX_S = new AxisAlignedBB(0.03125, 0, 0.71875, 0.96875, 1, 0);
    private static final AxisAlignedBB BOUND_BOX_W = new AxisAlignedBB(0.28125, 0, 0.03125, 1, 1, 0.96875);

    public BlockVending() {
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

    public void recipe(){
        ItemStack basic = new ItemStack(Item.getItemFromBlock(this));
        basic.setItemDamage(0);

        GameRegistry.addRecipe(basic,
                "ABA", "ACA", "ADA",
                'A', Items.IRON_INGOT,
                'B', Items.COMPARATOR,
                'C', Item.getItemFromBlock(Blocks.CHEST),
                'D', Items.IRON_DOOR);
    }

    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileVending();

    }

    public TileVending getTile(World world, BlockPos pos) {
        return (TileVending) world.getTileEntity(pos);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(getTile(worldIn, pos).getPlayerUsing() == null) {
            getTile(worldIn, pos).setField(5, playerIn.isCreative() ? 1 : 0);

            if(!worldIn.isRemote){
                getTile(worldIn, pos).openGui(playerIn, worldIn, pos);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.withProperty(StateHandler.FACING, placer.getHorizontalFacing().getOpposite()));
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileVending te = getTile(worldIn, pos);
        te.setField(2,1);
        te.outChange();
        te.setField(2,0);
        te.outChange();
        te.dropItems();

        super.breakBlock(worldIn, pos, state);
        worldIn.setBlockToAir(pos.up());
    }

    //<editor-fold desc="Block States--------------------------------------------------------------------------------------------------------">
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, StateHandler.FACING, StateHandler.TWOTALL);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(StateHandler.FACING, EnumFacing.getHorizontal(meta))
                .withProperty(StateHandler.TWOTALL, StateHandler.EnumTwoBlock.one);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return (state.getValue(StateHandler.FACING).getHorizontalIndex());
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
