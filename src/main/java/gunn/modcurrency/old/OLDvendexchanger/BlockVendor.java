package gunn.modcurrency.old.OLDvendexchanger;

import gunn.modcurrency.mod.ModCurrency;
import gunn.modcurrency.mod.block.ModBlocks;
import gunn.modcurrency.mod.handler.StateHandler;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
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
 * File Created on 2016-10-30.
 */
public class BlockVendor extends Block implements ITileEntityProvider{
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileVendor();
    }

    public TileVendor getTile(World world, BlockPos pos) {
        return (TileVendor) world.getTileEntity(pos);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing heldItem, float side, float hitX, float hitY) {
        if(getTile(world, pos).getPlayerUsing() == null) {      //Client and Server
            getTile(world, pos).setField(5, player.isCreative() ? 1 : 0);
            if (player.getHeldItemMainhand() != ItemStack.EMPTY && !world.isRemote) {      //Just Server
                if (player.getHeldItemMainhand() != ItemStack.EMPTY && world.isRemote) return true;
            }

            if ((player.isSneaking() && player.getUniqueID().toString().equals(getTile(world, pos).getOwner())) || (player.isSneaking() && player.isCreative())) {      //Client and Server

                if (getTile(world, pos).getField(2) == 1) {   //If True
                    getTile(world, pos).setField(2, 0);
                    world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundCategory.BLOCKS, 0.5F, -9.0F);
                } else {
                    getTile(world, pos).setField(2, 1);
                    world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundCategory.BLOCKS, 0.5F, -9.0F);
                }
                return true;
            }

            if(!world.isRemote){    //Just Server
                getTile(world, pos).openGui(player, world, pos);
                return true;
            } else return true;
        }
        return false;
    }



    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.up()).getBlock().isReplaceable(worldIn,pos.up());
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileVendor te = getTile(worldIn, pos);
        te.setField(2,1);
        te.outChange();
        te.setField(2,0);
        te.outChange();
        te.dropItems();

        super.breakBlock(worldIn, pos, state);
        worldIn.setBlockToAir(pos.up());
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }
}
