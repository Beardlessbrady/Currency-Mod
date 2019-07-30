package beardlessbrady.modcurrency.block.tradein;

import beardlessbrady.modcurrency.block.EconomyBlockBase;
import beardlessbrady.modcurrency.handler.StateHandler;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-07-09
 */

public class BlockTradein extends EconomyBlockBase {

    public BlockTradein() {
        super("blocktradein", TileTradein.class);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        //Creates the top and bottom part of the block (since this block is 2 blocks on top of each other)
        worldIn.setBlockState(pos, state.withProperty(StateHandler.TWOTALL, StateHandler.EnumTwoBlock.TWOBOTTOM)
                .withProperty(StateHandler.FACING, placer.getHorizontalFacing().getOpposite()));

        worldIn.setBlockState(pos.up(), state.withProperty(StateHandler.TWOTALL, StateHandler.EnumTwoBlock.TWOTOP)
                .withProperty(StateHandler.FACING, placer.getHorizontalFacing().getOpposite()));

        //Sets owner to the placer
        getTile(worldIn, pos).setOwner(placer.getUniqueID());
        getTile(worldIn, pos).markDirty();
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

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileTradein tile;

        if(StateHandler.EnumTwoBlock.class.getEnumConstants()[getMetaFromState(state) / 4] == StateHandler.EnumTwoBlock.TWOTOP){
            tile = (TileTradein) worldIn.getTileEntity(pos.down());
        }else{
            tile = (TileTradein) worldIn.getTileEntity(pos);
        }

        return this.getDefaultState().withProperty(StateHandler.FACING, EnumFacing.getHorizontal(getMetaFromState(state) % 4))
                .withProperty(StateHandler.TWOTALL, StateHandler.EnumTwoBlock.class.getEnumConstants()[getMetaFromState(state) / 4]);
    }

    //<editor-fold desc="Rendering-----------------------------------------------------------------------------------------------------------">
    @Override
    @SideOnly(Side.CLIENT)
    public void registerModel() {
        super.registerModel();
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    //</editor-fold>

}
