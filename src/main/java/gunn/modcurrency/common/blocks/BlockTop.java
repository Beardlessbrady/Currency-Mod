package gunn.modcurrency.common.blocks;

import gunn.modcurrency.api.ModTile;
import gunn.modcurrency.common.core.handler.StateHandler;
import gunn.modcurrency.common.items.ModItems;
import gunn.modcurrency.common.tiles.TileSeller;
import gunn.modcurrency.common.tiles.TileVendor;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * Distributed with the Currency-Mod for Minecraft.
 * Copyright (C) 2016  Brady Gunn
 *
 * File Created on 2016-11-20.
 */
public class BlockTop extends Block{
    private static final AxisAlignedBB BOUND_BOX_N = new AxisAlignedBB(0.03125, 0, 0.28125, 0.96875, 1, 1);
    private static final AxisAlignedBB BOUND_BOX_E = new AxisAlignedBB(0.71875, 0, 0.03125, 0, 1, 0.96875);
    private static final AxisAlignedBB BOUND_BOX_S = new AxisAlignedBB(0.03125, 0, 0.71875, 0.96875, 1, 0);
    private static final AxisAlignedBB BOUND_BOX_W = new AxisAlignedBB(0.28125, 0, 0.03125, 1, 1, 0.96875);
    
    public BlockTop() {
        super(Material.ROCK);
        setRegistryName("blocktop");
        setUnlocalizedName(this.getRegistryName().toString());

        setHardness(3.0F);
        setSoundType(SoundType.METAL);

        GameRegistry.register(this);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        if(source.getTileEntity(pos.down()) != null){
            Integer face = ((ModTile) source.getTileEntity(pos.down())).getField(7);
            double y = (source.getTileEntity(pos.down()) instanceof TileVendor) ? 0 : 0.0625;
            AxisAlignedBB box;
            AxisAlignedBB newBox;

            switch (face) {
                default:
                case 0: box = BOUND_BOX_N;
                break;
                case 1: box = BOUND_BOX_E;
                break;
                case 2: box = BOUND_BOX_S;
                break;
                case 3: box = BOUND_BOX_W;
            }

            newBox = new AxisAlignedBB(box.minX, box.minY, box.minZ, box.maxX, box.maxY - y, box.maxZ);
            return newBox;
        }
        return super.getBoundingBox(state, source, pos);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        if(worldIn.getTileEntity(pos.down()) != null){
            Integer face = ((ModTile) worldIn.getTileEntity(pos.down())).getField(7);
            double y = (worldIn.getTileEntity(pos.down()) instanceof TileVendor) ? 0 : 0.0625;
            AxisAlignedBB box;
            AxisAlignedBB newBox;

            switch (face) {
                default:
                case 0: box = BOUND_BOX_N;
                    break;
                case 1: box = BOUND_BOX_E;
                    break;
                case 2: box = BOUND_BOX_S;
                    break;
                case 3: box = BOUND_BOX_W;
            }

            newBox = new AxisAlignedBB(box.minX, box.minY, box.minZ, box.maxX, box.maxY - y, box.maxZ);
            return newBox;
        }
        return super.getCollisionBoundingBox(blockState, worldIn, pos);
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    public int whatBlock(World world, BlockPos pos){
        if(world.getBlockState(pos.down()).getBlock().equals(ModBlocks.blockVendor)) return 0;
        if(world.getBlockState(pos.down()).getBlock().equals(ModBlocks.blockSeller)) return 1;
        return -1;
    }

    public int whatBlock(IBlockAccess world, BlockPos pos){
        if(world.getBlockState(pos.down()).getBlock().equals(ModBlocks.blockVendor)) return 0;
        if(world.getBlockState(pos.down()).getBlock().equals(ModBlocks.blockSeller)) return 1;
        return -1;
    }

    public ModTile getTile(World world, BlockPos pos) {
        return (ModTile) world.getTileEntity(pos.down());
    }

    public ModTile getTile(IBlockAccess world, BlockPos pos) {
        return (ModTile) world.getTileEntity(pos.down());
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing heldItem, float side, float hitX, float hitY) {
        if (world.getBlockState(pos.down()).getBlock() == ModBlocks.blockVendor || world.getBlockState(pos.down()).getBlock() == ModBlocks.blockSeller) {
            if (getTile(world, pos).getPlayerUsing() == null) {      //Client and Server
                getTile(world, pos).setField(5, player.isCreative() ? 1 : 0);

                if (player.getHeldItemMainhand() != ItemStack.EMPTY && !world.isRemote) {      //Just Server
                    if (player.getHeldItemMainhand().getItem() == Items.DYE) {
                        //<editor-fold desc="Saving Tile Variables">
                        ModTile tile = getTile(world, pos);

                        ItemStackHandler inputStackHandler = tile.getInputHandler();
                        ItemStackHandler vendStackHandler = tile.getVendHandler();
                        ItemStackHandler buffStackHandler = tile.getBufferHandler();

                        int bank = tile.getField(0);
                        int face = tile.getField(7);
                        int four = tile.getField(4);
                        int locked = tile.getField(1);
                        int mode = tile.getField(2);
                        int infinite = tile.getField(6);
                        String owner = tile.getOwner();
                        int[] itemCosts = tile.getAllItemCosts();
                        //</editor-fold>

                        world.setBlockState(pos, state.withProperty(StateHandler.COLOR, EnumDyeColor.byDyeDamage(player.getHeldItemMainhand().getItemDamage())), 3);
                        world.setBlockState(pos.down(), world.getBlockState(pos.down()).withProperty(StateHandler.COLOR, EnumDyeColor.byDyeDamage(player.getHeldItemMainhand().getItemDamage())), 3);

                        //<editor-fold desc="Setting Tile Variables">
                        tile = getTile(world, pos);

                        tile.setStackHandlers(inputStackHandler, buffStackHandler, vendStackHandler);
                        tile.setField(0, bank);
                        tile.setField(7, face);
                        tile.setField(4, four);
                        tile.setField(1, locked);
                        tile.setField(2, mode);
                        tile.setField(6, infinite);
                        tile.setOwner(owner);
                        tile.setAllItemCosts(itemCosts);
                        //</editor-fold>

                        if (!player.isCreative()) player.getHeldItemMainhand().shrink(1);
                        return true;
                    }
                } else if (player.getHeldItemMainhand() != ItemStack.EMPTY && world.isRemote) return true;

                if ((player.isSneaking() && player.getUniqueID().toString().equals(getTile(world, pos).getOwner())) || (player.isSneaking() && player.isCreative())) {      //Client and Server
                    if (getTile(world, pos).getField(2) == 1) {
                        getTile(world, pos).setField(2, 0);
                    } else {
                        getTile(world, pos).setField(2, 1);
                    }
                    getTile(world, pos).getWorld().notifyBlockUpdate(getTile(world, pos).getPos(), getTile(world, pos).getBlockType().getDefaultState(), getTile(world, pos).getBlockType().getDefaultState(), 3);
                    return true;
                }

                if (!world.isRemote) {    //Just Server
                    if (whatBlock(world, pos) == 0) {
                        TileVendor te = (TileVendor) getTile(world, pos);
                        te.openGui(player, world, pos.down());
                    } else if (whatBlock(world, pos) == 1) {
                        TileSeller te = (TileSeller) getTile(world, pos);
                        te.openGui(player, world, pos.down());
                    }
                    return true;
                } else return true;
            }
        }
        return false;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if(!worldIn.isAirBlock(pos.down())) {
            ItemStack drop = new ItemStack(Item.getItemFromBlock(ModBlocks.blockVendor));
            if (worldIn.getBlockState(pos.down()).getBlock().equals(ModBlocks.blockSeller)) drop = new ItemStack(Item.getItemFromBlock(ModBlocks.blockSeller));
            drop.setItemDamage(worldIn.getBlockState(pos.down()).getValue(StateHandler.COLOR).getDyeDamage());
            spawnAsEntity(worldIn,pos,drop);
        }

        super.breakBlock(worldIn, pos, state);
        worldIn.setBlockToAir(pos.down());
    }

    //<editor-fold desc="Block States--------------------------------------------------------------------------------------------------------">
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {StateHandler.TOP,StateHandler.COLOR,StateHandler.FACING});
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(StateHandler.TOP).ordinal();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(StateHandler.TOP,StateHandler.EnumTopTypes.class.getEnumConstants()[meta]);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        if (worldIn.getBlockState(pos.down()).getBlock() == ModBlocks.blockVendor || worldIn.getBlockState(pos.down()).getBlock() == ModBlocks.blockSeller) {
            int faceData = 0;

            ModTile tile = (ModTile) worldIn.getTileEntity(pos.down());
            faceData = tile.getField(7);

            EnumFacing face = EnumFacing.NORTH;
            switch (faceData) {
                case 1:
                    face = EnumFacing.EAST;
                    break;
                case 2:
                    face = EnumFacing.SOUTH;
                    break;
                case 3:
                    face = EnumFacing.WEST;
                    break;
            }

            StateHandler.EnumTopTypes type = StateHandler.EnumTopTypes.VENDOR;
            switch (whatBlock(worldIn, pos)) {
                case 0:
                    if (getTile(worldIn, pos).getField(2) == 1) type = StateHandler.EnumTopTypes.VENDOROPEN;
                    break;
                case 1:
                    if (getTile(worldIn, pos).getField(2) == 1) {
                        type = StateHandler.EnumTopTypes.SELLEROPEN;
                    } else {
                        type = StateHandler.EnumTopTypes.SELLER;
                    }
                    break;
            }


            return getDefaultState().withProperty(StateHandler.FACING, face)
                    .withProperty(StateHandler.COLOR, worldIn.getBlockState(pos.down()).getValue(StateHandler.COLOR))
                    .withProperty(StateHandler.TOP, type);
        }
        return getDefaultState();
    }
    //</editor-fold>
}
