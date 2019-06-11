package beardlessbrady.modcurrency.block.vending;

import beardlessbrady.modcurrency.block.EconomyBlockBase;
import beardlessbrady.modcurrency.block.TileEconomyBase;
import beardlessbrady.modcurrency.handler.StateHandler;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;


/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-02-10
 */

public class BlockVending extends EconomyBlockBase {

    public BlockVending() {
        super("blockvending", TileVending.class);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileVending tile = (TileVending) getTile(worldIn, pos);
        if (TileEconomyBase.EMPTYID.equals(getTile(worldIn, pos).getPlayerUsing())) {
            if (playerIn.isSneaking() && tile.getOwner().equals(playerIn.getUniqueID())) {
                tile.setField(TileEconomyBase.FIELD_MODE, 1);
            } else {
                tile.setField(TileEconomyBase.FIELD_MODE, 0);
            }

            if (!worldIn.isRemote) {
                ((TileVending) getTile(worldIn, pos)).openGui(playerIn, worldIn, pos);
                return true;
            }
        }
        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.withProperty(StateHandler.FACING, placer.getHorizontalFacing().getOpposite()));

        worldIn.setBlockState(pos, state.withProperty(StateHandler.TWOTALL, StateHandler.EnumTwoBlock.TWOBOTTOM)
                .withProperty(StateHandler.FACING, placer.getHorizontalFacing().getOpposite()));

        worldIn.setBlockState(pos.up(), state.withProperty(StateHandler.TWOTALL, StateHandler.EnumTwoBlock.TWOTOP)
                .withProperty(StateHandler.FACING, placer.getHorizontalFacing().getOpposite()));

        getTile(worldIn, pos).setOwner(placer.getUniqueID());
        getTile(worldIn, pos).markDirty();
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if(!worldIn.isRemote) {
            TileVending tile = (TileVending) getTile(worldIn, pos, state);

            tile.setField(TileEconomyBase.FIELD_MODE, 0);
            tile.outChange(true);

            tile.setField(TileEconomyBase.FIELD_MODE, 1);
            tile.outChange(true);

            tile.dropInventory();

            if (state.getValue(StateHandler.TWOTALL) == StateHandler.EnumTwoBlock.TWOTOP) {     //TOP BLOCK
                worldIn.setBlockToAir(pos.down());
            } else if (state.getValue(StateHandler.TWOTALL) == StateHandler.EnumTwoBlock.TWOBOTTOM) { //BOTTOM BLOCK
                worldIn.setBlockToAir(pos.up());
            }
            super.breakBlock(worldIn, pos, state);
        }
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
       if(state.getValue(StateHandler.TWOTALL) != StateHandler.EnumTwoBlock.TWOTOP)
           return super.createTileEntity(world, state);

       return null;
    }

    @Override
    public TileEconomyBase getTile(World world, BlockPos pos) {
            if (world.getBlockState(pos).getValue(StateHandler.TWOTALL) == StateHandler.EnumTwoBlock.TWOTOP){
                if (world.getTileEntity(pos.down()) instanceof TileVending)
                    return (TileVending) world.getTileEntity(pos.down());
            }else{
                if (world.getTileEntity(pos) instanceof TileVending)
                    return (TileVending) world.getTileEntity(pos);
            }
        return null;
    }

    public TileEconomyBase getTile(World world, BlockPos pos, IBlockState state) {
        if (state.getValue(StateHandler.TWOTALL) == StateHandler.EnumTwoBlock.TWOTOP){
            if (world.getTileEntity(pos.down()) instanceof TileVending)
                return (TileVending) world.getTileEntity(pos.down());
        }else{
            if (world.getTileEntity(pos) instanceof TileVending)
                return (TileVending) world.getTileEntity(pos);
        }
        return null;
    }

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

    //<editor-fold desc="Rendering-----------------------------------------------------------------------------------------------------------">
    @Override
    @SideOnly(Side.CLIENT)
    public void registerModel() {
        super.registerModel();
        ClientRegistry.bindTileEntitySpecialRenderer(TileVending.class, new RenderVending());
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
