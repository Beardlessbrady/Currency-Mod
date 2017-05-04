package gunn.modcurrency.mod.block;

import gunn.modcurrency.mod.ModCurrency;
import gunn.modcurrency.mod.core.handler.StateHandler;
import gunn.modcurrency.mod.tile.TileSeller;
import gunn.modcurrency.mod.tile.abAdvSell;
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
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

/**
 * Distributed with the Currency-Mod for Minecraft.
 * Copyright (C) 2016  Brady Gunn
 *
 * File Created on 2016-12-19
 */
public class BlockSeller extends Block implements ITileEntityProvider{
    private static final AxisAlignedBB BOUND_BOX_N = new AxisAlignedBB(0.03125, 0, 0.28125, 0.96875, 1, 1);
    private static final AxisAlignedBB BOUND_BOX_E = new AxisAlignedBB(0.71875, 0, 0.03125, 0, 1, 0.96875);
    private static final AxisAlignedBB BOUND_BOX_S = new AxisAlignedBB(0.03125, 0, 0.71875, 0.96875, 1, 0);
    private static final AxisAlignedBB BOUND_BOX_W = new AxisAlignedBB(0.28125, 0, 0.03125, 1, 1, 0.96875);

    public BlockSeller() {
        super(Material.ROCK);
        setRegistryName("blockseller");
        setUnlocalizedName(this.getRegistryName().toString());

        setHardness(3.0F);
        setCreativeTab(ModCurrency.tabCurrency);
        setSoundType(SoundType.METAL);

        GameRegistry.register(this);
        GameRegistry.register(new ItemBlock(this), getRegistryName());
        GameRegistry.registerTileEntity(TileSeller.class, ModCurrency.MODID + "_teseller");
    }

    public void recipe(){
        ItemStack basic = new ItemStack(Item.getItemFromBlock(this));
        basic.setItemDamage(0);

        GameRegistry.addRecipe(basic,
                "ABA",
                "ACA",
                "ADA",
                'A', Items.IRON_INGOT,
                'B', Items.REPEATER,
                'C', Item.getItemFromBlock(Blocks.CHEST),
                'D', Items.IRON_DOOR);
    }

    public void initModel(){
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "facing=north,item=true"));
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileSeller();
    }

    public TileSeller getTile(World world, BlockPos pos) {
        return (TileSeller) world.getTileEntity(pos);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing heldItem, float side, float hitX, float hitY) {
        if (getTile(world, pos).getPlayerUsing() == null) {      //Client and Server
            getTile(world, pos).setField(5, player.isCreative() ? 1 : 0);

            if (player.getHeldItemMainhand() != ItemStack.EMPTY && !world.isRemote) {       //Just Server
                if (player.getHeldItemMainhand().getItem() == Items.DYE) {
                    //<editor-fold desc="Saving Tile Variables">
                    abAdvSell tile = getTile(world, pos);

                    ItemStackHandler inputStackHandler = tile.getInputHandler();
                    ItemStackHandler vendStackHandler = tile.getVendHandler();
                    ItemStackHandler buffStackHandler = tile.getBufferHandler();

                    int bank = tile.getField(0);
                    int face = tile.getField(7);
                    int cashRegister = tile.getField(4);
                    int locked = tile.getField(1);
                    int mode = tile.getField(2);
                    int infinite = tile.getField(6);
                    String owner = tile.getOwner();
                    int[] itemCosts = tile.getAllItemCosts();
                    //</editor-fold>

                    //<editor-fold desc="Setting Tile Variables">
                    tile = getTile(world, pos);

                    tile.setStackHandlers(inputStackHandler, buffStackHandler, vendStackHandler);
                    tile.setField(0, bank);
                    tile.setField(7, face);
                    tile.setField(4, cashRegister);
                    tile.setField(1, locked);
                    tile.setField(2, mode);
                    tile.setField(6, infinite);
                    tile.setOwner(owner);
                    tile.setAllItemCosts(itemCosts);
                    //</editor-fold>

                    if (!player.isCreative()) player.getHeldItemMainhand().shrink(1);
                    return true;
                } else if (player.getHeldItemMainhand() != ItemStack.EMPTY && world.isRemote) return true;
            }

            if ((player.isSneaking() && player.getUniqueID().toString().equals(getTile(world, pos).getOwner())) || (player.isSneaking() && player.isCreative())) {         //Client and Server
                if (getTile(world, pos).getField(2) == 1) {   //If True
                    getTile(world, pos).setField(2, 0);
                    world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundCategory.BLOCKS, 0.5F, -9.0F);
                } else {
                    getTile(world, pos).setField(2, 1);
                    world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundCategory.BLOCKS, 0.5F, -9.0F);
                }
                getTile(world, pos).getWorld().notifyBlockUpdate(getTile(world, pos).getPos(), getTile(world, pos).getBlockType().getDefaultState(), getTile(world, pos).getBlockType().getDefaultState(), 3);
                return true;
            }

            if (!world.isRemote) {    //Just Server
                getTile(world, pos).openGui(player, world, pos);
                return true;
            } else return true;
        }
        return false;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        EnumFacing face = EnumFacing.NORTH;
        switch(placer.getHorizontalFacing().getOpposite()){
            case NORTH: break;
            case EAST: face = EnumFacing.EAST;
                break;
            case SOUTH: face = EnumFacing.SOUTH;
                break;
            case WEST: face = EnumFacing.WEST;
                break;
        }
        worldIn.setBlockState(pos, state.withProperty(StateHandler.FACING, face));

        if(placer instanceof EntityPlayer) getTile(worldIn, pos).setOwner((placer).getUniqueID().toString());

        if(placer instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) placer;
            String playerName = player.getUniqueID().toString();
            getTile(worldIn, pos).setOwner(playerName);
        }
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.up()).getBlock().isReplaceable(worldIn,pos.up());
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileSeller te = getTile(worldIn, pos);
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

    //<editor-fold desc="Block States--------------------------------------------------------------------------------------------------------">
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {StateHandler.FACING, StateHandler.ITEM});
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(StateHandler.FACING, EnumFacing.getHorizontal(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return (state.getValue(StateHandler.FACING).getHorizontalIndex());
    }

    //</editor-fold>

    //<editor-fold desc="Render--------------------------------------------------------------------------------------------------------------">
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        if(source.getTileEntity(pos) != null){
            Integer face = ((abAdvSell) source.getTileEntity(pos)).getField(7);
            switch (face) {
                default:
                case 0: return BOUND_BOX_N;
                case 1: return BOUND_BOX_E;
                case 2: return BOUND_BOX_S;
                case 3: return BOUND_BOX_W;
            }
        }
        return super.getBoundingBox(state, source, pos);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        if(worldIn.getTileEntity(pos) != null){
            Integer face = ((abAdvSell) worldIn.getTileEntity(pos)).getField(7);
            switch (face) {
                default:
                case 0: return BOUND_BOX_N;
                case 1: return BOUND_BOX_E;
                case 2: return BOUND_BOX_S;
                case 3: return BOUND_BOX_W;
            }
        }
        return super.getCollisionBoundingBox(blockState, worldIn, pos);
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
